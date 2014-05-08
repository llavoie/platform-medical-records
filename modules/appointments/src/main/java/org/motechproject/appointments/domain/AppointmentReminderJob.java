package org.motechproject.appointments.domain;

import org.motechproject.scheduler.domain.RepeatingSchedulableJob;

/**
 * Helper class to manage reminders for an appointment
 */
public class AppointmentReminderJob extends RepeatingSchedulableJob {
    // multiplier for the scheduler
    public static final int MILLIS_PER_SEC = 1000;

   public AppointmentReminderJob(Appointment appointment) {

   }
}
