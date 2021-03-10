package model;

import java.time.LocalDateTime;

/**
 * @author matt
 *
 * This model class defines the properties and getters/setters for
 * the Appointment model class.
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private int customerID;
    private int userID;
    private int contactID;

    /**
     * This is the appointment constructor. It is used to hold initialize an Appointment object using the code for the
     * Appointment model.
     *
     * @param appointmentID
     * @param title
     * @param description
     * @param location
     * @param type
     * @param start
     * @param end
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param customerID
     * @param userID
     * @param contactID
     */
    public Appointment(int appointmentID, String title, String description, String location, String type, LocalDateTime start, LocalDateTime end, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.customerID = customerID;
        this.userID = userID;
        this.contactID = contactID;
    }

    /**
     * This getter returns the appointmentID.
     *
     * @return int
     */
    public int getAppointmentID() {
        return appointmentID;
    }

    /**
     * This setter sets the appointmentID data to the object.
     *
     * @param appointmentID
     */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * This getter returns the Appointment Title data.
     *
     * @return String
     */
    public String getTitle() {
        return title;
    }

    /**
     * This setter sets the Title data for an Appointment object.
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This getter returns the Appointment description data.
     *
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * This setter sets an Appointment description for an Appointment object.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This getter returns the location for an Appointment object.
     *
     * @return String
     */
    public String getLocation() {
        return location;
    }

    /**
     * This setter sets the location data for an Appointment object.
     *
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * This getter returns the type of an Appointment in String format.
     *
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * This setter sets the Appointment type for an Appointment object.
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This getter returns the start date & time of an Appointment object in the form of a LocalDateTime type.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * This setter sets the start date & time data for an Appointment object in the form of a LocalDateTime type.
     *
     * @param start
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * This getter returns the end date & time of an Appointment object in the form of a LocalDateTime type.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * This setter sets the end date & time data for an Appointment object in the form of a LocalDateTime type.
     *
     * @param end
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * This getter returns the date & time of when this Appointment database entry was first created, in the form of a
     * LocalDateTime object.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * This setter sets the date & time of when a Appointment database entry was first created, in the form of a
     * LocalDateTime object.
     *
     * @param createDate
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * This getter returns who the Appointment database entry was created by.
     *
     * @return String
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * This setter sets who created the respective Appointment database entry.
     *
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This getter returns the date & time of when this database entry was last updated.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * This setter sets the date & time of when this database entry was last updated.
     *
     * @param lastUpdate
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * This getter returns who last updated this Appointment database entry.
     *
     * @return String
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * This setter sets who last updated this Appointment database entry.
     *
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * This getter returns the Appointment customer ID.
     *
     * @return int
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * This setter sets the Appointment customer ID.
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * This getter returns this Appointments related user ID.
     * @return int
     */
    public int getUserID() {
        return userID;
    }

    /**
     * This setter sets this Appointments related user ID.
     *
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * This getter returns this Appointments contact ID.
     * @return int
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * This setter sets this Appointments contact ID.
     * @param contactID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }
}
