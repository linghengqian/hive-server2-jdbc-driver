# Third-party builds of the HiveServer2 JDBC Driver

The purpose of the current project is to create a `Thin JAR` of HiveServer2 JDBC Driver.
All release products have been verified and usable in the GraalVM Native Image compiled by GraalVM CE For JDK 22.0.2 and GraalVM CE For JDK 23.

The steps to use directly in Maven are as follows.
The latest version is available
at https://central.sonatype.com/artifact/io.github.linghengqian/hive-server2-jdbc-driver-thin .

```xml

<dependencies>
    <dependency>
        <groupId>io.github.linghengqian</groupId>
        <artifactId>hive-server2-jdbc-driver-thin</artifactId>
        <version>{latest.version}</version>
    </dependency>
</dependencies>
```

The current project also provides a HiveServer2 JDBC Driver Uber JAR to simplify the steps of specifying the `classifier`.
The steps to use directly in Maven are as follows.
The latest version is at https://central.sonatype.com/artifact/io.github.linghengqian/hive-server2-jdbc-driver-uber .

```xml
<dependencies>
    <dependency>
        <groupId>io.github.linghengqian</groupId>
        <artifactId>hive-server2-jdbc-driver-uber</artifactId>
        <version>{latest.version}</version>
    </dependency>
</dependencies>
```

## Quick Start

Start a HiveServer2 instance through Docker Engine.

```bash
docker run -d -p 10000:10000 -p 10002:10002 --env SERVICE_NAME=hiveserver2 apache/hive:4.0.1
```

Use third-party builds of this project in any Maven project.

```xml
<dependencies>
    <dependency>
        <groupId>io.github.linghengqian</groupId>
        <artifactId>hive-server2-jdbc-driver-thin</artifactId>
        <version>1.6.0</version>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>4.0.3</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.2</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Simply enjoy the happiness that comes without dependency conflicts.

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
@SuppressWarnings("SqlNoDataSourceInspection")
public class ExampleTest {
    @Test
    void test() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:hive2://127.0.0.1:10000/");
        config.setDriverClassName("org.apache.hive.jdbc.HiveDriver");
        try (HikariDataSource dataSource = new HikariDataSource(config);
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE demo_ds_0");
            statement.executeQuery("show tables");
            statement.execute("create table hive_example(a string, b int) partitioned by(c int)");
            statement.execute("alter table hive_example add partition(c=1)");
            statement.execute("insert into hive_example partition(c=1) values('a', 1), ('a', 2),('b',3)");
            statement.executeQuery("select count(distinct a) from hive_example");
            statement.executeQuery("select sum(b) from hive_example");
        }
    }
}
```

## Compatibility

For the Docker Image of `apache/hive:4.0.0`, 
you can use `1.4.0` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

For the Docker Image of `apache/hive:4.0.1`, 
you can use `1.6.0` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

## Background

Refer to [Background](./doc/Background.md).

## Release Note

Refer to [CHANGELOG](./doc/CHANGELOG.md).

## Contributing

Refer to [CONTRIBUTING](./doc/CONTRIBUTING.md).

## License

Refer to [LICENSE](./LICENSE) and [NOTICE](./NOTICE).

The license applies to both the source code and the final JAR distributed on Maven Central.
