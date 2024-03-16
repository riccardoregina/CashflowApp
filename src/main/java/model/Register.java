package model;

import java.util.TreeSet;
public class Register {
    private DateComparator comparator = new DateComparator();
    private TreeSet<Transaction> transactions = new TreeSet<>(comparator);
    public TreeSet<Transaction> getTransactions() {
        return transactions;
    }
}
