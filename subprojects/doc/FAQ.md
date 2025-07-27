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

## Why do I get the exception `java.nio.charset.UnsupportedCharsetException: Cp1252` after compiling dependencies into GraalVM Native Image?

This does not normally happen on Ubuntu systems.

When compiling a GraalVM Native Image containing `io.github.linghengqian:hive-server2-jdbc-driver-thin` with Windows 11 Home 24H2, 
you may get an exception of `java.nio.charset.UnsupportedCharsetException: Cp1252`.

```shell
Jul 26, 2025 3:56:43 PM com.sun.jna.Native <clinit>
WARNING: Failed to get charset for native.encoding value : 'Cp1252'
java.nio.charset.UnsupportedCharsetException: Cp1252
	at java.base@22.0.2/java.nio.charset.Charset.forName(Charset.java:559)
	at com.sun.jna.Native.<clinit>(Native.java:133)
	at com.github.dockerjava.transport.NamedPipeSocket$Kernel32.<clinit>(NamedPipeSocket.java:82)
	at com.github.dockerjava.transport.NamedPipeSocket.connect(NamedPipeSocket.java:64)
	at com.github.dockerjava.transport.NamedPipeSocket.connect(NamedPipeSocket.java:43)
	at org.testcontainers.dockerclient.DockerClientProviderStrategy.lambda$test$3(DockerClientProviderStrategy.java:214)
	at org.testcontainers.shaded.org.awaitility.core.AssertionCondition.lambda$new$0(AssertionCondition.java:53)
	at org.testcontainers.shaded.org.awaitility.core.ConditionAwaiter$ConditionPoller.call(ConditionAwaiter.java:248)
	at org.testcontainers.shaded.org.awaitility.core.ConditionAwaiter$ConditionPoller.call(ConditionAwaiter.java:235)
	at java.base@22.0.2/java.util.concurrent.FutureTask.run(FutureTask.java:317)
	at java.base@22.0.2/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	at java.base@22.0.2/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	at java.base@22.0.2/java.lang.Thread.runWith(Thread.java:1583)
	at java.base@22.0.2/java.lang.Thread.run(Thread.java:1570)
	at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:853)
	at org.graalvm.nativeimage.builder/com.oracle.svm.core.thread.PlatformThreads.threadStartRoutine(PlatformThreads.java:829)
```

But if you encounter this Error Log, you can pass the `buildArg` of `-H:+AddAllCharsets` to GraalVM Native Build Tools. 
Possible Maven 3 examples are as follows,

```xml
<project>
     <dependencies>
         <dependency>
             <groupId>io.github.linghengqian</groupId>
             <artifactId>hive-server2-jdbc-driver-thin</artifactId>
             <version>1.7.0</version>
         </dependency>
     </dependencies>
    
     <build>
         <plugins>
             <plugin>
                 <groupId>org.graalvm.buildtools</groupId>
                 <artifactId>native-maven-plugin</artifactId>
                 <version>0.11.0</version>
                 <extensions>true</extensions>
                 <configuration>
                    <buildArgs>
                       <buildArg>-H:+AddAllCharsets</buildArg>
                    </buildArgs>
                 </configuration>
                 <executions>
                     <execution>
                         <id>build-native</id>
                         <goals>
                             <goal>compile-no-fork</goal>
                         </goals>
                         <phase>package</phase>
                     </execution>
                     <execution>
                         <id>test-native</id>
                         <goals>
                             <goal>test</goal>
                         </goals>
                         <phase>test</phase>
                     </execution>
                 </executions>
             </plugin>
         </plugins>
     </build>
</project>
```
