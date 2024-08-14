# Third-party builds of the HiveServer2 JDBC Driver

The purpose of this project is to release HiveServer2 JDBC Driver and HiveServer2 Docker Image faster outside of ASF.

Use directly in Maven.

```xml
<dependency>
    <groupId>io.github.linghengqian</groupId>
    <artifactId>hive-server2-jdbc-driver</artifactId>
    <version>{latest.version}</version>
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

## contribute

Take Ubuntu WSL 22.04.4 as an example.
It is assumed that `Git` is configured, and `SDKMAN!` and `Docker Engine` are installed.

```shell
sdk install java 8.0.422-tem
sdk install maven 3.9.8
```

### How to test

- Execute the following command.

```shell
sdk use java 8.0.422-tem
git clone git@github.com:apache/hive.git
cd ./hive/
git reset --hard b09d76e68bfba6be19733d864b3207f95265d11f
mvn clean install -DskipTests
mvn clean package -pl packaging -DskipTests -Pdocker
cd ../
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw clean test
```

### How to publish via the central portal

First set up GPG. Take Ubuntu WSL 22.04.4 as an example.
Take Ubuntu WSL 22.04.4 as an example and provide your real name and email address.

```shell
gpg --gen-key
gpg --list-keys
gpg --keyserver keyserver.ubuntu.com --send-keys {Aloha, your keyid}
```

According to https://central.sonatype.org/publish/publish-portal-maven/#credentials, 
use the following command to change the content of `~/.m2/setting.xml`.

```shell
sudo apt install gnome-text-editor gimp vlc nautilus x11-apps -y
gnome-text-editor ~/.m2/setting.xml
```

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username><!-- your token username --></username>
      <password><!-- your token password --></password>
    </server>
  </servers>
</settings>
```

Then execute the following command.

```shell
sdk use java 8.0.422-tem
git clone git@github.com:apache/hive.git
cd ./hive/
git reset --hard b09d76e68bfba6be19733d864b3207f95265d11f
mvn clean install -DskipTests
cd ../
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw -Ppublishing-via-the-central-portal -DskipTests clean deploy
```
