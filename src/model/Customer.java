package model;

import java.time.LocalDateTime;

/**
 * @author matt
 *
 * This model class defines the properties and getters/setters for
 * the Customer model class.
 */
public class Customer {
    private int customerID;
    private String customerName;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;

    /**
     * This method is the constructor for the Customer class, which is used to create a Customer object.
     *
     * @param customerID
     * @param customerName
     * @param address
     * @param postalCode
     * @param phoneNumber
     * @param createDate
     * @param createdBy
     * @param lastUpdate
     * @param lastUpdatedBy
     * @param divisionID
     */
    public Customer(int customerID, String customerName, String address, String postalCode, String phoneNumber, LocalDateTime createDate, String createdBy, LocalDateTime lastUpdate, String lastUpdatedBy, int divisionID) {
        this.customerID = customerID;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.divisionID = divisionID;
    }

    /**
     * This method returns a customer ID in integer format from a Customer object.
     *
     * @return int
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * This method sets the customer ID for a Customer object.
     *
     * @param customerID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * This method returns the Customer name in String format from a Customer object.
     *
     * @return String
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * This method sets the customer name property for a Customer object.
     *
     * @param customerName
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * This method returns the address of a customer object in String format.
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method set the address property of a Customer object.
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * This method returns the postal code property of a Customer object in String format.
     *
     * @return String
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * This method sets the postal code property of a Customer object.
     *
     * @param postalCode
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * This method returns the phone number property from a Customer object in String format.
     *
     * @return String
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This method sets the phone number property for a Customer object.
     *
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This method returns the create date of a Customer object in LocalDateTime format.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    /**
     * This method sets the create date of a Customer object in LocalDateTime format.
     *
     * @param createDate
     */
    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * This method returns the user who created the customer in String format.
     *
     * @return String
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * This method sets the name of the user who created the customer entry.
     *
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * This method returns the last update date of a Customer object in LocalDateTime format.
     *
     * @return LocalDateTime
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * This method sets the last update date of a Customer object in LocalDateTime format.
     *
     * @param lastUpdate
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * This method returns the user who last updated the customer in String format.
     *
     * @return
     */
    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    /**
     * This method sets the user who last updated the customer in String format.
     *
     * @param lastUpdatedBy
     */
    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * This method returns the division ID of the Customer object.
     *
     * @return int
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * This method sets the division ID of the Customer object.
     *
     * @param divisionID
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
}
