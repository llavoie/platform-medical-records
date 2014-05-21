package org.motechproject.appointments.reminder.impl;

import org.joda.time.Period;
import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.reminder.AppointmentReminderService;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation for the appointment reminder connector service
 */
@Component
public class AppointmentReminderServiceImpl implements AppointmentReminderService {

    private static final String SUBJECT = "Appointment.Reminder.%s.%s";
    private MotechSchedulerService schedulerService;


    @Autowired
    public AppointmentReminderServiceImpl(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Add reminders for a specific appointment
     * @param appointment Appointment object with the necessary fields populated
     */
    public void addReminders(Appointment appointment) {
        RepeatingSchedulableJob reminder = buildJob(appointment);
        this.schedulerService.safeScheduleRepeatingJob(reminder);
    }

    /**
     * Remove reminders for a specific appointment
     * @param appointment Appointment object with the necessary fields populated
     */
    public void removeReminders(Appointment appointment) {
        this.schedulerService.safeUnscheduleAllJobs(String.format(SUBJECT, appointment.getExternalId(), appointment.getId()));
    }

    /**
     * Helper to create a repeating schedulable job from an appointment
     * @param appointment
     * @return
     */
    private RepeatingSchedulableJob buildJob(Appointment appointment) {
        String eventTitle = String.format(SUBJECT, appointment.getExternalId(), appointment.getId());
        MotechEvent event = new MotechEvent(eventTitle);

        // temp workaround for lack of period support in scheduler, ignoring anything bigger than weeks for now
        Period interval = appointment.getReminderInterval();
        long reminderInterval = interval.getWeeks() * 7 * 24 * 60 * 60 * 1000 + interval.getDays() * 24 * 60 * 60 * 1000 +
                interval.getHours() * 60 * 60 * 1000 + interval.getMinutes() * 60 * 1000 + interval.getSeconds() * 1000 + interval.getMillis();

        return new RepeatingSchedulableJob(event, appointment.getReminderStartTime().toDate(), appointment.getAppointmentDate().toDate(), reminderInterval, true);
    }
}
