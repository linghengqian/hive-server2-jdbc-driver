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

package io.github.linghengqian.hive.server2.jdbc.driver.uber;

import io.github.linghengqian.hive.server2.jdbc.driver.uber.util.ImageUtils;
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
public class CostBasedOptimizerTest {
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
            // Enable cost-based optimizer settings
            statement.execute("set hive.cbo.enable=true");
            statement.execute("set hive.compute.query.using.stats=true");
            statement.execute("set hive.stats.autogather=true");
            statement.execute("set hive.stats.column.autogather=true");
            
            // Create tables for cost-based optimization testing
            statement.execute("CREATE TABLE IF NOT EXISTS customers (\n" +
                    "    customer_id INT,\n" +
                    "    name STRING,\n" +
                    "    age INT,\n" +
                    "    city STRING\n" +
                    ") STORED AS ORC");
            
            statement.execute("CREATE TABLE IF NOT EXISTS orders (\n" +
                    "    order_id INT,\n" +
                    "    customer_id INT,\n" +
                    "    amount DOUBLE,\n" +
                    "    order_date STRING\n" +
                    ") STORED AS ORC");
            
            // Insert test data
            statement.executeUpdate("INSERT INTO customers VALUES " +
                    "(1, 'John', 25, 'New York'), " +
                    "(2, 'Jane', 30, 'Boston'), " +
                    "(3, 'Bob', 35, 'Chicago')");
            
            statement.executeUpdate("INSERT INTO orders VALUES " +
                    "(101, 1, 100.0, '2023-01-01'), " +
                    "(102, 2, 200.0, '2023-01-02'), " +
                    "(103, 1, 150.0, '2023-01-03')");
            
            // Generate statistics for cost-based optimization
            statement.execute("ANALYZE TABLE customers COMPUTE STATISTICS");
            statement.execute("ANALYZE TABLE orders COMPUTE STATISTICS");
            statement.execute("ANALYZE TABLE customers COMPUTE STATISTICS FOR COLUMNS");
            statement.execute("ANALYZE TABLE orders COMPUTE STATISTICS FOR COLUMNS");
            
            // Test join query that benefits from cost-based optimization
            ResultSet joinResult = statement.executeQuery(
                    "SELECT c.name, SUM(o.amount) as total_amount " +
                    "FROM customers c " +
                    "JOIN orders o ON c.customer_id = o.customer_id " +
                    "GROUP BY c.name " +
                    "ORDER BY total_amount DESC");
            
            assertThat(joinResult.next(), is(true));
            assertThat(joinResult.getString("name"), is("John"));
            assertThat(joinResult.getDouble("total_amount"), is(250.0));
            
            assertThat(joinResult.next(), is(true));
            assertThat(joinResult.getString("name"), is("Jane"));
            assertThat(joinResult.getDouble("total_amount"), is(200.0));
            
            // Test query with selective WHERE clause
            ResultSet selectiveResult = statement.executeQuery(
                    "SELECT COUNT(*) as count FROM customers WHERE age > 28");
            assertThat(selectiveResult.next(), is(true));
            assertThat(selectiveResult.getInt("count"), is(2));
            
            statement.execute("DROP TABLE IF EXISTS customers");
            statement.execute("DROP TABLE IF EXISTS orders");
        }
    }
}