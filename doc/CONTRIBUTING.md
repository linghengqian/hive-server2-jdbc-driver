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
./mvnw -T 1.5C clean test
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

```shell
sdk use java 8.0.422-tem
git clone git@github.com:apache/hive.git
cd ./hive/
git reset --hard b09d76e68bfba6be19733d864b3207f95265d11f
mvn clean install -DskipTests
cd ../
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
echo "test" | gpg --clearsign
./mvnw -T 1.5C -Ppublishing-via-the-central-portal -DskipTests clean deploy
```
