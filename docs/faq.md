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

## Why do I get a warning about `META-INF/services/javax.xml.stream.XMLOutputFactory`?

If you use `io.github.linghengqian:hive-server2-jdbc-driver-uber` under GraalVM Native Image, you may see this Warning.

```shell
WARNING: Type implements CloseableResource but not AutoCloseable: org.testcontainers.junit.jupiter.TestcontainersExtension$StoreAdapter
com.oracle.svm.core.jdk.resources.MissingResourceRegistrationError: Cannot access resource at path 'META-INF/services/javax.xml.stream.XMLOutputFactory'. To allow this operation, add the following to the 'resources' section of 'reachability-metadata.json' and rebuild the native image:

  {
    "glob": "META-INF/services/javax.xml.stream.XMLOutputFactory"
  }

The 'reachability-metadata.json' file should be located in 'META-INF/native-image/<group-id>/<artifact-id>/' of your project. For further help, see https://www.graalvm.org/latest/reference-manual/native-image/metadata/#resources
  java.base@25/java.util.ServiceLoader$2.hasNext(ServiceLoader.java:1246)
  java.xml@25/javax.xml.stream.FactoryFinder.findServiceProvider(FactoryFinder.java:275)
  java.xml@25/javax.xml.stream.FactoryFinder.find(FactoryFinder.java:239)
  java.xml@25/javax.xml.stream.FactoryFinder.find(FactoryFinder.java:192)
  java.xml@25/javax.xml.stream.XMLOutputFactory.newInstance(XMLOutputFactory.java:143)
  org.junit.platform.reporting.legacy.xml.XmlReportWriter$XmlReport.<init>(XmlReportWriter.java:130)
  org.junit.platform.reporting.legacy.xml.XmlReportWriter.writeXmlReport(XmlReportWriter.java:118)
  org.junit.platform.reporting.legacy.xml.XmlReportWriter.writeXmlReport(XmlReportWriter.java:101)
```

This is intentional, 
because `io.github.linghengqian:hive-server2-jdbc-driver-uber` does not have a `com.ctc.wstx.stax.WstxOutputFactory` class, 
but only a `org.apache.hive.com.ctc.wstx.stax.WstxOutputFactory` class.

If you really want this warning log to disappear, 
you can introduce a dependency on `com.fasterxml.woodstox:woodstox-core:5.4.0` in a build tool such as Maven or Gradle.
`com.fasterxml.woodstox:woodstox-core:5.4.0` is also a dependency of `org.apache.hadoop:hadoop-common:3.3.6`.

Then create the following JSON entry in any `resource-config.json` on the classpath.

```json
{
  "resources":{
  "includes":[{
    "condition":{"typeReachable":"org.junit.platform.reporting.legacy.xml.XmlReportWriter"},
    "pattern":"\\QMETA-INF/services/javax.xml.stream.XMLOutputFactory\\E"
  }]},
  "bundles":[]
}
```

Then create the following JSON entry in any `reflect-config.json` on the classpath.

```json
[
{
"condition":{"typeReachable":"org.junit.platform.reporting.legacy.xml.XmlReportWriter"},
"name":"com.ctc.wstx.stax.WstxOutputFactory"
}
]
```

If there are no related dependency `com.fasterxml.woodstox:woodstox-core:5.4.0`, it will result in,

```shell
Aug 05, 2025 3:09:36 PM org.junit.platform.launcher.core.CompositeTestExecutionListener lambda$notifyEach$21
WARNING: TestExecutionListener [org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener] threw exception for method: executionFinished(TestIdentifier [uniqueId = [engine:junit-jupiter], parentId = null, displayName = 'JUnit Jupiter', legacyReportingName = 'JUnit Jupiter', source = null, tags = [], type = CONTAINER], TestExecutionResult [status = SUCCESSFUL, throwable = null])
javax.xml.stream.FactoryConfigurationError: Provider for class javax.xml.stream.XMLOutputFactory cannot be created
	at java.xml@25/javax.xml.stream.FactoryFinder.findServiceProvider(FactoryFinder.java:291)
	at java.xml@25/javax.xml.stream.FactoryFinder.find(FactoryFinder.java:239)
	at java.xml@25/javax.xml.stream.FactoryFinder.find(FactoryFinder.java:192)
	at java.xml@25/javax.xml.stream.XMLOutputFactory.newInstance(XMLOutputFactory.java:143)
	at org.junit.platform.reporting.legacy.xml.XmlReportWriter$XmlReport.<init>(XmlReportWriter.java:130)
	at org.junit.platform.reporting.legacy.xml.XmlReportWriter.writeXmlReport(XmlReportWriter.java:118)
	at org.junit.platform.reporting.legacy.xml.XmlReportWriter.writeXmlReport(XmlReportWriter.java:101)
	at org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener.writeXmlReportSafely(LegacyXmlReportGeneratingListener.java:117)
	at org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener.writeXmlReportInCaseOfRoot(LegacyXmlReportGeneratingListener.java:110)
	at org.junit.platform.reporting.legacy.xml.LegacyXmlReportGeneratingListener.executionFinished(LegacyXmlReportGeneratingListener.java:104)
	at org.junit.platform.launcher.core.CompositeTestExecutionListener.lambda$executionFinished$10(CompositeTestExecutionListener.java:74)
	at org.junit.platform.launcher.core.CompositeTestExecutionListener.lambda$notifyEach$21(CompositeTestExecutionListener.java:110)
	at org.junit.platform.commons.util.CollectionUtils.forEachInReverseOrder(CollectionUtils.java:263)
	at org.junit.platform.launcher.core.IterationOrder$2.forEach(IterationOrder.java:30)
	at org.junit.platform.launcher.core.CompositeTestExecutionListener.notifyEach(CompositeTestExecutionListener.java:108)
	at org.junit.platform.launcher.core.CompositeTestExecutionListener.executionFinished(CompositeTestExecutionListener.java:73)
	at org.junit.platform.launcher.core.ExecutionListenerAdapter.executionFinished(ExecutionListenerAdapter.java:57)
	at org.junit.platform.launcher.core.CompositeEngineExecutionListener.lambda$executionFinished$6(CompositeEngineExecutionListener.java:60)
	at org.junit.platform.launcher.core.CompositeEngineExecutionListener.lambda$notifyEach$13(CompositeEngineExecutionListener.java:82)
	at org.junit.platform.commons.util.CollectionUtils.forEachInReverseOrder(CollectionUtils.java:263)
	at org.junit.platform.launcher.core.IterationOrder$2.forEach(IterationOrder.java:30)
	at org.junit.platform.launcher.core.CompositeEngineExecutionListener.notifyEach(CompositeEngineExecutionListener.java:80)
	at org.junit.platform.launcher.core.CompositeEngineExecutionListener.executionFinished(CompositeEngineExecutionListener.java:59)
	at org.junit.platform.launcher.core.DelegatingEngineExecutionListener.executionFinished(DelegatingEngineExecutionListener.java:47)
	at org.junit.platform.launcher.core.StackTracePruningEngineExecutionListener.executionFinished(StackTracePruningEngineExecutionListener.java:46)
	at org.junit.platform.launcher.core.DelegatingEngineExecutionListener.executionFinished(DelegatingEngineExecutionListener.java:47)
	at org.junit.platform.launcher.core.OutcomeDelayingEngineExecutionListener.reportEngineOutcome(OutcomeDelayingEngineExecutionListener.java:69)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.executeEngine(EngineExecutionOrchestrator.java:233)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.failOrExecuteEngine(EngineExecutionOrchestrator.java:204)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:172)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:101)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.lambda$execute$0(EngineExecutionOrchestrator.java:64)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.withInterceptedStreams(EngineExecutionOrchestrator.java:150)
	at org.junit.platform.launcher.core.EngineExecutionOrchestrator.execute(EngineExecutionOrchestrator.java:63)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:109)
	at org.junit.platform.launcher.core.DefaultLauncher.execute(DefaultLauncher.java:100)
	at org.junit.platform.launcher.core.DelegatingLauncher.execute(DelegatingLauncher.java:52)
	at org.junit.platform.launcher.core.InterceptingLauncher.lambda$execute$2(InterceptingLauncher.java:47)
	at org.junit.platform.launcher.core.ClasspathAlignmentCheckingLauncherInterceptor.intercept(ClasspathAlignmentCheckingLauncherInterceptor.java:25)
	at org.junit.platform.launcher.core.InterceptingLauncher.execute(InterceptingLauncher.java:46)
	at org.junit.platform.launcher.core.DelegatingLauncher.execute(DelegatingLauncher.java:52)
	at org.junit.platform.launcher.core.SessionPerRequestLauncher.execute(SessionPerRequestLauncher.java:73)
	at org.graalvm.junit.platform.NativeImageJUnitLauncher.main(NativeImageJUnitLauncher.java:132)
	at java.base@25/java.lang.invoke.LambdaForm$DMH/sa346b79c.invokeStaticInit(LambdaForm$DMH)
Caused by: java.lang.RuntimeException: Provider for class javax.xml.stream.XMLOutputFactory cannot be created
	at java.xml@25/javax.xml.stream.FactoryFinder.findServiceProvider(FactoryFinder.java:288)
	... 43 more
Caused by: java.util.ServiceConfigurationError: javax.xml.stream.XMLOutputFactory: Provider com.ctc.wstx.stax.WstxOutputFactory not found
	at java.base@25/java.util.ServiceLoader.fail(ServiceLoader.java:559)
	at java.base@25/java.util.ServiceLoader$LazyClassPathLookupIterator.nextProviderClass(ServiceLoader.java:1090)
	at java.base@25/java.util.ServiceLoader$LazyClassPathLookupIterator.hasNextService(ServiceLoader.java:1099)
	at java.base@25/java.util.ServiceLoader$LazyClassPathLookupIterator.hasNext(ServiceLoader.java:1142)
	at java.base@25/java.util.ServiceLoader$1.hasNext(ServiceLoader.java:1164)
	at java.base@25/java.util.ServiceLoader$2.hasNext(ServiceLoader.java:1246)
	at java.xml@25/javax.xml.stream.FactoryFinder.findServiceProvider(FactoryFinder.java:275)
	... 43 more
```

## Why don't the containers distributed by the `tinycircus` subproject include `provenance attestations`?

The concept of `provenance attestations` can be obtained from https://docs.docker.com/build/metadata/attestations/slsa-provenance/ 
and https://github.com/docker/buildx/issues/1964#issuecomment-1644634461 .

However, due to the unresolved issue https://github.com/orgs/community/discussions/45969 , 
the default configuration of `provenance attestations` in the docker buildx plugin will cause the GHCR WebUI to display a manifest of `unknown/unknown`. 
This is not a user-friendly behavior.

Therefore, maintainers always use `--provenance=false` to disable the creation of `provenance attestations` when building Linux Containers of `tinycircus`.
