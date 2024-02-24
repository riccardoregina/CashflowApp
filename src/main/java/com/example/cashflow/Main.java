package com.example.cashflow;

import controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("yearView.fxml"));
        Parent root = loader.load();

        YearViewController yearViewController = loader.getController();
        Controller controller = new Controller();
        controller.loadTransactionsFromFile();
        yearViewController.setController(controller);
        stage.setTitle("Cashflow App");
        Scene scene = new Scene(root, Color.WHITE);
        try {
            Image image = new Image("app-icon.png");
            stage.getIcons().add(image);
        } catch (Exception e) {
            System.out.println("Icon file not found.");
        }
        stage.setMinWidth(1080);
        stage.setMinHeight(620);
        stage.setScene(scene);
        yearViewController.refresh();
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
