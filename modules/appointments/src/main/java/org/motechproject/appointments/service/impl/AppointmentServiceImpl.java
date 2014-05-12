package org.motechproject.appointments.service.impl;

import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.domain.AppointmentStatus;
import org.motechproject.appointments.service.AppointmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Service implementation for Appointments
 *
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    /**
     * Add appointments for users with external id set in the appointment objects
     *
     * @param appointments
     */
    public List<Appointment> addAppointments(List<Appointment> appointments) {
        return new ArrayList<>();
    }

    /**
     * Removes all appointments and reminders for given user (identified by externalId)
     *
     * @param appointments list of appointment objects to remove
     */
    public List<Appointment> removeAppointments(List<Appointment> appointments) {
        return new ArrayList<>();
    }

    /**
     * Updates the list of appointments
     * @param appointments List of appointment objects
     * @return Updated list of appointments
     */
    public List<Appointment> updateAppointments(List<Appointment> appointments) {
        return new ArrayList<>();
    }

    /**
     * Find the list of appointments for a given external id (overloaded)
     * @param externalId external id related to the implementation
     * @return List of appointments that belong to the external id
     */
    public List<Appointment> findAppointments(String externalId) {
        return new ArrayList<>();
    }

    /**
     * Find visit for given user identifier (external id) and appointment status (overloaded)
     *
     * @param externalId external id related to the implementation
     * @param status status of the appointment to filter on
     * @return List of appointments that belong to the external id, filtered by status
     */
    public List<Appointment> findAppointments(String externalId, AppointmentStatus status) {
        return new ArrayList<>();
    }

    /**
     * Toggle the reminders for a user with external id
     *
     * @param externalId External id of the user
     * @param sendReminders boolean flag to start or stop reminders based on the appointment interval field
     * @return
     */
    public boolean toggleReminders(String externalId, boolean sendReminders) {
        return true;
    }

}
