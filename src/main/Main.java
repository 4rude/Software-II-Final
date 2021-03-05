package main;

import database.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.User;
import DAO.UserDAO;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    /**
     * The start() function is the genesis function of the GUI. It is in charge of loading the login scene/stage. It
     * also detects the default language of the users computer and creates a ArrayList of all the users information
     * found within the connected database.
     *
     * @param primaryStage
     * @throws Exception
     *
     */
    @Override
    public void start(Stage primaryStage) throws Exception{

        // TESTING ZONE ** CHANGE IN PRODUCTION **
        //Locale france = new Locale("fr", "FR");
        //Locale.setDefault(france);
        // TESTING ZONE ** CHANGE IN PRODUCTION **

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


    public static void main(String[] args) {

        DBConnection.startConnection();
        launch(args);
        DBConnection.closeConnection();
    }
}
