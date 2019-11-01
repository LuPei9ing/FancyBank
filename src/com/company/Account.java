package com.company;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

//class that defines an account with basic methods with abstract methods to fill in later in Checking, Savings, and Loan
public abstract class Account {
    private double balance;
    private double rate;
    private static Integer count = 0;
    private Integer index;
    private LinkedHashMap<Integer, Transaction> ts;
    private Integer transactionCounter;
    private String accountType;

    public Account(double b, Currency c, double fee, double r){
        transactionCounter = 0;
        balance = (b * c.getConversionToBaseRate()) - fee;
        index = count;
        count += 1;
        ts = new LinkedHashMap();
        rate = r;
    }

    public double getBalance() {
        return balance;
    }

    public double getRate(){
        return rate;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAccountType(){
        return accountType;
    }

    public void setAccountType(String aT){
        accountType = aT;
    }

    public void addBalance(double b){
        balance += b;
    }

    public void removeBalance(double b){
        balance -= b;
    }

    public Integer getIndex(){
        return index;
    }

    public Integer getCount(){
        return count;
    }

    public void addTransaction(Transaction t){
        ts.put(transactionCounter, t);
        transactionCounter += 1;
    }

    public Collection<Transaction> getAllTransactions(){
        return ts.values();
    }

    public abstract Transaction applyInterest();

    public abstract String toString();
}