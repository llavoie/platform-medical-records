package org.motechproject.appointments.reminder;

import org.joda.time.Period;
import org.motechproject.appointments.domain.Appointment;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.scheduler.domain.RepeatingSchedulableJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kosh on 5/8/14.
 */
@Component
public class AppointmentReminderService {

    private static final String SUBJECT = "Appointment.Reminder.%s.%s";
    private MotechSchedulerService schedulerService;


    @Autowired
    public AppointmentReminderService(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void addReminders(Appointment appointment) {

    }

    public void removeReminders(Appointment appointment) {

    }

    public void updateReminders(Appointment appointment) {

    }

    private RepeatingSchedulableJob buildJob(Appointment appointment) {
        String eventTitle = String.format(SUBJECT, appointment.getExternalId(), appointment.getId());
        MotechEvent event = new MotechEvent(eventTitle);

        // temp workaround for lack of period support in scheduler, ignoring anything bigger than weeks for now
        Period interval = appointment.getReminderInterval();
        long reminderInterval = interval.getWeeks() * 7 * 24 * 60 * 60 * 1000 + interval.getDays() * 24 * 60 * 60 * 1000 +
                interval.getHours() * 60 * 60 * 1000 + interval.getMinutes() * 60 * 1000 + interval.getSeconds() * 1000 + interval.getMillis();

        return new RepeatingSchedulableJob();
    }
}
