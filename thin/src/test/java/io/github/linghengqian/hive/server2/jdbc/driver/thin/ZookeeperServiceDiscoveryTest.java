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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.linghengqian.hive.server2.jdbc.driver.thin.util.ImageUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection", "resource", "deprecation"})
@Testcontainers
@Isolated("This unit test starts two or more Linux containers, which will hit the performance limit of GitHub Actions Runner")
class ZookeeperServiceDiscoveryTest {

    private static final int RANDOM_PORT_FIRST = getRandomPort();

    @AutoClose
    private static final Network NETWORK = Network.newNetwork();

    @Container
    @AutoClose
    private static final GenericContainer<?> ZOOKEEPER_CONTAINER = new GenericContainer<>("zookeeper:3.9.3-jre-17")
            .withNetwork(NETWORK)
            .withNetworkAliases("foo")
            .withExposedPorts(2181);

    @Container
    @AutoClose
    private static final GenericContainer<?> HIVE_SERVER2_1_CONTAINER = new FixedHostPortGenericContainer<>(ImageUtils.HIVE_IMAGE)
            .withNetwork(NETWORK)
            .withEnv("SERVICE_NAME", "hiveserver2")
            .withEnv("SERVICE_OPTS", "-Dhive.server2.support.dynamic.service.discovery=true" + " "
                    + "-Dhive.zookeeper.quorum=" + ZOOKEEPER_CONTAINER.getNetworkAliases().get(0) + ":2181" + " "
                    + "-Dhive.server2.thrift.bind.host=0.0.0.0" + " "
                    + "-Dhive.server2.thrift.port=" + RANDOM_PORT_FIRST)
            .withFixedExposedPort(RANDOM_PORT_FIRST, RANDOM_PORT_FIRST)
            .dependsOn(ZOOKEEPER_CONTAINER);

    private final String jdbcUrlSuffix = ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";

    private String jdbcUrlPrefix;

    @Test
    void assertShardingInLocalTransactions() throws SQLException {
        jdbcUrlPrefix = "jdbc:hive2://" + ZOOKEEPER_CONTAINER.getHost() + ":" + ZOOKEEPER_CONTAINER.getMappedPort(2181) + "/";
        DataSource dataSource = createDataSource();
        extracted(dataSource);
        HIVE_SERVER2_1_CONTAINER.stop();
        int randomPortSecond = getRandomPort();
        try (GenericContainer<?> hiveServer2SecondContainer = new FixedHostPortGenericContainer<>(ImageUtils.HIVE_IMAGE)
                .withNetwork(NETWORK)
                .withEnv("SERVICE_NAME", "hiveserver2")
                .withEnv("SERVICE_OPTS", "-Dhive.server2.support.dynamic.service.discovery=true" + " "
                        + "-Dhive.zookeeper.quorum=" + ZOOKEEPER_CONTAINER.getNetworkAliases().get(0) + ":2181" + " "
                        + "-Dhive.server2.thrift.bind.host=0.0.0.0" + " "
                        + "-Dhive.server2.thrift.port=" + randomPortSecond)
                .withFixedExposedPort(randomPortSecond, randomPortSecond)
                .dependsOn(ZOOKEEPER_CONTAINER)) {
            hiveServer2SecondContainer.start();
            extracted(hiveServer2SecondContainer.getMappedPort(randomPortSecond));
            extracted(dataSource);
        }
    }

    private static void extracted(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection();
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

    private DataSource createDataSource() {
        extracted(HIVE_SERVER2_1_CONTAINER.getMappedPort(RANDOM_PORT_FIRST));
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        config.setJdbcUrl(jdbcUrlPrefix + jdbcUrlSuffix);
        return new HikariDataSource(config);
    }

    private void extracted(final int hiveServer2Port) {
        String connectionString = ZOOKEEPER_CONTAINER.getHost() + ":" + ZOOKEEPER_CONTAINER.getMappedPort(2181);
        await().atMost(Duration.ofMinutes(2L)).ignoreExceptions().until(() -> {
            try (CuratorFramework client = CuratorFrameworkFactory.builder().connectString(connectionString)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build()) {
                client.start();
                List<String> children = client.getChildren().forPath("/hiveserver2");
                assertThat(children.size(), is(1));
                return children.get(0).startsWith("serverUri=0.0.0.0:" + hiveServer2Port + ";version=4.0.1;sequence=");
            }
        });
        await().atMost(Duration.ofMinutes(1L)).ignoreExceptions().until(() -> {
            DriverManager.getConnection(jdbcUrlPrefix + jdbcUrlSuffix).close();
            return true;
        });
    }

    private static int getRandomPort() {
        try (ServerSocket server = new ServerSocket(0)) {
            server.setReuseAddress(true);
            return server.getLocalPort();
        } catch (IOException exception) {
            throw new Error(exception);
        }
    }
}
