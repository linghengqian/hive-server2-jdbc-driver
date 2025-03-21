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
public class AcidTableTest {
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
            statement.execute("set metastore.compactor.initiator.on=true");
            statement.execute("set metastore.compactor.cleaner.on=true");
            statement.execute("set metastore.compactor.worker.threads=1");
            statement.execute("set hive.support.concurrency=true");
            statement.execute("set hive.exec.dynamic.partition.mode=nonstrict");
            statement.execute("set hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
            statement.execute("create table IF NOT EXISTS t_order (\n" +
                    "    order_id   BIGINT NOT NULL,\n" +
                    "    order_type INT,\n" +
                    "    user_id    INT    NOT NULL,\n" +
                    "    address_id BIGINT NOT NULL,\n" +
                    "    status     VARCHAR(50),\n" +
                    "    PRIMARY KEY (order_id) disable novalidate\n" +
                    ") CLUSTERED BY (order_id) INTO 2 BUCKETS STORED AS ORC TBLPROPERTIES ('transactional' = 'true')");
            statement.execute("TRUNCATE TABLE t_order");
            statement.executeUpdate("INSERT INTO t_order (order_id, user_id, order_type, address_id, status) VALUES (1, 1, 1, 1, 'INSERT_TEST')");
            ResultSet firstResultSet = statement.executeQuery("select * from t_order");
            assertThat(firstResultSet.next(), is(true));
            statement.executeUpdate("DELETE FROM t_order WHERE order_id=1");
            ResultSet secondResultSet = statement.executeQuery("select * from t_order");
            assertThat(secondResultSet.next(), is(false));
            statement.execute("DROP TABLE IF EXISTS t_order");
        }
    }
}
