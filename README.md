# Third-party builds of the HiveServer2 JDBC Driver

The purpose of this project is to release HiveServer2 JDBC Driver and HiveServer2 Docker Image faster outside of ASF.

Use directly in Maven.

```xml
<dependency>
    <groupId>com.github.linghengqian.hive</groupId>
    <artifactId>hive-server2-jdbc-driver</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Background

The current project is actually a third-party build of HiveServer2 JDBC Driver. 
The release rhythm of apache/hive is conservative, 
and the latest changes of the master branch are rarely included in the latest version, 
but commits are filtered from the master branch. 
In order to conduct faster testing in downstream projects, 
this project was created by an individual developer.

More background at https://lists.apache.org/thread/63lr45kyh78s64spwsjxjy8zdyzprnz1 .

## Release Note

The current project has not yet carried out any release work.
