package controller;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

public class Controller {
    private Register register;
    private Exchange exchange;
    private Currency defaultCurrency = Currency.EUR;

    private Path filePath;
    private Path defaultFilePath = Paths.get(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "history_location.config");
    public Controller() {
        register = new Register();
        exchange = new Exchange();
        setHistoryLocationFile();
        filePath = getFilepath();
    }

    public void setHistoryLocationFile() {
        try {
            Files.createFile(defaultFilePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(defaultFilePath.toString()))) {
                writer.write(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "history.cashflow");
            } catch (IOException e) {
                System.out.println("An error occurred while writing on file");
            }
        } catch (IOException e) {
            System.out.println("Couldn't create the default file: it may exist already.");
        }
    }

    public Path getFilepath() {
        Path path = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(defaultFilePath.toString()))) {
            path = Paths.get(reader.readLine());
        } catch (IOException e) {
            System.out.println("Couldn't find the history_location.config file.");
        }
        return path;
    }

    public void loadTransactionsFromFile() {
        register.getTransactions().clear();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.filePath.toString()))) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                //Decode line into Transaction
                Float importRead;
                String currencyRead;
                String typeRead;
                String commentRead;
                LocalDate dateRead;
                //Fetch of import field
                int i, begin, end;
                begin = end = 0;
                for (i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        begin = i+1;
                        continue;
                    }
                    if (line.charAt(i) == '>') {
                        end = i;
                        i++;
                        break;
                    }
                }
                importRead = Float.parseFloat(line.substring(begin, end));
                //Fetch of currency field
                for (; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        begin = i+1;
                        continue;
                    }
                    if (line.charAt(i) == '>') {
                        end = i;
                        i++;
                        break;
                    }
                }
                currencyRead = line.substring(begin, end);
                //Fetch of type field
                for (; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        begin = i+1;
                        continue;
                    }
                    if (line.charAt(i) == '>') {
                        end = i;
                        i++;
                        break;
                    }
                }
                typeRead = line.substring(begin, end);
                //Fetch of comment field
                for (; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        begin = i+1;
                        continue;
                    }
                    if (line.charAt(i) == '>') {
                        end = i;
                        i++;
                        break;
                    }
                }
                commentRead = line.substring(begin, end);
                //Fetch of date field
                for (; i < line.length(); i++) {
                    if (line.charAt(i) == '<') {
                        begin = i+1;
                        continue;
                    }
                    if (line.charAt(i) == '>') {
                        end = i;
                        i++;
                        break;
                    }
                }
                dateRead = LocalDate.parse(line.substring(begin, end));

                this.register.getTransactions().add(new Transaction(importRead, Currencies.ParseCurrency(currencyRead), typeRead, commentRead, dateRead));
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the file: check if the file exists or if the file is well formatted.");
        }
    }
    public void addTransaction(Float value, Currency currency, String type, String comment, LocalDate date) {
        Transaction newTransaction = new Transaction(value, currency, type, comment, date);
        register.getTransactions().add(newTransaction);
        try {
            String toAdd = newTransaction.toString() + "\n";
            Files.write(filePath, toAdd.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Couldn't write on file: check if the file exists.");
        }
    }

    private Float getExchange(Currency currency1, Currency currency2) {
        //In the future: get the exchange values from open exchange rates API
        //But at this time we are poor...
        float conversion = 1F;
        if (currency1 == Currency.EUR && currency2 == Currency.USD) {
            conversion = exchange.getEurTOusd();
        } else if (currency1 == Currency.USD && currency2 == Currency.EUR) {
            conversion =  exchange.getUsdTOeur();
        }
        return conversion;
    }

    public void setExchange(Currency currency1, Currency currency2, Float value) {
        if (currency1 == Currency.EUR && currency2 == Currency.USD) {
            exchange.setEurTOusd(value);
        } else if (currency1 == Currency.USD && currency2 == Currency.EUR) {
            exchange.setUsdTOeur(value);
        }
    }

    public void setDefaultCurrency(Currency currency) {
        if (currency == Currency.USD || currency == Currency.EUR) {
            this.defaultCurrency = currency;
        } else {
            System.out.println("Insert a valid currency");
        }
    }

    /**
     *
     * @param imports - output parameter
     * @param currencies - output parameter
     * @param types - output parameter
     * @param dates - output parameter
     */
    public void getAllTransactions(ArrayList<Float> imports, ArrayList<Currency> currencies, ArrayList<String> types, ArrayList<String> comments, ArrayList<LocalDate> dates) {
        Iterator<Transaction> it = register.getTransactions().descendingIterator();
        while (it.hasNext()) {
            Transaction t = it.next();
            imports.add(t.getValue());
            currencies.add(t.getCurrency());
            types.add(t.getType());
            comments.add(t.getComment());
            dates.add(t.getDate());
        }
    }

    public void setFilePath(String path) {
        this.filePath = Paths.get(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.defaultFilePath.toString()))) {
            writer.write(path);
        } catch (IOException e) {
            System.out.println("Couldn't create the file: it may exist already");
        }
    }

    public Float getDayExpenses(LocalDate date) {
        float total = 0;
        for (Transaction t : register.getTransactions()) {
            if (t.getDate().isEqual(date) && t.getValue() < 0) {
                float value = t.getValue();
                if (!t.getCurrency().equals(defaultCurrency)) {
                    value *= getExchange(t.getCurrency(), defaultCurrency);
                }
                total += value;
            }
        }
        return -total;
    }

    public Float getDayEarnings(LocalDate date) {
        float total = 0;
        for (Transaction t : register.getTransactions()) {
            if (t.getDate().isEqual(date) && t.getValue() > 0) {
                float value = t.getValue();
                if (!t.getCurrency().equals(defaultCurrency)) {
                    value *= getExchange(t.getCurrency(), defaultCurrency);
                }
                total += value;
            }
        }
        return total;
    }

    /**
     * Returns two lists: the i-th element of expensesByType is the earning by the i-th element of types.
     *
     * @param types - output parameter
     * @param earningsByType - output parameter
     */
    public void getEarningsByTypeInPeriod(ArrayList<String> types, ArrayList<Float> earningsByType, LocalDate startOfPeriod, LocalDate endOfPeriod) {
        for (Transaction t : register.getTransactions()) {
            if ( (t.getDate().isAfter(startOfPeriod) || t.getDate().isEqual(startOfPeriod)) &&
                    (t.getDate().isBefore(endOfPeriod) || t.getDate().isEqual(endOfPeriod)) &&
                    t.getValue() > 0 ) {

                int index = types.indexOf(t.getType());
                float value = t.getValue();
                if (index == -1) {
                    types.add(t.getType());
                    earningsByType.add(value);
                } else {
                    if (!t.getCurrency().equals(defaultCurrency)) {
                        value *= getExchange(t.getCurrency(), defaultCurrency);
                    }
                    Float expense = earningsByType.get(index) + value;
                    earningsByType.set(index, expense);
                }
            }
        }
    }

    /**
     * Returns two lists: the i-th element of expensesByType is the expense by the i-th element of types.
     *
     * @param types - output parameter
     * @param expensesByType - output parameter
     */
    public void getExpensesByTypeInPeriod(ArrayList<String> types, ArrayList<Float> expensesByType, LocalDate startOfPeriod, LocalDate endOfPeriod) {
        for (Transaction t : register.getTransactions()) {
            if ( (t.getDate().isAfter(startOfPeriod) || t.getDate().isEqual(startOfPeriod)) &&
                    (t.getDate().isBefore(endOfPeriod) || t.getDate().isEqual(endOfPeriod)) &&
                    t.getValue() < 0 ) {

                int index = types.indexOf(t.getType());
                float value = - t.getValue(); //The pieChart doesn't want negative values
                if (index == -1) {
                    types.add(t.getType());
                    expensesByType.add(value);
                } else {
                    if (!t.getCurrency().equals(defaultCurrency)) {
                        value *= getExchange(t.getCurrency(), defaultCurrency);
                    }
                    Float expense = expensesByType.get(index) + value;
                    expensesByType.set(index, expense);
                }
            }
        }
    }

    public float getExpensesInPeriod(LocalDate startOfPeriod, LocalDate endOfPeriod) {
        float total = 0;
        for (Transaction t : register.getTransactions()) {
            if ((t.getDate().isAfter(startOfPeriod) || t.getDate().isEqual(startOfPeriod)) &&
                    ((t.getDate().isBefore(endOfPeriod)) || t.getDate().isEqual(endOfPeriod)) &&
                    t.getValue() < 0) {
                float value = t.getValue();
                if (!t.getCurrency().equals(defaultCurrency)) {
                    value *= getExchange(t.getCurrency(), defaultCurrency);
                }
                total += value;
            }
        }
        return total;
    }

    public float getEarningsInPeriod(LocalDate startOfPeriod, LocalDate endOfPeriod) {
        float total = 0;
        for (Transaction t : register.getTransactions()) {
            if ((t.getDate().isAfter(startOfPeriod) || t.getDate().isEqual(startOfPeriod)) &&
                    ((t.getDate().isBefore(endOfPeriod)) || t.getDate().isEqual(endOfPeriod)) &&
                    t.getValue() > 0) {
                float value = t.getValue();
                if (!t.getCurrency().equals(defaultCurrency)) {
                    value *= getExchange(t.getCurrency(), defaultCurrency);
                }
                total += value;
            }
        }
        return total;
    }
}
