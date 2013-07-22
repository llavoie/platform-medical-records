package org.motechproject.couch.mrs.impl;

import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.helper.EventHelper;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CouchObservationAdapter implements MRSObservationAdapter {

    @Autowired
    private AllCouchEncounters allCouchEncounters;

    @Autowired
    private EventRelay eventRelay;

    @Override
    public void voidObservation(MRSObservation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {

        CouchEncounterImpl encounter = allCouchEncounters.findEncounterByObservationId(mrsObservation.getObservationId());

        if (encounter == null) {
            throw new ObservationNotFoundException("The observation with id: " + mrsObservation.getObservationId() + " was not found in the Couch database");
        } 

        MRSObservation obsToRemove = encounter.getObservationById(mrsObservation.getObservationId());

        encounter.getObservations().remove(obsToRemove);

        allCouchEncounters.updateEncounter(encounter);

        eventRelay.sendEventMessage(new MotechEvent(EventKeys.DELETED_OBSERVATION_SUBJECT, EventHelper.observationParameters(obsToRemove)));
    }

    @Override
    public MRSObservation findObservation(String patientMotechId, String conceptName) {
        List<MRSObservation> obsList = findObservations(patientMotechId, conceptName);
        if (obsList.size() > 0) {
            return obsList.get(0);
        }
        return null;
    }

    @Override
    public List<MRSObservation> findObservations(String patientMotechId, String conceptName) {
        return allCouchEncounters.findObservationsByPatientIdAndConceptName(patientMotechId, conceptName);
    }

    @Override
    public MRSObservation getObservationById(String observationId) {
        return allCouchEncounters.findByObservationId(observationId);
    }
}
