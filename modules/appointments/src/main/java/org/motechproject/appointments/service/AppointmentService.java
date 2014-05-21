package org.motechproject.appointments.service;

import org.motechproject.appointments.domain.Appointment;
import org.motechproject.appointments.domain.AppointmentStatus;

import java.util.List;


/**
 * AppointmentService interface to add/remove/update/find appointments
 */
public interface AppointmentService {

    /**
     * Add appointments for users with external id set in the appointment objects
     *
     * @param appointments
     */
    List<Appointment> addAppointments(List<Appointment> appointments);

    /**
     * Removes all appointments and reminders for given user (identified by externalId)
     *
     * @param appointments list of appointment objects to remove
     */
    void removeAppointments(List<Appointment> appointments);

    /**
     * Updates the list of appointments
     * @param appointments List of appointment objects
     * @return Updated list of appointments
     */
    List<Appointment> updateAppointments(List<Appointment> appointments);

    /**
     * Find the list of appointments for a given external id (overloaded)
     * @param externalId external id related to the implementation
     * @return List of appointments that belong to the external id
     */
    List<Appointment> findAppointments(String externalId);

    /**
     * Find visit for given user identifier (external id) and appointment status (overloaded)
     *
     * @param externalId external id related to the implementation
     * @param status status of the appointment to filter on
     * @return List of appointments that belong to the external id, filtered by status
     */
    List<Appointment> findAppointments(String externalId, AppointmentStatus status);

    /**
     * Toggle the reminders for a user with external id
     *
     * @param externalId External id of the user
     * @param sendReminders boolean flag to start or stop reminders based on the appointment interval field
     */
    void toggleReminders(String externalId, boolean sendReminders);
}