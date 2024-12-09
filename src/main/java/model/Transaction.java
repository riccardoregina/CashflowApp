package model;

import java.time.LocalDate;

public class Transaction {
    private Float value;
    private String type;
    private String comment;
    private LocalDate date;
    private Currency currency = Currency.UNKNOWN;
    public Transaction(Float value, Currency currency, String type, String comment, LocalDate date) {
        this.value = value;
        this.currency = currency;
        this.type = type;
        this.comment = comment;
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getType() {
        return type;
    }
    public String getComment() {
        return comment;
    }
    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "<" + value.toString() + ">" + "<" + Currencies.CurrencyToString(currency) + ">" + "<" + type + ">" + "<" + comment.toString() + ">" + "<" + date.toString() + ">";
    }

    public String toXML() {
        return "\t" + "<transaction>" + "\n" +
                "\t\t" + "<value>"+ value +"</value>" + "\n" +
                "\t\t" + "<type>" + type + "</type>" + "\n" +
                "\t\t" + "<comment>" + comment + "</comment>" + "\n" +
                "\t\t" + "<date>" + date + "</date>" + "\n" +
                "\t\t" + "<currency>" + type + "</currency>" + "\n" +
                "\t" + "</transaction>" + "\n";
    }
}
