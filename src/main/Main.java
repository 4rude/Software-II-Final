package main;

import database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The Main class is the class that extends the Application library, which acts as an entry for a JFX application. It
 * is responsible for much of the lifetime of the created application. The start() method associated with the
 * Application is abstract so it is implemented below.
 */
public class Main extends Application {

    /**
     * The start() function is the genesis function of the GUI. It is in charge of loading the login scene/stage. It
     * also detects the default language of the users computer and creates a ArrayList of all the users information
     * found within the connected database.
     *
     * The following objects and functions can be used to change the default locale of the system:
     * Locale france = new Locale("fr", "FR");
     * Locale.setDefault(france);
     *
     * @param primaryStage
     * @throws Exception
     *
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        ResourceBundle rb = ResourceBundle.getBundle("main/Nat", Locale.getDefault());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view_controller/Login.fxml"), rb);
        view_controller.Login controller = new view_controller.Login();
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    /**
     * The main method starts the connection to the database specified in the DBConnection class, launches the
     * application, and then closes the database connection after the launch function is returned and the application
     * is closed.
     *
     * @param args
     */
    public static void main(String[] args) {

        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
