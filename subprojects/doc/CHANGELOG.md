# Release Note

## v2

Build from `apache/hive:rel/release-4.1.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:2.0.0-SNAPSHOT
io.github.linghengqian:hive-server2-jdbc-driver-uber:2.0.0-SNAPSHOT
```

## v1

### 1.8.1

1. Only configure `native-image.properties` related to `io.grpc:grpc-netty-shaded` in Thin JAR.
   This is because the Uber JAR of HiveServer2 JDBC Driver does not use `io.grpc:grpc-netty-shaded`.
2. Add more GraalVM Reachability Metadata for `org.apache.hive:hive-jdbc:4.0.1:standalone`.
3. Remove Thin JAR's use of `native-image.properties`.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.8.1
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.8.1
```

### 1.8.0

1. Introduce a new module `io.github.linghengqian:hive-server2-jdbc-driver-reachability-metadata` to store GraalVM Reachability Metadata for third-party dependencies.
2. Add more GraalVM Reachability Metadata for `commons-logging:commons-logging:1.1.3`.
3. Add more GraalVM Reachability Metadata for `org.apache.hadoop:hadoop-common:3.3.6`.
4. Add more GraalVM Reachability Metadata for `org.apache.hive:hive-jdbc:4.0.1`.
5. Add more GraalVM Reachability Metadata for `org.apache.hive:hive-jdbc:4.0.1:standalone`.
6. Add more GraalVM Reachability Metadata for `org.apache.zookeeper:zookeeper:3.8.3`.
7. Add more GraalVM Reachability Metadata for `org.slf4j:slf4j-api:1.7.30`.
8. Introduce new subproject `tinycircus` for distributing Linux Containers.
9. Support using Thin JAR and Uber JAR of HiveServer2 JDBC Driver under `OpenJDK 24`.
10. Supports using Thin JAR and Uber JAR of HiveServer2 JDBC Driver in GraalVM Native Image compiled by `GraalVM CE For JDK 24`.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.8.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.8.0
```

### 1.7.0

1. Fix GraalVM CE warning about
`Warning:  Properties file at 'jar:file:///home/runner/.m2/repository/io/github/linghengqian/hive-server2-jdbc-driver-thin/1.6.0/hive-server2-jdbc-driver-thin-1.6.0.jar!/META-INF/native-image/io.grpc/grpc-netty-shaded/native-image.properties' does not match the recommended 'META-INF/native-image/io.github.linghengqian/hive-server2-jdbc-driver-thin/native-image.properties' layout.`.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.7.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.7.0
```

### 1.6.0

1. Support connecting to HiveServer2 with ZooKeeper Service Discovery enabled in GraalVM Native Image.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.6.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.6.0
```

### 1.5.0

1. Bump base HiveServer2 JDBC Driver version to `4.0.1`.
2. Support using Thin JAR and Uber JAR of HiveServer2 JDBC Driver under `OpenJDK 23`.
3. Supports using Thin JAR and Uber JAR of HiveServer2 JDBC Driver in GraalVM Native Image compiled by `GraalVM CE For JDK 23`.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.5.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.5.0
```

### 1.4.0

Updates Apache-2.0 license header.

Build from `apache/hive:rel/release-4.0.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.4.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.4.0
```

### 1.3.0

Fixes incorrect package used in unit tests.

Build from `apache/hive:rel/release-4.0.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.3.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.3.0
```

### 1.2.0

Keep in sync with the Zookeeper Client version of `org.apache.hive.shims:hive-shims-common:4.0.0`.
Tip: `apache/hive:master` is using `org.apache.zookeeper:zookeeper:3.8.4`,
while `apache/hive:rel/release-4.0.0` is using `org.apache.zookeeper:zookeeper:3.8.3`.

Build from `apache/hive:rel/release-4.0.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.2.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.2.0
```

### 1.1.0

Provides built-in GraalVM Reachability Metadata.

Build from `apache/hive:rel/release-4.0.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.1.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.1.0
```

### 1.0.0

This is the first `Thin JAR` build and an accompanying, intuitive `Uber JAR` build for the HiveServer2 JDBC driver from
`apache/hive:rel/release-4.0.0`.
Fixed all class conflicts.

Build from `apache/hive:rel/release-4.0.0`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.0.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.0.0
```
