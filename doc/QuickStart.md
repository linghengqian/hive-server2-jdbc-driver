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
