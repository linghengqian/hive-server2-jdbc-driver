# Third-party builds of the HiveServer2 JDBC Driver

<p>
    <a>
        <img src="https://img.shields.io/badge/HotSpot VM-OpenJDK 8+-green.svg"  alt="">
    </a>
    <a>
        <img src="https://img.shields.io/badge/GraalVM Native Image-GraalVM CE For JDK 22.0.2+-blue.svg"  alt="">
    </a>
</p>

The purpose of the current project is to create a `Thin JAR` of HiveServer2 JDBC Driver.
All release products have been verified and usable in the GraalVM Native Image compiled by GraalVM CE For JDK 22.0.2 and GraalVM CE For JDK 23.0.2 .

For HotSpot VM, all JAR products can be run on any OpenJDK 8 and later distribution.

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

## Document

Refer to [QuickStart](./doc/QuickStart.md).

## Compatibility

For the Docker Image of `apache/hive:4.0.0`, 
you can use `1.4.0` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

For the Docker Image of `apache/hive:4.0.1`, 
you can use `1.7.0` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

## FAQ

Refer to [FAQ](./doc/FAQ.md).

## Background

Refer to [Background](./doc/Background.md).

## Release Note

Refer to [CHANGELOG](./doc/CHANGELOG.md).

## Contributing

Refer to [CONTRIBUTING](./doc/CONTRIBUTING.md).

## License

Refer to [LICENSE](./LICENSE) and [NOTICE](./NOTICE).

The license applies to both the source code and the final JAR distributed on Maven Central.
