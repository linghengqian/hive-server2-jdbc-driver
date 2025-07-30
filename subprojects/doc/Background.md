# Background

The current project is actually a third-party build of HiveServer2 JDBC Driver.
`apache/hive` only provides `Skinny JAR` and `Uber JAR` for HiveServer2 JDBC Driver, which leads to many unexpected
situations
in downstream projects.

For `org.apache.hive:hive-jdbc:4.0.0` corresponding to `Skinny JAR`,
this leads to PRs like https://github.com/apache/shardingsphere/pull/31680
and https://github.com/apache/shardingsphere/pull/31774 .
The related PRs make hundreds of lines of dependency adjustments to `apache/hive`.

For `org.apache.hive:hive-jdbc:4.0.0:standalone` corresponding to the `Uber JAR`,
this leads to issues like https://issues.apache.org/jira/browse/HIVE-28315
and https://issues.apache.org/jira/browse/HIVE-28445.
It often takes months to wait for the fixes of the related issues to be released into the new version,
without any way to fix the class conflicts.
This is almost unacceptable to downstream projects.

It is designed to be easily integrated into the nativeTest form of the GraalVM Native Image of `apache/shardingsphere`
or other projects.

More background at https://lists.apache.org/thread/63lr45kyh78s64spwsjxjy8zdyzprnz1 .
