package com.faceScan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        com.faceScan.dao.DBInit.init();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login_view.fxml"));
        primaryStage.setTitle("FaceScan - Logowanie");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
