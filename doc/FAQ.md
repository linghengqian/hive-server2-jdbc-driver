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
