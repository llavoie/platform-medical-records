package org.motechproject.appointments.helpers;

import org.motechproject.scheduler.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kosh on 5/8/14.
 */
@Component
public class AppointmentReminders {

    private MotechSchedulerService schedulerService;

    @Autowired
    public AppointmentReminders(MotechSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
