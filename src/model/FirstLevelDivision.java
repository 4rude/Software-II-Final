package model;

/**
 * @author matt
 *
 * This model class defines the properties and getters/setters for
 * the FirstLevelDivision model class.
 */
public class FirstLevelDivision {

    private int divisionID;
    private String division;
    private int countryID;

    /**
     * This method is the constructor for the FirstLevelDivision class. It is used to instantiate a FirstLevelDivision
     * object.
     *
     * @param divisionID
     * @param division
     * @param countryID
     */
    public FirstLevelDivision(int divisionID, String division, int countryID) {
        this.divisionID = divisionID;
        this.division = division;
        this.countryID = countryID;
    }

    /**
     * This method returns the division ID from a FirstLevelDivision object.
     *
     * @return int
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * This method sets the division ID on a FirstLevelDivision object.
     *
     * @param divisionID
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * This method returns the name of the division associated with the FirstLevelDivision object.
     *
     * @return String
     */
    public String getDivision() {
        return division;
    }

    /**
     * This method sets the name of the division associated with the FirstLevelDivision object.
     *
     * @param division
     */
    public void setDivision(String division) {
        this.division = division;
    }

    /**
     * This method returns the country ID associated with a FirstLevelDivision object.
     *
     * @return int
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * This method sets the country ID associated with a FirstLevelDivision object.
     *
     * @param countryID
     */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
