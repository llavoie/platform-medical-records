package org.motechproject.appointments.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;

/**
 * Created by kosh on 5/12/14.
 */
@Entity
public class AppointmentRecord {

    // external Id of the patient
    public String externalId;

    // Appointment id being changed
    public String appointmentId;

    // date of the Appointment creation/change
    public DateTime appointmentDate;

    // Previous appointment status being changed
    public AppointmentStatus fromStatus;

    // Current appointment status to change to
    public AppointmentStatus toStatus;
}
