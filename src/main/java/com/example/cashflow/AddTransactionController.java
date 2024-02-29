package com.example.cashflow;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Currencies;
import model.Currency;

import java.io.IOException;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddTransactionController implements Initializable {
    private Controller controller;
    private Stage stage;
    private Scene scene;
    private Parent root;
    //Components
    @FXML
    public TextField textImport;
    @FXML
    public ChoiceBox<String> choiceCurrency;
    @FXML
    public TextField textType;
    @FXML
    private TextField textComment;
    @FXML
    public DatePicker datepicker;
    //Methods
    public void switchToMenu(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("yearView.fxml"));
        root = loader.load();

        YearViewController yearViewController = loader.getController();
        yearViewController.setController(this.controller);
        yearViewController.refresh();

        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void addTransaction(ActionEvent e) throws IOException {
        try {
            Float insertedImport = Float.parseFloat(textImport.getText());
            Currency insertedCurrency = Currencies.ParseCurrency(choiceCurrency.getValue());
            String insertedType = textType.getText();
            String insertedComment = isTextfieldEmpty(textComment) ? null : textComment.getText();
            LocalDate insertedDate = datepicker.getValue();

            if (insertedType.contains("<") || insertedType.contains(">")) {
                throw new IOException("Comment and Type fields cannot contain < or >.");
            }
            if (insertedComment != null && (insertedComment.contains("<") || insertedComment.contains(">"))) {
                throw new IOException("Comment and Type fields cannot contain < or >.");
            }
            if (insertedDate == null) {
                throw new DateTimeException("");
            }
            controller.addTransaction(insertedImport, insertedCurrency, insertedType, insertedComment, insertedDate);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("yearView.fxml"));
            root = loader.load();

            YearViewController yearViewController = loader.getController();
            yearViewController.setController(this.controller);
            yearViewController.refresh();

            stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (NumberFormatException e1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert a valid number format");
            alert.showAndWait();
        } catch (DateTimeException e2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please insert a valid date");
            alert.showAndWait();
        } catch (IOException e3) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e3.getMessage());
            alert.showAndWait();
        }
    }

    private boolean isTextfieldEmpty(TextField textField) {
        try {
            textField.getText();
        } catch (Exception exception) {
            return true;
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceCurrency.getItems().addAll("eur", "usd");
        choiceCurrency.setValue("eur");
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
