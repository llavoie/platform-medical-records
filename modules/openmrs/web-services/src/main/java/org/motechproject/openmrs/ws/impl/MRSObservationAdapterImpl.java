package org.motechproject.openmrs.ws.impl;

import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.resource.ObservationResource;
import org.motechproject.openmrs.ws.resource.model.Observation;
import org.motechproject.openmrs.ws.resource.model.ObservationListResult;
import org.motechproject.openmrs.ws.util.ConverterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("observationAdapter")
public class MRSObservationAdapterImpl implements MRSObservationAdapter {
    private static final Logger LOGGER = Logger.getLogger(MRSObservationAdapterImpl.class);

    private final MRSPatientAdapter patientAdapter;
    private final ObservationResource obsResource;
    private final EventRelay eventRelay;

    @Autowired
    public MRSObservationAdapterImpl(ObservationResource obsResource, MRSPatientAdapter patientAdapter, EventRelay eventRelay) {
        this.obsResource = obsResource;
        this.patientAdapter = patientAdapter;
        this.eventRelay = eventRelay;
    }

    @Override
    public List<MRSObservation> findObservations(String motechId, String conceptName) {
        Validate.notEmpty(motechId, "Motech id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<MRSObservation> obs = new ArrayList<>();
        MRSPatient patient = patientAdapter.getPatientByMotechId(motechId);
        if (patient == null) {
            return obs;
        }

        ObservationListResult result = null;
        try {
            result = obsResource.queryForObservationsByPatientId(patient.getPatientId());
        } catch (HttpException e) {
            LOGGER.error("Could not retrieve observations for patient with motech id: " + motechId);
            return Collections.emptyList();
        }

        for (Observation ob : result.getResults()) {
            if (ob.hasConceptByName(conceptName)) {
                obs.add(ConverterUtils.convertObservationToMrsObservation(ob));
            }
        }

        return obs;
    }

    @Override
    public void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {
        Validate.notNull(mrsObservation);
        Validate.notEmpty(mrsObservation.getObservationId());

        try {
            obsResource.deleteObservation(mrsObservation.getObservationId(), reason);
            eventRelay.sendEventMessage(new MotechEvent(EventKeys.CREATED_NEW_OBSERVATION_SUBJECT, EventHelper.observationParameters(mrsObservation)));
        } catch (HttpException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                LOGGER.warn("No Observation found with uuid: " + mrsObservation.getObservationId());
                throw new ObservationNotFoundException(mrsObservation.getObservationId(), e);
            }

            LOGGER.error("Could not void observation with uuid: " + mrsObservation.getObservationId());
        }
    }

    @Override
    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        Validate.notEmpty(patientMotechId, "MoTeCH Id cannot be empty");
        Validate.notEmpty(conceptName, "Concept name cannot be empty");

        List<MRSObservation> observations = findObservations(patientMotechId, conceptName);
        if (observations.size() == 0) {
            return null;
        }

        return observations.get(0);
    }

    @Override
    public MRSObservation getObservationById(String id) {
        try {
            Observation obs = obsResource.getObservationById(id);
            return ConverterUtils.convertObservationToMrsObservation(obs);
        } catch (HttpException e) {
            return null;
        }
    }
}