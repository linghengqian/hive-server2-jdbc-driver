# Third-party builds of the HiveServer2 JDBC Driver

The purpose of this project is to release HiveServer2 JDBC Driver and HiveServer2 Docker Image faster outside of ASF.

Use directly in Maven.

```xml

<dependencies>
    <dependency>
        <groupId>io.github.linghengqian</groupId>
        <artifactId>hive-server2-jdbc-driver-thin</artifactId>
        <version>{latest.version}</version>
    </dependency>
</dependencies>
```

Or use the Uber JAR of the HiveServer2 JDBC Driver.

```xml

<dependencies>
    <dependency>
        <groupId>io.github.linghengqian</groupId>
        <artifactId>hive-server2-jdbc-driver-uber</artifactId>
        <version>{latest.version}</version>
    </dependency>
</dependencies>
```

## Background

The current project is actually a third-party build of HiveServer2 JDBC Driver.
The release rhythm of apache/hive is conservative,
and the latest changes of the master branch are rarely included in the latest version,
but commits are filtered from the master branch.
In order to conduct faster testing in downstream projects,
this project was created by an individual developer.

It is designed to be easily integrated into the nativeTest form of the GraalVM Native Image of `apache/shardingsphere`
or other projects.

More background at https://lists.apache.org/thread/63lr45kyh78s64spwsjxjy8zdyzprnz1 .

## Release Note

The current project has not yet carried out any release work.

## contribute

Refer to [CONTRIBUTING](./doc/CONTRIBUTING.md) .
