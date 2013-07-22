package org.motechproject.couch.mrs.impl;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.couch.mrs.model.CouchEncounter;
import org.motechproject.couch.mrs.model.CouchEncounterImpl;
import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.MRSCouchException;
import org.motechproject.couch.mrs.repository.AllCouchEncounters;
import org.motechproject.couch.mrs.repository.impl.AllCouchEncountersImpl;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistry;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mrs.EventKeys;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSObservation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/motech/*.xml")
public class CouchObservationAdapterIT extends SpringIntegrationTest {

    @Autowired
    private CouchObservationAdapter observationAdapter;

    @Autowired
    private AllCouchEncounters allCouchEncounters;

    @Autowired
    EventListenerRegistry eventListenerRegistry;

    MrsListener mrsListener;
    final Object lock = new Object();

    @Autowired
    @Qualifier("couchEncounterDatabaseConnector")
    CouchDbConnector connector;

    @Test
    public void shouldFindByDependantObservation() {
        CouchObservation<String> observation1 = new CouchObservation<String>(new DateTime(), "level1", "stringValue", "patient1");
        CouchObservation<String> observation2 = new CouchObservation<String>(new DateTime(), "level2", "stringValue", "patient1");
        CouchObservation<String> observation3 = new CouchObservation<String>(new DateTime(), "level3", "stringValue", "patient1");
        CouchObservation<String> observation4 = new CouchObservation<String>(new DateTime(), "level3-2", "stringValue", "patient1");
        CouchObservation<String> observation5 = new CouchObservation<String>(new DateTime(), "level4", "stringValue", "patient1");

        observation4.addDependantObservation(observation5);
        observation2.addDependantObservation(observation4);
        observation2.addDependantObservation(observation3);
        observation1.addDependantObservation(observation2);

        CouchEncounterImpl couchEncounter = new CouchEncounterImpl();

        HashSet<CouchObservation> observations = new HashSet<CouchObservation>();

        observations.add(observation1);

        couchEncounter.setObservations(observations);
        couchEncounter.setPatientId("PatientId");

        allCouchEncounters.createOrUpdateEncounter(couchEncounter);

        MRSObservation obs5 = observationAdapter.getObservationById(observation5.getObservationId());

        assertEquals(obs5.getConceptName(), observation5.getConceptName());
        assertEquals(obs5.getObservationId(), observation5.getObservationId());

        MRSObservation obs4 = observationAdapter.getObservationById(observation4.getObservationId());

        assertEquals(obs4.getConceptName(), observation4.getConceptName());
        assertEquals(obs4.getObservationId(), observation4.getObservationId());

        MRSObservation obs3 = observationAdapter.getObservationById(observation3.getObservationId());

        assertEquals(obs3.getConceptName(), observation3.getConceptName());
        assertEquals(obs3.getObservationId(), observation3.getObservationId());

        MRSObservation obs2 = observationAdapter.getObservationById(observation2.getObservationId());

        assertEquals(obs2.getConceptName(), observation2.getConceptName());
        assertEquals(obs2.getObservationId(), observation2.getObservationId());

        MRSObservation obs1 = observationAdapter.getObservationById(observation1.getObservationId());

        assertEquals(obs1.getConceptName(), observation1.getConceptName());
        assertEquals(obs1.getObservationId(), observation1.getObservationId());
        
        List<MRSObservation> observationsReturned = observationAdapter.findObservations(couchEncounter.getPatientId(), observation4.getConceptName());
        assertEquals(observationsReturned.size(), 1);
        assertEquals(observationsReturned.get(0).getConceptName(), observation4.getConceptName());
        assertEquals(observationsReturned.get(0).getObservationId(), observation4.getObservationId());
    }

    @Test
    public void shouldReturnFullObservationObjectGraph() throws MRSCouchException {
        CouchObservation<String> observation = new CouchObservation<String>(new DateTime(), "testConcept", "stringValue", "patient1");

        CouchObservation<String> observation2 = new CouchObservation<String>(new DateTime(), "testConcep2t", "stringValue2", "patient1");

        CouchObservation<String> observation3 = new CouchObservation<String>(new DateTime(), "testConcept3", "stringValue3", "patient1");

        CouchObservation<String> observation4 = new CouchObservation<String>(new DateTime(), "testConcept4", "stringValue4", "patient1");

        Set<MRSObservation> dependantObservations = new HashSet<MRSObservation>();

        dependantObservations.add(observation);
        dependantObservations.add(observation2);
        dependantObservations.add(observation3);

        observation4.setDependantObservations(dependantObservations);

        CouchEncounterImpl couchEncounter = new CouchEncounterImpl();

        HashSet<CouchObservation> observations = new HashSet<CouchObservation>();

        observations.add(observation);
        observations.add(observation2);
        observations.add(observation3);
        observations.add(observation4);

        couchEncounter.setObservations(observations);
        couchEncounter.setPatientId("PatientId");

        allCouchEncounters.createOrUpdateEncounter(couchEncounter);

        List<MRSObservation> obsList = observationAdapter.findObservations(couchEncounter.getPatientId(), observation4.getConceptName());

        assertEquals(1, obsList.size());

        Set<MRSObservation> dependantObsReturned = obsList.get(0).getDependantObservations();

        assertEquals(3, dependantObsReturned.size());

        Iterator<MRSObservation> obsIterator = dependantObsReturned.iterator();

        while (obsIterator.hasNext()) {
            assertTrue(obsIterator.next().getConceptName() != null);
        }
    }

    @Test
    public void shouldVoidObservations() throws ObservationNotFoundException {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept1", "stringValue", "patient1");
        CouchObservation observation2 = new CouchObservation(new DateTime(), "testConcept2", "stringValue", "patient1");
        CouchObservation observation3 = new CouchObservation(new DateTime(), "testConcept3", "stringValue", "patient1");
        CouchObservation observation4 = new CouchObservation(new DateTime(), "testConcept4", "stringValue", "patient1");

        CouchEncounterImpl encounter = new CouchEncounterImpl();

        HashSet<CouchObservation> observations = new HashSet<CouchObservation>();
        observations.add(observation);
        observations.add(observation2);
        observations.add(observation3);
        observations.add(observation4);

        encounter.setObservations(observations);
        encounter.setPatientId("testVoidPatient");

        allCouchEncounters.createOrUpdateEncounter(encounter);

        CouchEncounterImpl encounterReturned = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals(4, encounterReturned.getObservations().size());

        observationAdapter.voidObservation(observation, null, null);

        encounterReturned = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals(3, encounterReturned.getObservations().size());

        observationAdapter.voidObservation(observation2, null, null);

        encounterReturned = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals(2, encounterReturned.getObservations().size());

        observationAdapter.voidObservation(observation3, null, null);

        encounterReturned = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals(1, encounterReturned.getObservations().size());

        observationAdapter.voidObservation(observation4, null, null);

        encounterReturned = allCouchEncounters.findEncounterById(encounter.getEncounterId());

        assertEquals(0, encounterReturned.getObservations().size());
    }

    @Test
    public void shouldRaiseVoidEvent() throws ObservationNotFoundException, InterruptedException {
        CouchObservation observation = new CouchObservation(new DateTime(), "testConcept", "stringValue", "patient1");

        CouchEncounterImpl encounter = new CouchEncounterImpl();

        HashSet<CouchObservation> observations = new HashSet<CouchObservation>();
        observations.add(observation);

        encounter.setObservations(observations);
        encounter.setPatientId("testVoidPatient");

        allCouchEncounters.createOrUpdateEncounter(encounter);

        mrsListener = new MrsListener();
        eventListenerRegistry.registerListener(mrsListener, EventKeys.DELETED_OBSERVATION_SUBJECT);

        synchronized (lock) {
            observationAdapter.voidObservation(observation, null, null);
            lock.wait(60000);
        }

        assertEquals(observation.getConceptName(), mrsListener.eventParameters.get(EventKeys.OBSERVATION_CONCEPT_NAME));
        assertTrue(mrsListener.voided);
    }


    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }

    @After
    public void tearDown() {
        ((AllCouchEncountersImpl) allCouchEncounters).removeAll();
        eventListenerRegistry.clearListenersForBean("mrsTestListener");
    }

    public class MrsListener implements EventListener {

        private boolean voided = false;
        private Map<String, Object> eventParameters;

        @MotechListener(subjects = {EventKeys.DELETED_OBSERVATION_SUBJECT})
        public void handle(MotechEvent event) {
            voided = true;
            eventParameters = event.getParameters();
            synchronized (lock) {
                lock.notify();
            }
        }

        @Override
        public String getIdentifier() {
            return "mrsTestListener";
        }
    }
}
