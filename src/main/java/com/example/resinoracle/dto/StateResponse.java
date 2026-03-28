package com.example.resinoracle.dto;

import com.example.resinoracle.domain.StateRecord;

public class StateResponse {
    private final String abbreviation;
    private final String name;
    private final String capital;
    private final String region;
    private final String updatedAt;

    public StateResponse(String abbreviation, String name, String capital, String region, String updatedAt) {
        this.abbreviation = abbreviation;
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.updatedAt = updatedAt;
    }

    public static StateResponse from(StateRecord record) {
        return new StateResponse(
                record.getAbbreviation(),
                record.getName(),
                record.getCapital(),
                record.getRegion(),
                record.getUpdatedAt()
        );
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

