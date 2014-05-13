package org.motechproject.appointments.domain;

import org.joda.time.DateTime;
import org.motechproject.mds.annotations.Entity;

/**
 * Created by kosh on 5/12/14.
 */
@Entity
public class AppointmentRecord {

    // external Id of the patient
    private String externalId;

    // Appointment id being changed
    private String appointmentId;

    // date of the Appointment creation/change
    private DateTime appointmentDate;

    // Previous appointment status being changed
    private AppointmentStatus fromStatus;

    // Current appointment status to change to
    private AppointmentStatus toStatus;

    public AppointmentRecord(String externalId, String appointmentId,
                             DateTime appointmentDate, AppointmentStatus fromStatus, AppointmentStatus toStatus) {
        this.externalId = externalId;
        this.appointmentId = appointmentId;
        this.appointmentDate = appointmentDate;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    // Getters & Setters
    public String getExternalId() { return this.externalId; }

    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getAppointmentId() { return this.appointmentId; }

    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public DateTime getAppointmentDate() { return this.appointmentDate; }

    public void setAppointmentDate(DateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public AppointmentStatus getFromStatus() { return this.fromStatus; }

    public void setFromStatus(AppointmentStatus fromStatus) { this.fromStatus = fromStatus; }

    public AppointmentStatus getToStatus() { return this.toStatus; }

    public void setToStatus(AppointmentStatus toStatus) { this.toStatus = toStatus; }
}
