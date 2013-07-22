package org.motechproject.couch.mrs.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.codehaus.jackson.JsonNode;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.motechproject.couch.mrs.util.CouchJsonUtils;
import org.motechproject.mrs.domain.MRSObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllCouchEncountersImpl extends MotechBaseRepository<CouchEncounterImpl> implements AllCouchEncounters {

    @Autowired
    protected AllCouchEncountersImpl(@Qualifier("couchEncounterDatabaseConnector") CouchDbConnector db) {
        super(CouchEncounterImpl.class, db);
        initStandardDesignDocument();
    }

    @Override
    @View(name = "by_encounterId", map = "function(doc) { if (doc.type ==='Encounter') { emit(doc.encounterId, doc._id); }}")
    public CouchEncounterImpl findEncounterById(String encounterId) {

        if (encounterId == null) {
            return null;
        }

        ViewQuery viewQuery = createQuery("by_encounterId").key(encounterId).includeDocs(true);
        List<CouchEncounterImpl> encounters = db.queryView(viewQuery, CouchEncounterImpl.class);

        return (encounters == null || encounters.size() == 0) ? null : encounters.get(0);
    }

    @Override
    @View(name = "find_by_motech_id_and_encounter_type", map = "function(doc) {if(doc.type === 'Encounter') emit([doc.encounterPatientId, doc.encounterType]);}")
    public List<CouchEncounterImpl> findEncountersByMotechIdAndEncounterType(String motechId, String encounterType) {
        return queryView("find_by_motech_id_and_encounter_type", ComplexKey.of(motechId, encounterType));
    }

    @Override
    @View(name = "find_encounter_by_observation_id", map = "function (doc) { if (doc.type === 'Encounter') for each(item in doc.observations) { emit(item.observationId); recursiveDescent(item); } function recursiveDescent(obsNode) { for each(item in obsNode.dependantObservations) { emit(item.observationId); recursiveDescent(item) }}}")
    public CouchEncounterImpl findEncounterByObservationId(String observationId) {
        ViewQuery viewQuery = createQuery("find_encounter_by_observation_id").key(observationId).includeDocs(true);
        List<CouchEncounterImpl> encounters = db.queryView(viewQuery, CouchEncounterImpl.class);

        return encounters.isEmpty() ? null : encounters.get(0);
    }

    @Override
    @View(name = "find_by_observation_id", map = "function (doc) { if (doc.type === 'Encounter') for each(item in doc.observations) { emit(item.observationId, item); recursiveDescent(item); } function recursiveDescent(obsNode) { for each(item in obsNode.dependantObservations) { emit(item.observationId, item); recursiveDescent(item) }}}")
    public MRSObservation findByObservationId(String observationId) {
        ViewQuery viewQuery = createQuery("find_by_observation_id").key(observationId).includeDocs(false);

        ViewResult viewResult = db.queryView(viewQuery);

        MRSObservation obs = null;

        if (viewResult.getRows() != null && viewResult.getRows().size() > 0) {
            Row row = viewResult.getRows().get(0);
            JsonNode value = row.getValueAsNode();
            String text = value.toString();
            obs = (CouchObservation) CouchJsonUtils.readJson(text, CouchObservation.class);
        }

        return obs;
    }

    @Override
    @View(name = "find_observations_by_patient_id_and_concept_name", map = "function (doc) { if (doc.type === 'Encounter') for each(item in doc.observations) { emit([doc.encounterPatientId, item.conceptName], item); recursiveDescent(item); } function recursiveDescent(obsNode) { for each(item in obsNode.dependantObservations) { emit([doc.encounterPatientId, item.conceptName], item); recursiveDescent(item) }}}")
    public List<MRSObservation> findObservationsByPatientIdAndConceptName(String motechId, String conceptName) {
        ViewQuery viewQuery = createQuery("find_observations_by_patient_id_and_concept_name").key(ComplexKey.of(motechId, conceptName)).includeDocs(false);

        ViewResult viewResult = db.queryView(viewQuery);

        List<MRSObservation> obsList = new ArrayList<MRSObservation>();

        for (Row row : viewResult.getRows()) {
            JsonNode value = row.getValueAsNode();
            String text = value.toString();
            MRSObservation obs = (CouchObservation) CouchJsonUtils.readJson(text, CouchObservation.class);
            obsList.add(obs);
        }

        return obsList;
    }

    @Override
    public void createOrUpdateEncounter(CouchEncounterImpl encounter) {
        if (encounter.getEncounterId() == null || encounter.getEncounterId().trim().length() == 0) {
            encounter.setEncounterId(UUID.randomUUID().toString());
            this.add(encounter);
            return;
        }

        CouchEncounterImpl oldEncounter = findEncounterById(encounter.getEncounterId());

        if (oldEncounter == null) {
            this.add(encounter);
        } else {
            this.update(updateEncounter(oldEncounter, encounter));
        }
    }

    private CouchEncounterImpl updateEncounter(CouchEncounterImpl oldEncounter, CouchEncounterImpl newEncounter) {
        oldEncounter.setCreatorId(newEncounter.getCreatorId());
        oldEncounter.setDate(newEncounter.getDate());
        oldEncounter.setEncounterType(newEncounter.getEncounterType());
        oldEncounter.setFacilityId(newEncounter.getFacilityId());
        oldEncounter.setObservations(newEncounter.getObservations());
        oldEncounter.setPatientId(newEncounter.getPatientId());
        oldEncounter.setProviderId(newEncounter.getProviderId());

        return oldEncounter;
    }

    @Override
    public void updateEncounter(CouchEncounterImpl encounter) {
        this.update(encounter);
    }
}
