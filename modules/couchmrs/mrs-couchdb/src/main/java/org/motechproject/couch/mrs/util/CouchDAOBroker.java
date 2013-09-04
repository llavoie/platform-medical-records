package org.motechproject.couch.mrs.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.motechproject.couch.mrs.model.CouchEncounter;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchFacility;
import org.motechproject.couch.mrs.model.CouchPatient;
import org.motechproject.couch.mrs.model.CouchPatientImpl;
import org.motechproject.couch.mrs.model.CouchPerson;
import org.motechproject.couch.mrs.model.CouchProvider;
import org.motechproject.couch.mrs.model.CouchProviderImpl;
import org.motechproject.couch.mrs.repository.AllCouchFacilities;
import org.motechproject.couch.mrs.repository.AllCouchPatients;
import org.motechproject.couch.mrs.repository.AllCouchPersons;
import org.motechproject.couch.mrs.repository.AllCouchProviders;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.domain.MRSUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CouchDAOBroker {

    @Autowired
    private AllCouchPersons allCouchPersons;
    @Autowired
    private AllCouchFacilities allFacilities;
    @Autowired
    private AllCouchPatients allPatients;
    @Autowired
    private AllCouchProviders allProviders;

    public MRSEncounter buildFullEncounter(CouchEncounterImpl encounter) {

        MRSUser creator = null;
        MRSPatient patient = null;
        MRSProvider provider = null;
        Set<MRSObservation> observations = new HashSet<MRSObservation>();

        if (encounter.getObservations() != null) {
            observations.addAll(encounter.getObservations());
        }

        MRSFacility facility = null;

        String patientId = encounter.getPatientId();
        String providerId = encounter.getProviderId();
        String facilityId = encounter.getFacilityId();

        patient = buildFullPatient(allPatients.findByMotechId(patientId));

        provider = buildFullProvider(allProviders.findByProviderId(providerId));

        List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);

        if (facilities != null && facilities.size() > 0) {
            facility = facilities.get(0);
        }

        return new CouchEncounter(encounter.getEncounterId(), provider, creator, facility, encounter.getDate(), observations, patient, encounter.getEncounterType());
    }

    public MRSPatient buildFullPatient(List<CouchPatientImpl> couchPatients) {

        if (couchPatients != null && couchPatients.size() > 0) {
            CouchPatientImpl couchPatient = couchPatients.get(0);
            String facilityId = couchPatient.getFacilityId();
            List<CouchFacility> facilities = allFacilities.findByFacilityId(facilityId);
            CouchFacility facility = null;
            if (facilities != null && facilities.size() > 0) {
                facility = facilities.get(0);
            }
            String personId = couchPatient.getPersonId();
            List<CouchPerson> persons = allCouchPersons.findByPersonId(personId);
            CouchPerson person = null;
            if (persons != null && persons.size() > 0) {
                person = persons.get(0);
            }
            return new CouchPatient(couchPatient.getPatientId(), couchPatient.getMotechId(), person, facility);
        }

        return null;
    }

    public CouchProvider buildFullProvider(List<CouchProviderImpl> providers) {
        CouchPerson person = null;
        if (providers != null && providers.size() > 0) {
            if (providers.get(0).getPersonId() != null) {
                person = allCouchPersons.findByPersonId(providers.get(0).getPersonId()).get(0);
            }
            return new CouchProvider(providers.get(0).getProviderId(), person);
        }

        return null;
    }
}
