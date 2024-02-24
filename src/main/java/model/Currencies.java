package model;

public class Currencies {

    public static String CurrencyToString(Currency currency) {
        String ret = "currency";
        switch (currency) {
            case EUR -> ret = "eur";
            case USD -> ret = "usd";
        }
        return ret;
    }

    public static Currency ParseCurrency(String string) {
        Currency ret = Currency.UNKNOWN;
        if (string.equals("eur")) {
            ret = Currency.EUR;
        } else if (string.equals("usd")) {
            ret = Currency.USD;
        }
        return ret;
    }
}

