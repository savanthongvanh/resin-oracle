package com.example.resinoracle.repository.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import com.example.resinoracle.domain.StateRecord;
import com.example.resinoracle.repository.StateRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class OracleStateRepository implements StateRepositoryPort {

    private static final String FIND_STATE_SQL =
            "SELECT STATE_CODE, STATE_NAME, CAPITAL, REGION, " +
            "TO_CHAR(UPDATED_AT AT TIME ZONE 'UTC', 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS UPDATED_AT_UTC " +
            "FROM US_STATES WHERE STATE_CODE = ?";

    private final DataSource dataSource;
    private final StateRowMapper rowMapper;

    @Autowired
    public OracleStateRepository(@Qualifier("oracleDataSource") DataSource dataSource) {
        this(dataSource, new StateRowMapper());
    }

    OracleStateRepository(DataSource dataSource, StateRowMapper rowMapper) {
        this.dataSource = dataSource;
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<StateRecord> findByAbbreviation(String abbreviation) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_STATE_SQL)) {
            statement.setString(1, abbreviation);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(rowMapper.map(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query Oracle for state " + abbreviation, ex);
        }
    }
}
