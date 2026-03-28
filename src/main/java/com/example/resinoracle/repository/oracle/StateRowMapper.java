package com.example.resinoracle.repository.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.resinoracle.domain.StateRecord;

public class StateRowMapper {

    public StateRecord map(ResultSet resultSet) throws SQLException {
        return new StateRecord(
                resultSet.getString("STATE_CODE"),
                resultSet.getString("STATE_NAME"),
                resultSet.getString("CAPITAL"),
                resultSet.getString("REGION"),
                resultSet.getString("UPDATED_AT_UTC")
        );
    }
}

