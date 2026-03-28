package com.example.resinoracle.service;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import com.example.resinoracle.domain.StateRecord;
import com.example.resinoracle.repository.StateRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class StateService {

    private static final Pattern STATE_CODE_PATTERN = Pattern.compile("^[A-Za-z]{2}$");

    private final StateRepositoryPort repository;

    public StateService(StateRepositoryPort repository) {
        this.repository = repository;
    }

    public StateRecord getState(String abbreviation) {
        String normalized = normalize(abbreviation);
        Optional<StateRecord> stateRecord = repository.findByAbbreviation(normalized);
        return stateRecord.orElseThrow(() -> new StateNotFoundException(normalized));
    }

    String normalize(String abbreviation) {
        if (abbreviation == null) {
            throw new InvalidStateAbbreviationException("State abbreviation must contain exactly two letters.");
        }

        String trimmed = abbreviation.trim();
        if (!STATE_CODE_PATTERN.matcher(trimmed).matches()) {
            throw new InvalidStateAbbreviationException("State abbreviation must contain exactly two letters.");
        }

        return trimmed.toUpperCase(Locale.US);
    }
}

