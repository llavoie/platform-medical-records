package org.motechproject.mrs;

import org.motechproject.commons.api.AbstractDataProvider;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSPersonAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MRSDataProvider extends AbstractDataProvider {
    private static final String ID_FIELD = "mrs.id";
    private static final String MOTECH_ID_FIELD = "mrs.motechId";

    private List<MRSPatientAdapter> patientAdapters;
    private List<MRSFacilityAdapter> facilityAdapters;
    private List<MRSPersonAdapter> personAdapters;

    @Autowired
    public MRSDataProvider(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");

        if (resource != null) {
            setBody(resource);
        }
    }

    @Override
    public String getName() {
        return "MRS";
    }

    @Override
    public Object lookup(String type, Map<String, String> lookupFields) {
        Object obj = null;
        try {
            if (supports(type)) {
                if (lookupFields.containsKey(ID_FIELD)) {
                    String id = lookupFields.get(ID_FIELD);

                    Class<?> cls = getClassForType(type);

                    if (MRSPatient.class.isAssignableFrom(cls)) {
                        obj = getPatient(id);
                    } else if (MRSPerson.class.isAssignableFrom(cls)) {
                        obj = getPerson(id);
                    } else if (MRSFacility.class.isAssignableFrom(cls)) {
                        obj = getFacility(id);
                    }

                } else if (lookupFields.containsKey(MOTECH_ID_FIELD)) {
                    String motechId = lookupFields.get(MOTECH_ID_FIELD);

                    Class<?> cls = getClassForType(type);

                    if (MRSPatient.class.isAssignableFrom(cls)) {
                        obj = getPatientByMotechId(motechId);
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            logError("Cannot lookup object: {type: %s, fields: %s}", type, lookupFields.keySet(), e);
        }
        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        return Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.mrs.domain";
    }

    public void setPatientAdapters(List<MRSPatientAdapter> patientAdapters) {
        this.patientAdapters = patientAdapters;
    }

    public void setFacilityAdapters(List<MRSFacilityAdapter> facilityAdapters) {
        this.facilityAdapters = facilityAdapters;
    }

    public void setPersonAdapters(List<MRSPersonAdapter> personAdapters) {
        this.personAdapters = personAdapters;
    }

    private Object getPatient(String patientId) {
        Object obj = null;

        if (patientAdapters != null && !patientAdapters.isEmpty()) {
            for (MRSPatientAdapter adapter : patientAdapters) {
                obj = adapter.getPatient(patientId);
            }
        }

        return obj;
    }

    private Object getPatientByMotechId(String motechId) {
        Object obj = null;

        if (patientAdapters != null && !patientAdapters.isEmpty()) {
            obj =  patientAdapters.get(0).getPatientByMotechId(motechId);
        }
        return obj;
    }

    private MRSFacility getFacility(String facilityId) {
        MRSFacility facility = null;

        if (facilityAdapters != null && !facilityAdapters.isEmpty()) {
            for (MRSFacilityAdapter adapter : facilityAdapters) {
                facility = adapter.getFacility(facilityId);
            }
        }

        return facility;
    }

    private MRSPerson getPerson(String personId) {
        MRSPerson person = null;

        if (personAdapters != null && !personAdapters.isEmpty()) {
            for (MRSPersonAdapter adapter : personAdapters) {
                List<? extends MRSPerson> byPersonId = adapter.findByPersonId(personId);
                person = byPersonId.isEmpty() ? null : byPersonId.get(0);
            }
        }

        return person;
    }
}
