package com.github.linghengqian.hive.server2.jdbc.driver;

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

@SuppressWarnings("SqlNoDataSourceInspection")
@Testcontainers
public class HiveServer2Test {

    @SuppressWarnings("resource")
    @Container
    public static final GenericContainer<?> CONTAINER = new GenericContainer<>(
            DockerImageName.parse("apache/hive:4.1.0-SNAPSHOT")
    )
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
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrlPrefix + "/demo_ds_0");
        hikariConfig.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        try (HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
             Connection connection = hikariDataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String tableName = "testHiveDriverTable";
            statement.execute("drop table if exists " + tableName);
            statement.execute("create table " + tableName + " (key int, value string)");
            String sql = "show tables '" + tableName + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            sql = "describe " + tableName;
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                System.out.printf("%s\t%s%n", resultSet.getString(1), resultSet.getString(2));
            }
        }
    }
}
