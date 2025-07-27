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

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
@Testcontainers
public class InformationSchemaTest {

    @AutoClose
    private static final Network NETWORK = Network.newNetwork();

    @Container
    @AutoClose
    private static final GenericContainer<?> POSTGRES = new GenericContainer<>("postgres:17.2-bookworm")
            .withEnv("POSTGRES_PASSWORD", "example")
            .withNetwork(NETWORK)
            .withNetworkAliases("some-postgres");

    @Container
    @AutoClose
    private static final GenericContainer<?> HS2 = new GenericContainer<>("ghcr.io/linghengqian/hive:4.0.1-all-in-one")
            .withEnv("SERVICE_NAME", "hiveserver2")
            .withEnv("DB_DRIVER", "postgres")
            .withEnv("SERVICE_OPTS", "-Djavax.jdo.option.ConnectionDriverName=org.postgresql.Driver" + " " +
                    "-Djavax.jdo.option.ConnectionURL=jdbc:postgresql://some-postgres:5432/postgres" + " " +
                    "-Djavax.jdo.option.ConnectionUserName=postgres" + " " +
                    "-Djavax.jdo.option.ConnectionPassword=example")
            .withNetwork(NETWORK)
            .dependsOn(POSTGRES)
            .withExposedPorts(10000);

    @Test
    void test() throws SQLException, IOException, InterruptedException {
        String jdbcUrlPrefix = "jdbc:hive2://" + HS2.getHost() + ":" + HS2.getMappedPort(10000);
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
            statement.execute("CREATE TABLE IF NOT EXISTS t_order (\n" +
                    "    order_id   BIGINT NOT NULL,\n" +
                    "    order_type INT,\n" +
                    "    user_id    INT    NOT NULL,\n" +
                    "    address_id BIGINT NOT NULL,\n" +
                    "    status     string,\n" +
                    "    PRIMARY KEY (order_id) disable novalidate\n" +
                    ") STORED BY ICEBERG STORED AS ORC TBLPROPERTIES ('format-version' = '2')");
            statement.execute("TRUNCATE TABLE t_order");
            statement.executeUpdate("INSERT INTO t_order (order_id, user_id, order_type, address_id, status) VALUES (1, 1, 1, 1, 'INSERT_TEST')");
            ResultSet resultSet = statement.executeQuery("select * from t_order");
            assertThat(resultSet.next(), is(true));
            assertThat(resultSet.next(), is(false));
        }
        assertThrows(SQLException.class, () -> DriverManager.getConnection(jdbcUrlPrefix + "/information_schema").close());
        ExecResult initResult = HS2.execInContainer(
                "/opt/hive/bin/schematool",
                "-initSchema",
                "-dbType", "hive",
                "-metaDbType", "postgres",
                "-url", "jdbc:hive2://localhost:10000/default"
        );
        assertThat(initResult.getStdout(), is("Initializing the schema to: 4.0.0\n" +
                "Metastore connection URL:\t jdbc:hive2://localhost:10000/default\n" +
                "Metastore connection Driver :\t org.apache.hive.jdbc.HiveDriver\n" +
                "Metastore connection User:\t APP\n" +
                "Starting metastore schema initialization to 4.0.0\n" +
                "Initialization script hive-schema-4.0.0.hive.sql\n" +
                "Initialization script completed\n"));
        try (Connection connection = DriverManager.getConnection(jdbcUrlPrefix + "/information_schema");
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select TABLE_CATALOG,\n" +
                    "       TABLE_NAME,\n" +
                    "       COLUMN_NAME,\n" +
                    "       DATA_TYPE,\n" +
                    "       ORDINAL_POSITION,\n" +
                    "       IS_NULLABLE\n" +
                    "FROM INFORMATION_SCHEMA.COLUMNS\n" +
                    "WHERE TABLE_CATALOG = 'default'\n" +
                    "  AND TABLE_SCHEMA = 'demo_ds_0'\n" +
                    "  AND UPPER(TABLE_NAME) IN ('T_ORDER')\n" +
                    "ORDER BY ORDINAL_POSITION limit 100");
            assertThat(resultSet.next(), is(true));
        }
    }
}
