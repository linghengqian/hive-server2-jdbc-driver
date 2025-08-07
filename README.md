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

```mermaid
flowchart TD
    subgraph Fat_Uber["Uber JAR/Fat JAR"]
        subgraph Hollow["Hollow JAR"]
            subgraph App_Runtime["App Runtime"]
            end
        end
        subgraph Thin["Thin JAR"]
            subgraph Skinny_JAR["Skinny JAR"]
                subgraph App["App"]
                end
            end
            subgraph App_Dependencies["App Dependencies"]
            end
        end
    end
    classDef container fill: #313244, stroke: #6c7086, stroke-width: 1.5, color: #cdd6f4, stroke-dasharray: 0
    classDef nested fill: #1e1e2e, stroke: #89b4fa, stroke-width: 1.5, color: #a6adc8
    classDef accent1 fill: #b4befe55, stroke: #b4befe, stroke-width: 1.5, color: #cdd6f4
    classDef accent2 fill: #74c7ec55, stroke: #74c7ec, stroke-width: 1.5, color: #cdd6f4
    classDef accent3 fill: #f2cdcd55, stroke: #f2cdcd, stroke-width: 1.5, color: #cdd6f4
    classDef accent4 fill: #f5c2e755, stroke: #f5c2e7, stroke-width: 1.5, color: #cdd6f4
    class Fat_Uber container
    class Hollow nested
    class App_Runtime accent1
    class Thin nested
    class Skinny_JAR accent2
    class App accent3
    class App_Dependencies accent4
```

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

## Subprojects

- [tinycircus](subprojects/tinycircus/README.md), for distributing unit-test-friendly Linux Containers.

## Document

Refer to [QuickStart](subprojects/doc/QuickStart.md).

## Compatibility

### For HiveServer2 `4.0.x`

All release products have been verified and usable in the GraalVM Native Image compiled by `GraalVM CE For JDK 22.0.2` and `GraalVM CE For JDK 24.0.2`.

For HotSpot VM, all JAR products can be run on any `OpenJDK 8` and later distribution.

For the Docker Image of `apache/hive:4.0.0`, 
you can use `1.4.0` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

For the Docker Image of `apache/hive:4.0.1`, 
you can use `1.8.2` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.

### For HiveServer2 `4.1.x`

All release products have been verified and usable in the GraalVM Native Image compiled by `GraalVM CE For JDK 22.0.2` and `GraalVM CE For JDK 24.0.2`.

For HotSpot VM, all JAR products can be run on any `OpenJDK 17` and later distribution.

For the Docker Image of `apache/hive:4.1.0`,
you can use `2.0.0-SNAPSHOT` of `io.github.linghengqian:hive-server2-jdbc-driver-thin` or `io.github.linghengqian:hive-server2-jdbc-driver-uber`.
**This sub-task has not yet been completed.**

## FAQ

Refer to [FAQ](subprojects/doc/FAQ.md).

## Background

Refer to [Background](subprojects/doc/Background.md).

## Release Note

Refer to [CHANGELOG](subprojects/doc/CHANGELOG.md).

## Contributing

Refer to [CONTRIBUTING](subprojects/doc/CONTRIBUTING.md).

## License

Refer to [LICENSE](./LICENSE) and [NOTICE](./NOTICE).

The license applies to both the source code and the final JAR distributed on Maven Central.
