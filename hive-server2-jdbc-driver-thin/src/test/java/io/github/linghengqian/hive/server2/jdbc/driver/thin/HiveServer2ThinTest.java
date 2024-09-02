/*
 * Copyright 2024 linghengqian@outlook.com
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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
@Testcontainers
public class HiveServer2ThinTest {

    @Container
    public static final GenericContainer<?> CONTAINER = new GenericContainer<>(DockerImageName.parse("apache/hive:4.0.0"))
            .withEnv("SERVICE_NAME", "hiveserver2")
            .withExposedPorts(10000, 10002);

    @Test
    void test() throws SQLException {
        HikariConfig config = new HikariConfig();
        String jdbcUrlPrefix = "jdbc:hive2://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(10000);
        config.setJdbcUrl(jdbcUrlPrefix + "/");
        config.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        await().atMost(Duration.of(30L, ChronoUnit.SECONDS)).until(() -> {
            try (HikariDataSource hikariDataSource = new HikariDataSource(config)) {
                hikariDataSource.getConnection().close();
            }
            return true;
        });
        try (HikariDataSource hikariDataSource = new HikariDataSource(config);
             Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE demo_ds_0");
            ResultSet firstResultSet = statement.executeQuery("show tables");
            assertThat(firstResultSet.next(), is(false));
            statement.execute("create table hive_example(a string, b int) partitioned by(c int)");
            statement.execute("alter table hive_example add partition(c=1)");
            statement.execute("insert into hive_example partition(c=1) values('a', 1), ('a', 2),('b',3)");
            ResultSet secondResultSet = statement.executeQuery("select count(distinct a) from hive_example");
            assertThat(secondResultSet.next(), is(true));
            assertThat(secondResultSet.getInt("_c0"), is(2));
            ResultSet thirdResultSet = statement.executeQuery("select sum(b) from hive_example");
            assertThat(thirdResultSet.next(), is(true));
            assertThat(thirdResultSet.getInt("_c0"), is(6));
        }
    }
}
