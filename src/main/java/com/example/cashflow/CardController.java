package com.example.cashflow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CardController implements Initializable {
    @FXML
    private VBox vbox;
    @FXML
    private Label labelType;
    @FXML
    private Label labelComment;
    @FXML
    private Label labelDate;
    @FXML
    private Label labelImport;
    @FXML
    private Label labelCurrency;

    private String[] colors = {"#f27457", "#89db76"};
    public void setData(Float value, String currency, String type, String comment, LocalDate date) {
        labelType.setText(type);
        labelDate.setText(date.toString());
        labelComment.setText(comment);
        labelImport.setText(value.toString());
        labelCurrency.setText(currency);

        String color = (value < 0) ? colors[0] : colors[1];
        vbox.setStyle("-fx-background-color: " + color + ";" + "-fx-background-radius: 15;" +
                "-fx-effect: dropShadow(three-pass-box, rgba(0,0,0,0), 10, 0, 0, 10);");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
