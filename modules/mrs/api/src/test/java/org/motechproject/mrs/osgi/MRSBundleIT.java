package org.motechproject.mrs.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.mrs.MRSDataProvider;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MRSBundleIT extends BasePaxIT {

    @Inject
    private BundleContext bundleContext;

    @Test
    public void testMRSApiBundle() {
        MRSDataProvider providerLookup =
                (MRSDataProvider) ServiceRetriever.getService(bundleContext, DataProvider.class);

        List<Class<?>> classes = Arrays.asList(MRSPerson.class, MRSPatient.class, MRSFacility.class);

        for (Class<?> cls : classes) {
            assertTrue(providerLookup.supports(cls.getSimpleName()));
        }
    }
}
