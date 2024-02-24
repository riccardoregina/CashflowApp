module com.example.cashflow {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.cashflow to javafx.fxml;
    exports com.example.cashflow;
}