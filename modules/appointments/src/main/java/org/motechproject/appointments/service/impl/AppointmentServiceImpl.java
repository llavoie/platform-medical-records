package org.motechproject.appointments.service.impl;

import org.hamcrest.Matchers;
import org.motechproject.appointments.data.AppointmentDataService;
import org.motechproject.appointments.data.AppointmentRecordDataService;
import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.domain.AppointmentRecord;
import org.motechproject.appointments.domain.AppointmentStatus;
import org.motechproject.appointments.reminder.AppointmentReminderService;
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
    private AppointmentReminderService appointmentReminderService;

    @Autowired
    public AppointmentServiceImpl(AppointmentDataService ads, AppointmentRecordDataService ards, AppointmentReminderService ars) {
        this.appointmentDataService = ads;
        this.appointmentRecordDataService = ards;
        this.appointmentReminderService = ars;
    }

    /**
     * Add appointments for users with external id set in the appointment objects
     * @param appointments List of new appointments to add/track
     */
    public List<Appointment> addAppointments(List<Appointment> appointments) {

        List<Appointment> result = new ArrayList<>(appointments.size());
        for (Appointment current : appointments) {
            result.add(addHelper(current));
        }
        return result;
    }

    /**
     * Helper to create/add appointments to MDS
     * @param appointment appointment to add
     * @return appointment object from MDS
     */
    private Appointment addHelper(Appointment appointment) {
        appointmentDataService.create(appointment);
        List<Appointment> lookup = appointmentDataService.findByCriteria(appointment.getExternalId(), appointment.getId(),
                appointment.getAppointmentDate(), appointment.getVisitedDate(), appointment.getAppointmentStatus(),
                appointment.getSendReminders(), appointment.getReminderInterval(), appointment.getReminderStartTime());
        if (lookup != null) {
            Appointment current = lookup.get(0);
            if (current.getSendReminders()) {
                this.appointmentReminderService.addReminders(current);
            }

            return lookup.get(0);
        } else {
            return null;
        }
    }

    /**
     * Removes all appointments and reminders for given user (identified by externalId)
     * @param appointments list of appointment objects to remove
     */
    public void removeAppointments(List<Appointment> appointments) {
        for (Appointment current : appointments) {
            appointmentDataService.delete(current);
            this.appointmentReminderService.removeReminders(current);
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

    /**
     * Update the the appointment if we can find it. Returns null otherwise.
     * @param current the latest version of the appointment object to update to
     * @return The updated appointment object if successful, null otherwise
     */
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

        // commit the new version of the appointment
        appointmentDataService.update(current);

        // remove old reminders and add new ones if needed
        this.appointmentReminderService.removeReminders(old);
        if (current.getSendReminders()) {
            this.appointmentReminderService.addReminders(current);
        }

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
     * Find appointments for given user identifier (external id) and appointment status (overloaded)
     * @param externalId external id related to the implementation
     * @param status status of the appointment to filter on
     * @return List of appointments that belong to the external id, filtered by status
     */
    public List<Appointment> findAppointments(String externalId, AppointmentStatus status) {
        return select(appointmentDataService.findAppointmentsByExternalId(externalId),
                having(on(Appointment.class).getAppointmentStatus(), Matchers.equalTo(status)));
    }

    /**
     * Toggle the reminders for a user with external id. This is the same as passing in an updated
     * Appointment object to the updateAppointments API with the sendReminders flag set
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
