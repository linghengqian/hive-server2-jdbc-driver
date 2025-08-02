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
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
@Testcontainers
public class InformationSchemaTest {

    @AutoClose
    private static final Network NETWORK = Network.newNetwork();

    @Container
    @AutoClose
    private static final GenericContainer<?> POSTGRES = new GenericContainer<>(ImageUtils.POSTGRES_IMAGE)
            .withEnv("POSTGRES_PASSWORD", "example")
            .withNetwork(NETWORK)
            .withNetworkAliases("some-postgres");

    @Container
    @AutoClose
    private static final GenericContainer<?> HS2 = new GenericContainer<>(ImageUtils.HIVE_ALL_IN_ONE_IMAGE)
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
                "/opt/hive/bin/schematool", "-initSchema",
                "-dbType", "hive",
                "-metaDbType", "postgres",
                "-url", "jdbc:hive2://localhost:10000/default"
        );
        assertThat(initResult.getStdout(), stringContainsInOrder(
                "main WARN The use of package scanning to locate Log4j plugins is deprecated.\n",
                "Please remove the `packages` attribute from your configuration file.\n",
                "See https://logging.apache.org/log4j/2.x/faq.html#package-scanning for details.\n",
                "main INFO Starting configuration org.apache.logging.log4j.core.config.properties.PropertiesConfiguration@14f9390f...\n",
                "main INFO Start watching for changes to /opt/hive/conf/hive-log4j2.properties every 0 seconds\n",
                "main INFO Configuration org.apache.logging.log4j.core.config.properties.PropertiesConfiguration@14f9390f started.\n",
                "main INFO Stopping configuration org.apache.logging.log4j.core.config.DefaultConfiguration@34f5090e...\n",
                "main INFO Configuration org.apache.logging.log4j.core.config.DefaultConfiguration@34f5090e stopped.\n",
                "INFO [main] conf.MetastoreConf: Found configuration file: file:/opt/hive/conf/hive-site.xml\n",
                "INFO [main] conf.MetastoreConf: Unable to find config file: hivemetastore-site.xml\n",
                "INFO [main] conf.MetastoreConf: Unable to find config file: metastore-site.xml\n",
                "Initializing the schema to: 4.1.0\n",
                "INFO [main] schematool.HiveSchemaHelper: Metastore connection URL:\t jdbc:hive2://localhost:10000/default\n",
                "Metastore connection URL:\t jdbc:hive2://localhost:10000/default\n",
                "INFO [main] schematool.HiveSchemaHelper: Metastore connection Driver :\t org.apache.hive.jdbc.HiveDriver\n",
                "Metastore connection Driver :\t org.apache.hive.jdbc.HiveDriver\n",
                "INFO [main] schematool.HiveSchemaHelper: Metastore connection User:\t APP\n",
                "Metastore connection User:\t APP\n",
                "Starting metastore schema initialization to 4.1.0\n",
                "Initialization script hive-schema-4.1.0.hive.sql\n",
                "INFO [main] conf.HiveConf: Found configuration file file:/opt/hive/conf/hive-site.xml\n",
                "INFO [main] conf.HiveConf: Found configuration file null\n",
                "INFO [main] conf.HiveConf: Found configuration file null\n",
                "INFO [main] conf.HiveConf: Found configuration file null\n",
                "WARN [main] schematool.HiveSchemaTool: Hive conf variable hive.hook.proto.base-directory is not set for creating protologging tables\n",
                "WARN [main] schematool.HiveSchemaTool: Tez conf variable tez.history.logging.proto-base-dir is not set for creating protologging tables\n",
                "Initialization script completed\n"
        ));
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
