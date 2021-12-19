package com.sky.security.service.Models;

public class Crime {

    private String date;
    private String typeOfCrime;


    public Crime(String date, String typeOfCrime) {
        this.date = date;
        this.typeOfCrime = typeOfCrime;
    }

    public Crime() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeOfCrime() {
        return typeOfCrime;
    }

    public void setTypeOfCrime(String typeOfCrime) {
        this.typeOfCrime = typeOfCrime;
    }
}
