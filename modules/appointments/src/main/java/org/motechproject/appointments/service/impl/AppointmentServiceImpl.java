package org.motechproject.appointments.service.impl;

import org.hamcrest.Matchers;
import org.motechproject.appointments.data.AppointmentDataService;
import org.motechproject.appointments.data.AppointmentRecordDataService;
import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.domain.AppointmentRecord;
import org.motechproject.appointments.domain.AppointmentStatus;
import org.motechproject.appointments.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.*;

/**
 *
 * Service implementation for Appointments
 *
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentDataService appointmentDataService;
    private AppointmentRecordDataService appointmentRecordDataService;

    @Autowired
    public AppointmentServiceImpl(AppointmentDataService ads, AppointmentRecordDataService ards) {
        this.appointmentDataService = ads;
        this.appointmentRecordDataService = ards;
    }

    /**
     * Add appointments for users with external id set in the appointment objects
     *
     * @param appointments
     */
    public List<Appointment> addAppointments(List<Appointment> appointments) {

        List<Appointment> result = new ArrayList<>(appointments.size());
        for (Appointment current : appointments) {
            result.add(addHelper(current));
        }
        return result;
    }

    private Appointment addHelper(Appointment appointment) {
        appointmentDataService.create(appointment);
        List<Appointment> lookup = appointmentDataService.findByCriteria(appointment.getExternalId(), appointment.getId(),
                appointment.getAppointmentDate(), appointment.getVisitedDate(), appointment.getAppointmentStatus(),
                appointment.getSendReminders(), appointment.getReminderInterval(), appointment.getReminderStartTime());
        return lookup != null ? lookup.get(0) : null;
    }

    /**
     * Removes all appointments and reminders for given user (identified by externalId)
     *
     * @param appointments list of appointment objects to remove
     */
    public void removeAppointments(List<Appointment> appointments) {
        for (Appointment current : appointments) {
            appointmentDataService.delete(current);
        }
    }

    /**
     * Updates the list of appointments
     * @param appointments List of appointment objects
     * @return Updated list of appointments
     */
    public List<Appointment> updateAppointments(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>(appointments.size());

        for (Appointment current : appointments) {
            result.add(updateHelper(current));
        }

        return result;
    }

    private Appointment updateHelper(Appointment current) {
        Appointment old = appointmentDataService.retrieve("id", current.getId());
        if (old == null) {
            return null;
        }

        // if the appt date changed or status changed, create a change record (log)
        if (old.getAppointmentDate() != current.getAppointmentDate() ||
                old.getAppointmentStatus() != current.getAppointmentStatus()) {
            appointmentRecordDataService.create(new AppointmentRecord(current.getExternalId(), current.getId(),
                    current.getAppointmentDate(), old.getAppointmentStatus(), current.getAppointmentStatus()));
        }

        appointmentDataService.update(current);
        return current;
    }

    /**
     * Find the list of appointments for a given external id (overloaded)
     * @param externalId external id related to the implementation
     * @return List of appointments that belong to the external id
     */
    public List<Appointment> findAppointments(String externalId) {
        return appointmentDataService.findAppointmentsByExternalId(externalId);
    }

    /**
     * Find visit for given user identifier (external id) and appointment status (overloaded)
     *
     * @param externalId external id related to the implementation
     * @param status status of the appointment to filter on
     * @return List of appointments that belong to the external id, filtered by status
     */
    public List<Appointment> findAppointments(String externalId, AppointmentStatus status) {
        return select(appointmentDataService.findAppointmentsByExternalId(externalId),
                having(on(Appointment.class).getAppointmentStatus(), Matchers.equalTo(status)));
    }

    /**
     * Toggle the reminders for a user with external id
     *
     * @param externalId External id of the user
     * @param sendReminders boolean flag to start or stop reminders based on the appointment interval field
     * @return
     */
    public void toggleReminders(String externalId, boolean sendReminders) {
        List<Appointment> result = appointmentDataService.findAppointmentsByExternalId(externalId);

        for (Appointment current : result) {
            if (current.getAppointmentStatus() != AppointmentStatus.VISITED) {
                current.setSendReminders(sendReminders);
            }
            updateHelper(current);
        }
    }

}
