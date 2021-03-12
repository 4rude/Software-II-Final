package model;

/**
 * @author matt
 *
 * This model class defines the properties and getters/setters for
 * the Contact model class.
 */
public class Contact {
    private int contactID;
    private String contactName;
    private String email;

    /**
     * This method is the constructor for the Contact class.
     *
     * @param contactID
     * @param contactName
     * @param email
     */
    public Contact(int contactID, String contactName, String email) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * This method returns the contact ID in integer format.
     * @return int
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * This method sets the contact ID for a Contact object.
     * @param contactID
     */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    /**
     * This method returns a contact name in String format.
     *
     * @return String
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * This method sets the contact name for a Contact object.
     *
     * @param contactName
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * This method returns the email property in String format.
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * This method sets the email property on a Contact object.
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
