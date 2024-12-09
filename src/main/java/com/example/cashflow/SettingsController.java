package com.example.cashflow;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Currency;

import java.io.IOException;
import java.util.Optional;

public class SettingsController {
    private Controller controller;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String caller;

    @FXML
    private TextField textfieldEURtoUSD;
    @FXML
    private TextField textfieldFilePath;
    @FXML
    private TextField textfieldUSDtoEUR;

    @FXML
    private Button generateXMLTranscript;

    public void generateXMLTranscript(ActionEvent e) {
        controller.generateXMLTranscript();
    }

    public void switchToMenu(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(this.caller));
        root = loader.load();

        if (this.caller.equals("yearView.fxml")) {
            YearViewController yearViewController = loader.getController();
            yearViewController.setController(this.controller);
            yearViewController.refresh();
        } else if (this.caller.equals("monthView.fxml")) {
            MonthViewController monthViewController = loader.getController();
            monthViewController.setController(controller);
            monthViewController.refresh();
        }

        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public boolean checkExtension(String filepath) {
        if (filepath.length() > 8) {
            return filepath.endsWith(".cashflow");
        }
        return false;
    }
    public void loadFile(ActionEvent e) {
        String path = textfieldFilePath.getText();
        if (path.isEmpty()) return;

        if (!checkExtension(path)) { //checking the file extension
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("The extension of the file should be \".cashflow\". Are you sure that this is the file you want to load?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        controller.setFilePath(path);
        controller.loadTransactionsFromFile();
        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setContentText("The file has been loaded successfully.");
        success.showAndWait();
    }

    public void setUSDtoEURexchange(ActionEvent e) {
        try {
            Float usdToEur = Float.parseFloat(textfieldUSDtoEUR.getText());
            controller.setExchange(Currency.USD, Currency.EUR, usdToEur);
        } catch (NumberFormatException e1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Insert a valid number");
            alert.showAndWait();
        }
    }

    public void setEURtoUSDexchange(ActionEvent e) {
        try {
            Float eurToUsd = Float.parseFloat(textfieldEURtoUSD.getText());
            controller.setExchange(Currency.EUR, Currency.USD, eurToUsd);
        } catch (NumberFormatException e1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Insert a valid number");
            alert.showAndWait();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }
}
