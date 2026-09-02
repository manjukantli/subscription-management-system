package com.subscription.management.dto;

public class MonthlySpendingResponse {

    private int year;
    private int month;
    private double total;

    public MonthlySpendingResponse() {
    }

    public MonthlySpendingResponse(int year, int month, double total) {
        this.year = year;
        this.month = month;
        this.total = total;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
