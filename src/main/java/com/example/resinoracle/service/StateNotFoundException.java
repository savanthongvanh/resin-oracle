package com.example.resinoracle.service;

public class StateNotFoundException extends RuntimeException {
    public StateNotFoundException(String abbreviation) {
        super("No state exists for abbreviation " + abbreviation);
    }
}

