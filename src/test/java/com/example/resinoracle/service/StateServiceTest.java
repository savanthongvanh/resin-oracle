package com.example.resinoracle.service;

import java.util.Optional;

import com.example.resinoracle.domain.StateRecord;
import com.example.resinoracle.repository.StateRepositoryPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateServiceTest {

    @Test
    void shouldNormalizeInputBeforeLookup() {
        CapturingRepository repository = new CapturingRepository();
        StateService service = new StateService(repository);

        StateRecord record = service.getState("tx");

        assertEquals("TX", repository.lastLookup);
        assertEquals("TX", record.getAbbreviation());
    }

    @Test
    void shouldRejectInvalidAbbreviation() {
        StateService service = new StateService(code -> Optional.empty());

        InvalidStateAbbreviationException ex = assertThrows(
                InvalidStateAbbreviationException.class,
                () -> service.getState("TEX")
        );

        assertEquals("State abbreviation must contain exactly two letters.", ex.getMessage());
    }

    @Test
    void shouldThrowNotFoundWhenStateIsMissing() {
        StateService service = new StateService(code -> Optional.empty());

        StateNotFoundException ex = assertThrows(
                StateNotFoundException.class,
                () -> service.getState("ZZ")
        );

        assertEquals("No state exists for abbreviation ZZ", ex.getMessage());
    }

    private static final class CapturingRepository implements StateRepositoryPort {
        private String lastLookup;

        @Override
        public Optional<StateRecord> findByAbbreviation(String abbreviation) {
            lastLookup = abbreviation;
            return Optional.of(new StateRecord("TX", "Texas", "Austin", "South", "2026-03-27T00:00:00Z"));
        }
    }
}

