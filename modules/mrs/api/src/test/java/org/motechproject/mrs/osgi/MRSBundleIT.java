package org.motechproject.mrs.osgi;

import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.mrs.MRSDataProvider;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class MRSBundleIT extends BaseOsgiIT {

    public void testMRSApiBundle() {
        getService(EventListenerRegistryService.class);

        MRSDataProvider providerLookup = (MRSDataProvider) getService(DataProvider.class);

        List<Class<?>> classes = Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);

        for (Class<?> cls : classes) {
            assertTrue(providerLookup.supports(cls.getSimpleName()));
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testApplicationMrsBundle.xml"};
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.mrs"
        );
    }
}
