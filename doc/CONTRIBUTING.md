# Contributing

Take Ubuntu WSL 22.04.4 as an example.
It is assumed that `Git` is configured, and `SDKMAN!` and `Docker Engine` are installed.

```shell
sdk install java 8.0.422-tem
sdk install java 22.0.2-graalce
sudo apt-get install build-essential zlib1g-dev -y
```

### How to test

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
sdk use java 22.0.2-graalce
./mvnw -T 1.5C clean test
```

### How to nativeTest under GraalVM Native Image

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
sdk use java 22.0.2-graalce
./mvnw -PnativeTestInCustom clean test
```

### Fixes LICENSE issue

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
docker run -it --rm -v $(pwd):/github/workspace apache/skywalking-eyes:0.6.0 header fix
```


### How to publish via the central portal

First set up GPG.
Take Ubuntu WSL 22.04.4 as an example and provide your real name and email address.

```shell
gpg --gen-key
gpg --list-keys
gpg --keyserver keyserver.ubuntu.com --send-keys {Aloha, your keyname}
```

According to https://central.sonatype.org/publish/publish-portal-maven/#credentials,
use the following command to change the content of `~/.m2/settings.xml`.

```shell
sudo apt install gnome-text-editor gimp vlc nautilus x11-apps -y
gnome-text-editor ~/.m2/settings.xml
```

The contents of `~/.m2/settings.xml` might be as follows.

```xml

<settings>
    <servers>
        <server>
            <id>central</id>
            <username><!-- Aloha, your token username --></username>
            <password><!-- Aloha, your token password --></password>
        </server>
    </servers>
</settings>
```

Then execute the following command.
Suppose the release to be released is `1.1.0`, and the next version is `1.2.0-SNAPSHOT`.

```shell
sdk use java 8.0.422-tem
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=1.1.0
echo "test" | gpg --clearsign
./mvnw -T 1.5C -Ppublishing-via-the-central-portal -DskipTests clean deploy
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=1.2.0-SNAPSHOT
```

Log in to https://central.sonatype.com/ and manually approve the publication.
