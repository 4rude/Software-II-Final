package model;

/**
 * @author matt
 *
 * This is a model class that holds the properties and getters/setters
 * for the User object model.
 */
public class User {
    private int userID;
    private String userName;
    private String password;

    /**
     * This method is a constructor for the User object, which allows it to be instantiated.
     *
     * @param userID
     * @param userName
     * @param password
     */
    public User(int userID, String userName, String password) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /**
     * This method returns the User ID associated with a User object.
     *
     * @return int
     */
    public int getUserID() {
        return userID;
    }

    /**
     * This method sets the User ID associated with a User object.
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * This method returns the User name property associated with a User object.
     *
     * @return String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * This method sets the User name property of a User object.
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * This method returns the password property associated of a User object in String format.
     *
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * This method sets the password property of a User object.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
