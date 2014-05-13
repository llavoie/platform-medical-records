package org.motechproject.appointments.data;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.domain.AppointmentStatus;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Interface to utilize the Seuss CRUD operations for appointment service
 */
public interface AppointmentDataService extends MotechDataService<Appointment> {

    @Lookup
    List<Appointment> findByCriteria(@LookupField(name = "externalId") String externalId,
                                     @LookupField(name = "id") String id,
                                     @LookupField(name = "appointmentDate") DateTime appointmentDate,
                                     @LookupField(name = "visitedDate") DateTime visitedDate,
                                     @LookupField(name = "status") AppointmentStatus status,
                                     @LookupField(name = "sendReminders") boolean sendReminders,
                                     @LookupField(name = "reminderInterval") Period reminderInterval,
                                     @LookupField(name = "startTime") DateTime startTime);

    @Lookup
    Appointment findAppointmentByStatus(@LookupField(name = "status") AppointmentStatus status);

    @Lookup
    List<Appointment> findAppointmentsByExternalId(@LookupField(name = "externalId") String externalId);
}
