package utilities;

import java.io.*;
import java.time.Instant;
import java.util.Scanner;

/**
 * The LoginIO class data & behavior is responsible for holding the file location of the login_activity.txt file, for
 * adding login attempts to the file, and returning all login attempts (line-by-line) to the requested user.
 */
public class LoginIO {
    // Get the location of the login_activity file
    //public static URL fileURL = LoginIO.class.getResource("/login_activity.txt");
    public static File findFile = new File("login_activity.txt");

    /**
     * This class method is a static method that uses the FileWriter and PrintWriter classes to open the
     * login_activity.txt, adds the current date & time to the file, adds whether the login attempt was successful to
     * the file, and the user data that was gathered from the Login ViewController's text fields to the txt file. The
     * connections to the file are then closed at the end to prevent errors.
     *
     * @param userName
     * @param userPassword
     * @param loginAttemptSuccess
     */
    // public static Method that adds login attempts to login_activity.txt file in src folder
    public static void addLoginAttempt(String userName, String userPassword, boolean loginAttemptSuccess) {
        try {
            // Create FileWriter and PrintWriter objects to access the text file data
            FileWriter writeToFile = new FileWriter(findFile, true);
            PrintWriter outputFile = new PrintWriter(writeToFile);

            // Get the current date & time and add it to a String object
            String currentDateTime = Instant.now().toString();
            String loginAttempt = "";
            // Put the login attempt into String format
            if (loginAttemptSuccess) {
                loginAttempt = "Successful";
            } else {
                loginAttempt = "Failed";
            }

            // Write data to the login_activity.txt file
            outputFile.println(" User Name: " + userName + " || User Password: " + userPassword +
                    " || Login Attempt Date & Time in UTC: " + currentDateTime + " || Attempt: " + loginAttempt);

            System.out.println("Added to file");
            // Close the connections to the file
            writeToFile.close();
            outputFile.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * The getLoginAttempt class method uses the Scanner class to get the data from the login_activity.txt file,
     * then reads the file line-by-line and adds that data to a String. The string is then returned from this method.
     *
     * @return String
     */
    // public static String method that gets user attempts from login_activity.txt file in src folder
    // Created for user in report #3
    public static String getLoginAttempts() {
        String allLoginAttempts = "";
        try {
            Scanner inputFile = new Scanner(findFile);

            while (inputFile.hasNext()) {
                allLoginAttempts += inputFile.nextLine() + System.lineSeparator();
            }

            System.out.println("Obtained from file");
        } catch (IOException e) {
            System.out.println(e);
        }

        return allLoginAttempts;
    }

}
