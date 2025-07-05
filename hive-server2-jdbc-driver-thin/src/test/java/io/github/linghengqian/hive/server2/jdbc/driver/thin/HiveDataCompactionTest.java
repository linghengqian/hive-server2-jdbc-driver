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
public class HiveDataCompactionTest {
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
            // Enable compaction settings
            statement.execute("set metastore.compactor.initiator.on=true");
            statement.execute("set metastore.compactor.cleaner.on=true");
            statement.execute("set metastore.compactor.worker.threads=1");
            statement.execute("set hive.support.concurrency=true");
            statement.execute("set hive.exec.dynamic.partition.mode=nonstrict");
            statement.execute("set hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager");
            statement.execute("set hive.compactor.delta.num.threshold=2");
            statement.execute("set hive.compactor.delta.pct.threshold=0.1");
            
            // Create transactional table for compaction testing
            statement.execute("CREATE TABLE IF NOT EXISTS compaction_test (\n" +
                    "    id INT,\n" +
                    "    name STRING,\n" +
                    "    status STRING,\n" +
                    "    last_updated TIMESTAMP\n" +
                    ") CLUSTERED BY (id) INTO 2 BUCKETS STORED AS ORC TBLPROPERTIES ('transactional' = 'true')");
            
            // Insert initial data
            statement.executeUpdate("INSERT INTO compaction_test VALUES " +
                    "(1, 'record1', 'active', '2023-01-01 10:00:00'), " +
                    "(2, 'record2', 'active', '2023-01-01 10:00:00'), " +
                    "(3, 'record3', 'active', '2023-01-01 10:00:00')");
            
            // Update records to create delta files
            statement.executeUpdate("UPDATE compaction_test SET status = 'updated' WHERE id = 1");
            statement.executeUpdate("UPDATE compaction_test SET status = 'updated' WHERE id = 2");
            
            // Insert more data
            statement.executeUpdate("INSERT INTO compaction_test VALUES " +
                    "(4, 'record4', 'active', '2023-01-02 10:00:00'), " +
                    "(5, 'record5', 'active', '2023-01-02 10:00:00')");
            
            // Check data before compaction
            ResultSet beforeCompaction = statement.executeQuery("SELECT COUNT(*) as count FROM compaction_test");
            assertThat(beforeCompaction.next(), is(true));
            assertThat(beforeCompaction.getInt("count"), is(5));
            
            // Verify updated records
            ResultSet updatedRecords = statement.executeQuery("SELECT COUNT(*) as count FROM compaction_test WHERE status = 'updated'");
            assertThat(updatedRecords.next(), is(true));
            assertThat(updatedRecords.getInt("count"), is(2));
            
            // Request minor compaction
            statement.execute("ALTER TABLE compaction_test COMPACT 'minor'");
            
            // Check data after compaction request
            ResultSet afterCompaction = statement.executeQuery("SELECT COUNT(*) as count FROM compaction_test");
            assertThat(afterCompaction.next(), is(true));
            assertThat(afterCompaction.getInt("count"), is(5));
            
            // Verify updated records still exist
            ResultSet updatedAfterCompaction = statement.executeQuery("SELECT COUNT(*) as count FROM compaction_test WHERE status = 'updated'");
            assertThat(updatedAfterCompaction.next(), is(true));
            assertThat(updatedAfterCompaction.getInt("count"), is(2));
            
            // Check compaction status using SHOW COMPACTIONS
            ResultSet compactionStatus = statement.executeQuery("SHOW COMPACTIONS");
            boolean hasCompactionRecord = false;
            while (compactionStatus.next()) {
                String tableName = compactionStatus.getString("table");
                if ("compaction_test".equals(tableName)) {
                    hasCompactionRecord = true;
                    break;
                }
            }
            assertThat(hasCompactionRecord, is(true));
            
            statement.execute("DROP TABLE IF EXISTS compaction_test");
        }
    }
}