package org.motechproject.appointments.domain;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.mds.annotations.Entity;

/**
 * Appointment class to track the appointments of a person with a given external Id
 */
@Entity
public class Appointment {

    // External id set by the implementation
    private String externalId;

    // Appointment id generated by the system
    private String id;

    // Due date for the appointment
    private DateTime appointmentDate;

    // Date of the fulfilled visit
    private DateTime visitedDate;

    // Status flag to indicate if the appointment was missed
    private AppointmentStatus status;

    // Flag to activate or deactivate sending reminders
    private boolean sendReminders;

    // Reminder interval in seconds
    private Period reminderInterval;

    // Time to start firing the reminder events
    private DateTime reminderStartTime;

    public Appointment() {
        this.externalId = "";
    }

    // Getters & Setters
    public String getExternalId() { return this.externalId; }

    public void setExternalId(String externalId) { this.externalId = externalId; }

    public String getId() { return this.id; }

    public DateTime getAppointmentDate() { return this.appointmentDate;}

    public void setAppointmentDate(DateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public DateTime getVisitedDate() { return this.visitedDate; }

    public void setVisitedDate(DateTime visitedDate) { this.visitedDate = visitedDate; }

    public AppointmentStatus getAppointmentStatus() { return this.status; }

    public void setAppointmentStatus(AppointmentStatus status) { this.status = status; }

    public boolean getSendReminders() { return this.sendReminders; }

    public void setSendReminders(boolean sendReminders) { this.sendReminders = sendReminders; }

    public Period getReminderInterval() { return this.reminderInterval; }

    public void setReminderInterval(Period interval) { this.reminderInterval = interval; }

    public DateTime getReminderStartTime() { return this.reminderStartTime; }

    public void setReminderStartTime(DateTime startTime) { this.reminderStartTime = startTime; }
}
