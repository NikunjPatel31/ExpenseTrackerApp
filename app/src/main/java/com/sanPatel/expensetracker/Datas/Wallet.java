package com.sanPatel.expensetracker.Datas;

import java.util.Date;

public class Wallet {
    private String walletName, timeStamp;
    private double initialBalance;
    private Date date;
    private int walletID, walletSync;

    public Wallet() {
    }

    public Wallet(String walletName, String timeStamp, double initialBalance, Date date, int walletID, int walletSync) {
        this.walletName = walletName;
        this.timeStamp = timeStamp;
        this.initialBalance = initialBalance;
        this.date = date;
        this.walletID = walletID;
        this.walletSync = walletSync;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getWalletID() {
        return walletID;
    }

    public void setWalletID(int walletID) {
        this.walletID = walletID;
    }

    public int getWalletSync() {
        return walletSync;
    }

    public void setWalletSync(int walletSync) {
        this.walletSync = walletSync;
    }
}
