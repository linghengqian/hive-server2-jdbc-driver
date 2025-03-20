# FAQ

## Where is the `org.apache.hadoop.mapred.JobConf` class?

For HiveServer2 JDBC Driver `org.apache.hive:hive-jdbc:4.0.1` or `org.apache.hive:hive-jdbc:4.0.1` with `classifier` as `standalone`,
there is actually no additional dependency on `org.apache.hadoop:hadoop-mapreduce-client-core:3.3.6`.

In some cases, users may need to do this.

```java
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.MetaException;
public class ExampleTest {

    void test() throws MetaException {
        HiveConf hiveConf = new HiveConf();
        hiveConf.set("hive.metastore.uris", "thrift://metastore:9083");
        HiveMetaStoreClient storeClient = new HiveMetaStoreClient(hiveConf);
        storeClient.close();
    }
}
```

Using `org.apache.hadoop.hive.conf.HiveConf` specifically requires a dependency on the `org.apache.hadoop.mapred.JobConf` class. 
This class belongs to `org.apache.hadoop:hadoop-mapreduce-client-core:3.3.6`.

When users need to use `org.apache.hadoop.hive.conf.HiveConf` directly in business code, 
they can additionally introduce the following dependencies.
Since only the `org.apache.hadoop.mapred.JobConf` class is needed, excluding all sub-dependencies is no problem.

```xml
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-mapreduce-client-core</artifactId>
    <version>3.3.6</version>
    <exclusions>
        <exclusion>
            <groupId>*</groupId>
            <artifactId>*</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## Why doesn't the project have CI for the Runner for `windows-latest`?

When we talk about the `windows-latest` Runner, we are referring to `Windows Server 2025`, 
and `Windows Server 2025` on Github Actions only supports creating Windows containers, 
not Linux containers.

The project's unit tests currently only use Linux containers, and `Windows Server 2025` actually only supports Windows containers. 
`Ubuntu 22.04` or `Windows 11` have ways to run Linux containers, 
so this does not affect local testing.

The project's unit tests also make extensive use of `testcontainers-java`. 
However, `testcontainers-java` explicitly does not support Windows Server, 
which is documented at https://github.com/testcontainers/testcontainers-java/issues/2960.

`Windows 11` can create Linux containers by installing `Docker Desktop For Windows`,
`Rancher Desktop For Windows`, `Podman Desktop For Windows`, or `Podman CLI For Windows`.
