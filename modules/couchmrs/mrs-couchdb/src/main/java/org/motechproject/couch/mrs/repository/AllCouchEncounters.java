package org.motechproject.couch.mrs.repository;

import java.util.List;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.mrs.domain.MRSObservation;

public interface AllCouchEncounters {

    CouchEncounterImpl findEncounterById(String encounterId);

    List<CouchEncounterImpl> findEncountersByMotechIdAndEncounterType(String motechId, String encounterType);

    void createOrUpdateEncounter(CouchEncounterImpl encounter);

    CouchEncounterImpl findEncounterByObservationId(String observationId);

    MRSObservation findByObservationId(String observationId);

    List<MRSObservation> findObservationsByPatientIdAndConceptName(String patientMotechId, String conceptName);

    void updateEncounter(CouchEncounterImpl encounter);
}
