package org.motechproject.mrs.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.mrs.services.PatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MrsImplementationsDataProvider implements OsgiServiceLifecycleListener {
    private static Map<String, PatientAdapter> patientAdapterMap = new LinkedHashMap<>();

    public static Map<String, PatientAdapter> getPatientAdapterMap() {
        return patientAdapterMap;
    }

    public static void setPatientAdapterMap(Map<String, PatientAdapter> patientAdapterMap) {
        MrsImplementationsDataProvider.patientAdapterMap = patientAdapterMap;
    }

    @Override
    public void bind(Object service, Map serviceProperties) throws IOException {
        if (service instanceof PatientAdapter) {
            patientAdapterMap.put(serviceProperties.get("Bundle-SymbolicName").toString(), (PatientAdapter)service);
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        if (service instanceof PatientAdapter) {
            patientAdapterMap.remove(serviceProperties.get("Bundle-SymbolicName").toString());
        }
    }

}
