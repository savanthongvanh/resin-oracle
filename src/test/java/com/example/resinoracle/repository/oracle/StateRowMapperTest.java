package com.example.resinoracle.repository.oracle;

import java.sql.ResultSet;

import com.example.resinoracle.domain.StateRecord;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StateRowMapperTest {

    @Test
    void shouldMapOracleRowToStateRecord() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("STATE_CODE")).thenReturn("TX");
        when(resultSet.getString("STATE_NAME")).thenReturn("Texas");
        when(resultSet.getString("CAPITAL")).thenReturn("Austin");
        when(resultSet.getString("REGION")).thenReturn("South");
        when(resultSet.getString("UPDATED_AT_UTC")).thenReturn("2026-03-27T00:00:00Z");

        StateRowMapper mapper = new StateRowMapper();
        StateRecord record = mapper.map(resultSet);

        assertEquals("TX", record.getAbbreviation());
        assertEquals("Texas", record.getName());
        assertEquals("Austin", record.getCapital());
        assertEquals("South", record.getRegion());
        assertEquals("2026-03-27T00:00:00Z", record.getUpdatedAt());
    }
}
