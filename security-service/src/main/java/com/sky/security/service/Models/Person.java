package com.sky.security.service.Models;

public class Person {
    private String firstName;
    private String lastName;
    private String nationalInsuranceNumber;
    private int age;

    public Person(String firstName, String lastName, String nationalInsuranceNumber, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.age = age;
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

    public String getNationalInsuranceNumber() {
        return nationalInsuranceNumber;
    }

    public void setNationalInsuranceNumber(String nationalInsuranceNumber) {
        this.nationalInsuranceNumber = nationalInsuranceNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nationalInsuranceNumber='" + nationalInsuranceNumber + '\'' +
                ", age=" + age +
                '}';
    }
}
