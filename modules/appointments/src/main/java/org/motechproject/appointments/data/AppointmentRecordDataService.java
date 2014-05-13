package org.motechproject.appointments.data;

import org.joda.time.DateTime;
import org.motechproject.appointments.domain.AppointmentRecord;
import org.motechproject.appointments.domain.AppointmentStatus;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Interface to utilize the Seuss CRUD operations for appointment record service (logging & reporting)
 */
public interface AppointmentRecordDataService extends MotechDataService<AppointmentRecord> {

    @Lookup
    List<AppointmentRecord> findByCriteria(@LookupField(name = "externalId") String externalId,
                                           @LookupField(name = "appointmentId") String appointmentId,
                                           @LookupField(name = "appointmentDate") DateTime appointmentDate,
                                           @LookupField(name = "fromStatus") DateTime fromStatus,
                                           @LookupField(name = "toStatus") AppointmentStatus toStatus);
}