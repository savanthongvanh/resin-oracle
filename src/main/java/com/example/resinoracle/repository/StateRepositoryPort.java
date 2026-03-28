package com.example.resinoracle.repository;

import java.util.Optional;

import com.example.resinoracle.domain.StateRecord;

public interface StateRepositoryPort {
    Optional<StateRecord> findByAbbreviation(String abbreviation);
}

