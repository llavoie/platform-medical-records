package org.motechproject.couch.mrs.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.repository.impl.AllCouchEncountersImpl;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class AllCouchEncountersIT extends SpringIntegrationTest {

    @Autowired
    private AllCouchEncounters allCouchEncounters;

    @Autowired
    @Qualifier("couchEncounterDatabaseConnector")
    private CouchDbConnector connector;

    @Test
    public void shouldSaveEncounterAndRetrieveById() {
        CouchEncounterImpl encounter = buildEncounter("patientId", "encounterType", "by-id");

        allCouchEncounters.createOrUpdateEncounter(encounter);

        CouchEncounterImpl retrievedEncounter = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals("patientId", retrievedEncounter.getPatientId());
        assertEquals("encounterType", retrievedEncounter.getEncounterType());
        assertEquals(encounter.getEncounterId(), retrievedEncounter.getEncounterId());
        assertEquals(encounter.getCreatorId(), retrievedEncounter.getCreatorId());
        assertEquals(encounter.getFacilityId(), retrievedEncounter.getFacilityId());
        assertEquals(encounter.getProviderId(), retrievedEncounter.getProviderId());

        assertEquals(encounter.getObservations().size(), retrievedEncounter.getObservations().size());
    }

    @Test
    public void shouldFindByMotechIdAndEncounterType() {
        CouchEncounterImpl encounter = buildEncounter("patientId1", "encounterType1", "by-type");
        CouchEncounterImpl encounter2 = buildEncounter("patientId1", "encounterType1", "by-type");
        CouchEncounterImpl encounter3 = buildEncounter("patientId1", "encounterType1", "by-type");
        CouchEncounterImpl encounter4 = buildEncounter("patientId1", "encounterType2", "by-type");
        CouchEncounterImpl encounter5 = buildEncounter("patientId2", "encounterType1", "by-type");
        CouchEncounterImpl encounter6 = buildEncounter("patientId2", "encounterType2", "by-type");

        allCouchEncounters.createOrUpdateEncounter(encounter);
        allCouchEncounters.createOrUpdateEncounter(encounter2);
        allCouchEncounters.createOrUpdateEncounter(encounter3);
        allCouchEncounters.createOrUpdateEncounter(encounter4);
        allCouchEncounters.createOrUpdateEncounter(encounter5);
        allCouchEncounters.createOrUpdateEncounter(encounter6);

        List<CouchEncounterImpl> retrievedEncounters = allCouchEncounters.findEncountersByMotechIdAndEncounterType(encounter.getPatientId(), encounter.getEncounterType());
        assertEquals(3, retrievedEncounters.size());
    }

    @Test
    public void shouldFindByMotechIdAndConceptName() {
        CouchEncounterImpl encounter = buildEncounter("patientId1", "encounterType", "1-");
        CouchEncounterImpl encounter2 = buildEncounter("patientId1", "encounterType", "2-");
        CouchEncounterImpl encounter3 = buildEncounter("patientId1", "encounterType", "3-");
        CouchEncounterImpl encounter4 = buildEncounter("patientId1", "encounterType", "2-");
        CouchEncounterImpl encounter5 = buildEncounter("patientId2", "encounterType", "2-");
        CouchEncounterImpl encounter6 = buildEncounter("patientId2", "encounterType", "1-");

        allCouchEncounters.createOrUpdateEncounter(encounter);
        allCouchEncounters.createOrUpdateEncounter(encounter2);
        allCouchEncounters.createOrUpdateEncounter(encounter3);
        allCouchEncounters.createOrUpdateEncounter(encounter4);
        allCouchEncounters.createOrUpdateEncounter(encounter5);
        allCouchEncounters.createOrUpdateEncounter(encounter6);

        List<MRSObservation> encounters = allCouchEncounters.findObservationsByPatientIdAndConceptName("patientId1", "2-concept1");

        assertEquals(encounters.size(), 2);
    }


    @Test
    public void shouldFindByObservationId() {
        CouchEncounterImpl encounter = buildEncounter("testObsIdPatient", "encounterType", "prefix1");

        allCouchEncounters.createOrUpdateEncounter(encounter);

        MRSObservation obs = encounter.getObservations().iterator().next();
        String obsId = obs.getObservationId();

        CouchEncounterImpl encounterResult = allCouchEncounters.findEncounterByObservationId(obsId);

        assertNotNull(encounterResult);

        MRSObservation obsResult = encounterResult.getObservationById(obsId);
        assertNotNull(obsResult);

        assertEquals(obs.getConceptName(), obsResult.getConceptName());
        assertEquals(obs.getValue(), obsResult.getValue());
    }

    private CouchEncounterImpl buildEncounter(String patientId, String encounterType, String conceptPrefix) {
        Set<CouchObservation> observations = new HashSet<CouchObservation>();

        CouchObservation obs1 = new CouchObservation(DateTime.now(), conceptPrefix + "concept1", "value1");
        CouchObservation obs2 = new CouchObservation(DateTime.now(), conceptPrefix + "concept2", "value2");
        CouchObservation obs3 = new CouchObservation(DateTime.now(), conceptPrefix + "concept3", "value3");
        observations.add(obs1);
        observations.add(obs2);
        observations.add(obs3);

        return new CouchEncounterImpl(UUID.randomUUID().toString(), "providerId", "creatorId", "facilityId", new DateTime(), observations, patientId, encounterType);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchEncountersImpl) allCouchEncounters).removeAll();
    }
}
