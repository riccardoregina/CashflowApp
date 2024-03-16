package model;

import java.util.Comparator;

public class DateComparator implements Comparator<Transaction> {

    @Override
    public int compare(Transaction o1, Transaction o2) {
        if (o1.getDate().isBefore(o2.getDate())) {
            return -1;
        } else if (o1.getDate().isAfter(o2.getDate())) {
            return 1;
        } else {
            return 0;
        }
    }
}
