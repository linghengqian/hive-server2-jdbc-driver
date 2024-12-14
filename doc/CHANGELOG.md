# Release Note

## v1

### 1.7.0-SNAPSHOT

1. Fix GraalVM CE warning about
`[WARNING] Properties file at 'jar:file:///home/root/.m2/repository/io/github/linghengqian/hive-server2-jdbc-driver-thin/1.6.0/hive-server2-jdbc-driver-thin-1.6.0.jar!/META-INF/native-image/io.grpc/grpc-netty-shaded/native-image.properties' does not match the recommended 'META-INF/native-image/io.github.linghengqian/hive-server2-jdbc-driver-thin/native-image.properties' layout.`.

Build from `apache/hive:rel/release-4.0.1`.

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.7.0-SNAPSHOT
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.7.0-SNAPSHOT
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
2. Support for Thin JAR and Uber JAR of HiveServer2 JDBC Driver under OpenJDK 23.
3. Supports using Thin JAR and Uber JAR of HiveServer2 JDBC Driver in GraalVM Native Image compiled by GraalVM CE For JDK23.

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

```
io.github.linghengqian:hive-server2-jdbc-driver-thin:1.0.0
io.github.linghengqian:hive-server2-jdbc-driver-uber:1.0.0
```
