package com.example.expensetracker;

public class taskItem {

    private String list;
    private String name;
    private String date;
    private String amount;
    private int complete;


    taskItem(String list, String name, String date, String amount, int complete) {
        this.name = name;
        this.date = date;
        this.amount = amount;
        this.list = list;
        this.complete = complete;

    }

    public String getName() {
        return name;
    }

    String getDate() {
        return date;
    }

    String getAmount() {
        return amount;
    }

    String getList() {
        return list;
    }


    boolean getComplete() {
        return (complete == 1);
    }


    void setComplete(boolean c) {
        if (c) complete = 1;
        else complete = 0;
    }


}