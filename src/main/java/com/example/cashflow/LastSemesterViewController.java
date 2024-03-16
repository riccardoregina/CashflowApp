package com.example.cashflow;

import controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Currencies;
import model.Currency;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LastSemesterViewController implements Initializable {
    private Controller controller;
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Components
    @FXML
    private AreaChart<Integer, Float> areachartTransactions;
    @FXML
    private PieChart piechartExpenses;
    @FXML
    private PieChart piechartEarnings;
    @FXML
    private ChoiceBox<String> choiceMenu;
    @FXML
    private Label labelMoneySpent;
    @FXML
    private Label labelMoneyEarned;
    @FXML
    private Label labelBalance;
    @FXML
    private ChoiceBox<String> choiceDefaultCurrency;
    @FXML
    private VBox cardContainer;

    private String selectedMenuItem = "Last semester";

    //Methods
    public void switchToAddTransaction(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTransaction.fxml"));
        root = loader.load();

        AddTransactionController addTransactionController = loader.getController();
        addTransactionController.setController(controller);

        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToSettings(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        root = loader.load();

        SettingsController settingsController = loader.getController();
        settingsController.setController(controller);

        settingsController.setCaller("yearView.fxml");

        stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void getNextScene(ActionEvent e) {
        if (!choiceMenu.getValue().equals(selectedMenuItem)) {
            if (choiceMenu.getValue().equals("This month")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("monthView.fxml"));
                try {
                    root = loader.load();

                    MonthViewController monthViewController = loader.getController();
                    monthViewController.setController(controller);

                    monthViewController.setSelectedMenuItem(choiceMenu.getValue());

                    stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    monthViewController.refresh();
                    stage.show();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            } else if (choiceMenu.getValue().equals("This year")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("yearView.fxml"));
                try {
                    root = loader.load();

                    YearViewController yearViewController = loader.getController();
                    yearViewController.setController(controller);

                    yearViewController.setSelectedMenuItem(choiceMenu.getValue());

                    stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    yearViewController.refresh();
                    stage.show();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        choiceMenu.getItems().addAll("This year", "Last semester", "This month");
        choiceMenu.setValue("Last semester");
        choiceMenu.setOnAction(this::getNextScene);

        choiceDefaultCurrency.getItems().addAll("eur", "usd");
        choiceDefaultCurrency.setValue("eur");
        choiceDefaultCurrency.setOnAction(this::setDefaultCurrency);
    }



    public void refresh() {
        LocalDate endOfPeriod = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()));
        LocalDate startOfPeriod = endOfPeriod.minusMonths(6);

        float moneySpent = -controller.getExpensesInPeriod(startOfPeriod, endOfPeriod);
        float moneyEarned = controller.getEarningsInPeriod(startOfPeriod, endOfPeriod);
        float balance = moneyEarned - moneySpent;

        String moneySpentText = Float.toString(BigDecimal.valueOf(moneySpent).setScale(2, RoundingMode.HALF_EVEN)
                .floatValue());
        String moneyEarnedText = Float.toString(BigDecimal.valueOf(moneyEarned).setScale(2, RoundingMode.HALF_EVEN)
                .floatValue());
        String balanceText = Float.toString(BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_EVEN)
                .floatValue());

        labelMoneySpent.setText(moneySpentText);
        labelMoneyEarned.setText(moneyEarnedText);
        labelBalance.setText(balanceText);

        areachartTransactions.getData().clear();

        XYChart.Series<Integer, Float> expenses = new XYChart.Series<>();
        XYChart.Series<Integer, Float> earnings = new XYChart.Series<>();

        expenses.setName("Expenses");
        earnings.setName("Earnings");

        for (int i = 1; i <= 12; i++) {
            LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(i), 1);
            LocalDate endOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(i), Month.of(i).length(LocalDate.now().isLeapYear()));
            expenses.getData().add(new XYChart.Data<>(i, -controller.getExpensesInPeriod(startOfMonth, endOfMonth)));
            earnings.getData().add(new XYChart.Data<>(i, controller.getEarningsInPeriod(startOfMonth, endOfMonth)));
        }

        areachartTransactions.getData().addAll(expenses, earnings);
        areachartTransactions.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");

        piechartExpenses.getData().clear();
        ArrayList<String> typesExpenses = new ArrayList<>();
        ArrayList<Float> expensesByType = new ArrayList<>();
        controller.getExpensesByTypeInPeriod(typesExpenses, expensesByType, startOfPeriod, endOfPeriod);

        for (int i = 0; i < typesExpenses.size(); i++) {
            PieChart.Data slice = new PieChart.Data(typesExpenses.get(i), expensesByType.get(i));
            //slice.getNode().setStyle("-fx-pie-color: red;"); DOES NOT WORK
            piechartExpenses.getData().add(slice);
        }
        piechartExpenses.setLegendVisible(true);

        piechartEarnings.getData().clear();
        ArrayList<String> typesEarnings = new ArrayList<>();
        ArrayList<Float> earningsByType = new ArrayList<>();
        controller.getEarningsByTypeInPeriod(typesEarnings, earningsByType, startOfPeriod, endOfPeriod);

        for (int i = 0; i < typesEarnings.size(); i++) {
            PieChart.Data slice = new PieChart.Data(typesEarnings.get(i), earningsByType.get(i));
            //slice.getNode().setStyle("-fx-pie-color: red;"); DOES NOT WORK
            piechartEarnings.getData().add(slice);
        }

        //Transaction cards
        cardContainer.setSpacing(10);
        cardContainer.getChildren().clear();
        ArrayList<Float> imports = new ArrayList<>();
        ArrayList<Currency> currencies = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<LocalDate> date = new ArrayList<>();
        controller.getAllTransactions(imports, currencies, types, comments, date);
        for (int i = 0; i < imports.size(); i++) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("card.fxml"));
                AnchorPane cardBox = fxmlLoader.load();
                CardController cardController = fxmlLoader.getController();
                cardController.setData(imports.get(i), Currencies.CurrencyToString(currencies.get(i)), types.get(i), comments.get(i), date.get(i));
                cardContainer.getChildren().add(cardBox);
            } catch (IOException e) {
                System.out.println("Errore nel caricamento di una card");
            }
        }
    }

    public void setDefaultCurrency(ActionEvent e) {
        Currency current = Currencies.ParseCurrency(choiceDefaultCurrency.getValue());
        controller.setDefaultCurrency(current);
        this.refresh();
    }

    public void setSelectedMenuItem(String selectedMenuItem) {
        this.selectedMenuItem = selectedMenuItem;
    }
}
