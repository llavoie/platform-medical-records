package org.motechproject.appointments.reminder;

import org.motechproject.appointments.domain.Appointment;
import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kosh on 5/8/14.
 */
@Component
public class AppointmentReminderService {

    private static final String SUBJECT = "Appointment.Reminder";
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
}
