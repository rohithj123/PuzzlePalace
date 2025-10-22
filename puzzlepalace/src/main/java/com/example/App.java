package com.example;

import java.io.IOException;
import java.net.URL;

import com.model.PuzzlePalaceFacade;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static final PuzzlePalaceFacade FACADE = new PuzzlePalaceFacade();

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        Parent root = loadFXML("login");
        scene = new Scene(root, 960, 600);
        stage.setScene(scene);
        stage.setTitle("Puzzle Palace");

        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        if (scene == null) {
            throw new IllegalStateException("Scene has not been initialised");
        }
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Use absolute path from the classpath root that matches your resources folder
        URL url = App.class.getResource("/com/puzzlepalace/" + fxml + ".fxml");
        System.out.println("DEBUG: FXML URL = " + url);
        if (url == null) {
            throw new IllegalStateException("FXML file not found: " + fxml + ".fxml");
        }
    
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static PuzzlePalaceFacade getFacade() {

        return FACADE;

    }


}