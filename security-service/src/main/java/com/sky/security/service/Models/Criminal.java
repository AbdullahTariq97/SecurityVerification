package com.sky.security.service.Models;

import java.util.List;

public class Criminal {
    private String firstName;
    private String lastName;
    private int age;
    private List<Crime> crimes;

    public Criminal(String firstName, String lastName, int age, List<Crime> crimes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.crimes = crimes;
    }

    public Criminal() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Crime> getCrimes() {
        return crimes;
    }

    public void setCrimes(List<Crime> crimes) {
        this.crimes = crimes;
    }
}
