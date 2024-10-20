package com.epam.learn.elasticsearch.task4.models;

public class Address {
    private String country;
    private String town;

    public Address() {
        super();
    }

    public Address(String country, String town) {
        this.country = country;
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
