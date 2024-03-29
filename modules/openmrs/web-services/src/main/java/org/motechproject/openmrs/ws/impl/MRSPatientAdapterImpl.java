package org.motechproject.openmrs.ws.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.exception.PatientNotFoundException;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.model.OpenMRSPatient;
import org.motechproject.openmrs.model.OpenMRSPerson;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.PatientResource;
import org.motechproject.openmrs.ws.resource.model.Identifier;
import org.motechproject.openmrs.ws.resource.model.IdentifierType;
import org.motechproject.openmrs.ws.resource.model.Location;
import org.motechproject.openmrs.ws.resource.model.Patient;
import org.motechproject.openmrs.ws.resource.model.PatientListResult;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component("patientAdapter")
public class MRSPatientAdapterImpl implements MRSPatientAdapter {

    private static Logger logger = LoggerFactory.getLogger(MRSPatientAdapterImpl.class);

    private final PatientResource patientResource;
    private final MRSPersonAdapterImpl personAdapter;
    private final MRSFacilityAdapter facilityAdapter;
    private final EventRelay eventRelay;

    @Autowired
    public MRSPatientAdapterImpl(PatientResource patientResource, MRSPersonAdapterImpl personAdapter,
            MRSFacilityAdapter facilityAdapter, EventRelay eventRelay) {
        this.patientResource = patientResource;
        this.personAdapter = personAdapter;
        this.facilityAdapter = facilityAdapter;
        this.eventRelay = eventRelay;
    }

    @Override
    public Integer getAgeOfPatientByMotechId(String arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MRSPatient getPatientByMotechId(String motechId) {
        Validate.notEmpty(motechId, "Motech Id cannot be empty");

        PatientListResult patientList = null;
        try {
            patientList = patientResource.queryForPatient(motechId);
        } catch (HttpException e) {
            logger.error("Failed search for patient by MoTeCH Id: " + motechId);
            return null;
        }

        if (patientList.getResults().size() == 0) {
            return null;
        } else if (patientList.getResults().size() > 1) {
            logger.warn("Search for patient by id returned more than 1 result");
        }

        return getPatient(patientList.getResults().get(0).getUuid());
    }

    @Override
    public MRSPatient getPatient(String patientId) {
        Validate.notEmpty(patientId, "Patient Id cannot be empty");

        Patient patient = null;
        try {
            patient = patientResource.getPatientById(patientId);
        } catch (HttpException e) {
            logger.error("Failed to get patient by id: " + patientId);
            return null;
        }

        String motechIdentifierUuid = null;
        try {
            motechIdentifierUuid = patientResource.getMotechPatientIdentifierUuid();
        } catch (HttpException e) {
            logger.error("There was an exception retrieving the MoTeCH Identifier Type UUID");
            return null;
        }

        Identifier identifier = patient.getIdentifierByIdentifierType(motechIdentifierUuid);
        if (identifier == null) {
            logger.warn("No MoTeCH Id found on Patient with id: " + patient.getUuid());
        }

        // since OpenMRS 1.9, patient identifiers no longer require an explicit
        // location
        // this means the identifier's location could be null
        // this is a guard against this situation
        if (identifier != null && identifier.getLocation() != null) {
            String facililtyUuid = identifier.getLocation().getUuid();
            return convertToMrsPatient(patient, identifier.getIdentifier(), facilityAdapter.getFacility(facililtyUuid));
        } else {
            String motechId = null;
            if (identifier != null) {
                motechId = identifier.getIdentifier();
            }

            return convertToMrsPatient(patient, motechId, null);
        }
    }

    private MRSPatient convertToMrsPatient(Patient patient, String identifier, MRSFacility facility) {
        MRSPatient converted = new OpenMRSPatient(patient.getUuid(), identifier, ConverterUtils.convertToMrsPerson(patient
                .getPerson()), facility);
        return converted;
    }

    @Override
    public OpenMRSPatient savePatient(MRSPatient patient) {
        validatePatientBeforeSave(patient);
        OpenMRSPatient openMRSPatient = ConverterUtils.createPatient(patient);

        OpenMRSPerson savedPerson = personAdapter.addPerson(patient.getPerson());

        Patient converted = fromMrsPatient(openMRSPatient, savedPerson);
        if (converted == null) {
            return null;
        }

        OpenMRSPatient savedPatient;
        try {
            patientResource.createPatient(converted);
            savedPatient =  new OpenMRSPatient(openMRSPatient.getPatientId(), openMRSPatient.getMotechId(), savedPerson, openMRSPatient.getFacility());
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_PATIENT_SUBJECT, EventHelper.patientParameters(savedPatient)));
        } catch (HttpException e) {
            logger.error("Failed to create a patient in OpenMRS with MoTeCH Id: " + patient.getMotechId());
            return null;
        }

        return savedPatient;
    }

    private void validatePatientBeforeSave(MRSPatient patient) {
        Validate.notNull(patient, "Patient cannot be null");
        Validate.isTrue(StringUtils.isNotEmpty(patient.getMotechId()), "You must provide a motech id to save a patient");
        Validate.notNull(patient.getPerson(), "Person cannot be null when saving a patient");
        Validate.notNull(patient.getFacility(), "Facility cannot be null when saving a patient");
    }

    private Patient fromMrsPatient(MRSPatient patient, MRSPerson savedPerson) {
        Patient converted = new Patient();
        Person person = new Person();
        person.setUuid(savedPerson.getPersonId());
        converted.setPerson(person);

        Location location = null;
        if (patient.getFacility() != null && StringUtils.isNotBlank(patient.getFacility().getFacilityId())) {
            location = new Location();
            location.setUuid(patient.getFacility().getFacilityId());
        }

        String motechPatientIdentiferTypeUuid = null;
        try {
            motechPatientIdentiferTypeUuid = patientResource.getMotechPatientIdentifierUuid();
        } catch (HttpException e) {
            logger.error("There was an exception retrieving the MoTeCH Identifier Type UUID");
            return null;
        }

        if (StringUtils.isBlank(motechPatientIdentiferTypeUuid)) {
            logger.error("Cannot save a patient until a MoTeCH Patient Idenitifer Type is created in the OpenMRS");
            return null;
        }

        IdentifierType type = new IdentifierType();
        type.setUuid(motechPatientIdentiferTypeUuid);

        Identifier identifier = new Identifier();
        identifier.setIdentifier(patient.getMotechId());
        identifier.setLocation(location);
        identifier.setIdentifierType(type);

        List<Identifier> identifiers = new ArrayList<Identifier>();
        identifiers.add(identifier);
        converted.setIdentifiers(identifiers);

        return converted;
    }

    @Override
    public List<MRSPatient> search(String name, String id) {
        Validate.notEmpty(name, "Name cannot be empty");

        PatientListResult result = null;
        try {
            result = patientResource.queryForPatient(name);
        } catch (HttpException e) {
            logger.error("Failed search for patient name: " + name);
            return Collections.emptyList();
        }

        List<MRSPatient> searchResults = new ArrayList<>();

        for (Patient partialPatient : result.getResults()) {
            MRSPatient patient = getPatient(partialPatient.getUuid());
            if (id == null) {
                searchResults.add(patient);
            } else {
                if (patient.getMotechId() != null && patient.getMotechId().contains(id)) {
                    searchResults.add(patient);
                }
            }
        }

        if (searchResults.size() > 0) {
            sortResults(searchResults);
        }


        List<MRSPatient> patientList = new ArrayList<>();

        patientList.addAll(searchResults);

        return patientList;
    }

    private void sortResults(List<MRSPatient> searchResults) {
        Collections.sort(searchResults, new Comparator<MRSPatient>() {
            @Override
            public int compare(MRSPatient patient1, MRSPatient patient2) {
                if (StringUtils.isNotEmpty(patient1.getMotechId()) && StringUtils.isNotEmpty(patient2.getMotechId())) {
                    return patient1.getMotechId().compareTo(patient2.getMotechId());
                } else if (StringUtils.isNotEmpty(patient1.getMotechId())) {
                    return -1;
                } else if (StringUtils.isNotEmpty(patient2.getMotechId())) {
                    return 1;
                }
                return 0;
            }
        });
    }

    @Override
    public MRSPatient updatePatient(MRSPatient patient, String currentMotechId) {
        if (!patient.getMotechId().equals(currentMotechId) && getPatientByMotechId(patient.getMotechId()) != null) {
            throw new MRSException("Patient with Motech ID" + patient.getMotechId() + "already exists.");
        }
        return updatePatient(patient);
    }

    @Override
    public MRSPatient updatePatient(MRSPatient patient) {
        Validate.notNull(patient, "Patient cannot be null");
        Validate.notEmpty(patient.getPatientId(), "Patient Id may not be empty");

        OpenMRSPatient openMRSPatient = ConverterUtils.createPatient(patient);
        OpenMRSPerson person = ConverterUtils.createPerson(patient.getPerson());

        personAdapter.updatePerson(person);
        // the openmrs web service requires an explicit delete request to remove
        // attributes. delete all previous attributes, and then
        // create any attributes attached to the patient
        personAdapter.deleteAllAttributes(person);
        personAdapter.saveAttributesForPerson(person);

        try {
            patientResource.updatePatientMotechId(patient.getPatientId(), patient.getMotechId());
        } catch (HttpException e) {
            logger.error("Failed to update OpenMRS patient with id: " + patient.getPatientId());
            throw new MRSException(e);
        }

        OpenMRSPatient updatedPatient = new OpenMRSPatient(openMRSPatient.getPatientId(), patient.getMotechId(), person, openMRSPatient.getFacility());
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.UPDATED_PATIENT_SUBJECT, EventHelper.patientParameters(updatedPatient)));
        return updatedPatient;
    }

    @Override
    public void deceasePatient(String motechId, String conceptName, Date dateOfDeath, String comment)
            throws PatientNotFoundException {
        Validate.notEmpty(motechId, "MoTeCh id cannot be empty");

        MRSPatient patient = getPatientByMotechId(motechId);
        if (patient == null) {
            logger.error("Cannot decease patient because no patient exist with motech id: " + motechId);
            throw new PatientNotFoundException("No Patient found with Motech Id: " + motechId);
        }

        personAdapter.savePersonCauseOfDeath(patient.getPatientId(), dateOfDeath, conceptName);
        eventRelay.sendEventMessage(new MotechEvent(EventKeys.PATIENT_DECEASED_SUBJECT, EventHelper.patientParameters(patient)));
    }

    @Override
    public List<MRSPatient> getAllPatients() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deletePatient(MRSPatient patient) throws PatientNotFoundException {
        try {
            String id = patientResource.queryForPatient(patient.getMotechId()).getResults().get(0).getUuid();
            patientResource.deletePatient(id);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_PATIENT_SUBJECT, EventHelper.patientParameters(patient)));
        } catch (HttpException e) {
            throw new PatientNotFoundException(e);
        }
    }

}
