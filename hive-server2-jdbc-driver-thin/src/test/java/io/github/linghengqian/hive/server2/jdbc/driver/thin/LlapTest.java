/*
 * Copyright 2025 Qiheng He
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.linghengqian.hive.server2.jdbc.driver.thin;

import io.github.linghengqian.hive.server2.jdbc.driver.thin.util.ImageUtils;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
@Testcontainers
public class LlapTest {
    @Container
    public static final GenericContainer<?> CONTAINER = new GenericContainer<>(ImageUtils.HIVE_IMAGE)
            .withEnv("SERVICE_NAME", "hiveserver2")
            .withExposedPorts(10000);

    @Test
    void test() throws SQLException {
        String jdbcUrlPrefix = "jdbc:hive2://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(10000);
        await().atMost(Duration.of(30L, ChronoUnit.SECONDS)).ignoreExceptions().until(() -> {
            DriverManager.getConnection(jdbcUrlPrefix).close();
            return true;
        });
        try (Connection connection = DriverManager.getConnection(jdbcUrlPrefix);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE demo_ds_0");
        }
        try (Connection connection = DriverManager.getConnection(jdbcUrlPrefix + "/demo_ds_0");
             Statement statement = connection.createStatement()) {
            // Enable Tez execution engine and optimization settings
            // Note: LLAP is not available in the standard Hive container, so we use Tez
            statement.execute("set hive.execution.engine=tez");
            statement.execute("set hive.vectorized.execution.enabled=true");
            statement.execute("set hive.vectorized.execution.reduce.enabled=true");
            
            // Create a simple table for optimization testing
            statement.execute("CREATE TABLE IF NOT EXISTS optimization_test_table (\n" +
                    "    id INT,\n" +
                    "    name STRING,\n" +
                    "    value DOUBLE\n" +
                    ") STORED AS ORC");
            
            // Insert test data
            statement.executeUpdate("INSERT INTO optimization_test_table VALUES (1, 'test1', 10.5), (2, 'test2', 20.5), (3, 'test3', 30.5)");
            
            // Query with Tez optimizations
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) as count FROM optimization_test_table WHERE value > 15.0");
            assertThat(resultSet.next(), is(true));
            assertThat(resultSet.getInt("count"), is(2));
            
            // Test aggregation with Tez
            ResultSet aggregateResult = statement.executeQuery("SELECT SUM(value) as total_value FROM optimization_test_table");
            assertThat(aggregateResult.next(), is(true));
            assertThat(aggregateResult.getDouble("total_value"), is(61.5));
            
            statement.execute("DROP TABLE IF EXISTS optimization_test_table");
        }
    }
}