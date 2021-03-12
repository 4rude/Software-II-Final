package model;

/**
 * @author matt
 *
 * This model class defines the properties and getters/setters for
 * the Country model class.
 */

public class Country {

    private int countryID;
    private String country;

    /**
     * This method is the constructor for the Country class, it is used to create a Country instance.
     *
     * @param countryID
     * @param country
     */
    public Country(int countryID, String country) {
        this.countryID = countryID;
        this.country = country;
    }

    /**
     * This method returns the country ID in integer format.
     *
     * @return int
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * This method sets the country ID for a Country object.
     *
     * @param countryID
     */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    /**
     * This method returns the name of the Country in String format.
     *
     * @return String
     */
    public String getCountry() {
        return country;
    }

    /**
     * This method sets the name of a country for a Country object.
     * @param country
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
