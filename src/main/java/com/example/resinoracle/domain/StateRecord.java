package com.example.resinoracle.domain;

public class StateRecord {
    private final String abbreviation;
    private final String name;
    private final String capital;
    private final String region;
    private final String updatedAt;

    public StateRecord(String abbreviation, String name, String capital, String region, String updatedAt) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.updatedAt = updatedAt;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getCapital() {
        return capital;
    }

    public String getRegion() {
        return region;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}

