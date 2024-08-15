package com.github.linghengqian.hive.server2.jdbc.driver.uber;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.awaitility.Awaitility.await;

@SuppressWarnings({"SqlNoDataSourceInspection", "resource"})
@Testcontainers
public class HiveServer2Test {

    @Container
    public static final GenericContainer<?> CONTAINER = new GenericContainer<>(DockerImageName.parse("apache/hive:4.1.0-SNAPSHOT"))
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
            statement.executeQuery("show tables");
            statement.execute("create table hive_example(a string, b int) partitioned by(c int)");
            statement.execute("insert into hive_example partition(c=1) values('a', 1), ('a', 2),('b',3)");
            statement.executeQuery("select count(distinct a) from hive_example");
            statement.executeQuery("select sum(b) from hive_example");
        }
    }
}
