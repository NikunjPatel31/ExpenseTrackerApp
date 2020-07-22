package com.sanPatel.expensetracker.Datas;

import java.util.Date;

public class Expense {
    String expense_id, expense_title, expense_description;
    double expense_amount;
    int expense_icon, expense_type;
    Date expense_date;

    public Expense(String expense_id, String expense_title, String expense_description, double expense_amount, int expense_icon, int expense_type, Date expense_date) {
        this.expense_id = expense_id;
        this.expense_title = expense_title;
        this.expense_description = expense_description;
        this.expense_amount = expense_amount;
        this.expense_icon = expense_icon;
        this.expense_type = expense_type;
        this.expense_date = expense_date;
    }

    public String getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(String expense_id) {
        this.expense_id = expense_id;
    }

    public String getExpense_title() {
        return expense_title;
    }

    public void setExpense_title(String expense_title) {
        this.expense_title = expense_title;
    }

    public String getExpense_description() {
        return expense_description;
    }

    public void setExpense_description(String expense_description) {
        this.expense_description = expense_description;
    }

    public double getExpense_amount() {
        return expense_amount;
    }

    public void setExpense_amount(double expense_amount) {
        this.expense_amount = expense_amount;
    }

    public int getExpense_icon() {
        return expense_icon;
    }

    public void setExpense_icon(int expense_icon) {
        this.expense_icon = expense_icon;
    }

    public int getExpense_type() {
        return expense_type;
    }

    public void setExpense_type(int expense_type) {
        this.expense_type = expense_type;
    }

    public Date getExpense_date() {
        return expense_date;
    }

    public void setExpense_date(Date expense_date) {
        this.expense_date = expense_date;
    }
}
