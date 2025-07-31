# Contributing

The password for `ghcr.io` comes from https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#labelling-container-images
and https://github.com/settings/tokens .

Follow the instructions at https://docs.github.com/zh/packages/learn-github-packages/connecting-a-repository-to-a-package
and link the container to the specified repository at https://github.com/linghengqian?tab=packages .

## For `apache/hive:4.0.1`

If you need to create and push a Linux Container, you can do something like this.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/

cd ./subprojects/tinycircus/hive/4.0.1
docker buildx build -t ghcr.io/linghengqian/hive:4.0.1-all-in-one .
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker push ghcr.io/linghengqian/hive:4.0.1-all-in-one
```

## For `apache/hive:4.1.0-SNAPSHOT`

**Warning: You can only build `apache/hive` binary from source code on Linux with `glibc`.**
1. Building `apache/hive` binary on `Windows 11 Home 24H2` will not work.
2. Building `apache/hive` binary on `Alpine Linux 3.22.1` will not work, **too**. This is related to https://github.com/protocolbuffers/protobuf-ci/issues/10 .

This is why the current project is to build `apache/hive` binary in `Ubuntu 24.04` Linux Container.

If you need to create and push a Linux Container, you can do something like this.
You need to have an account on https://hub.docker.com/ to execute `docker login`.

```shell
docker login
docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v ${home}/.m2:/root/.m2 maven:3.9.11-eclipse-temurin-17-noble /bin/bash
apt update
apt-get remove docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc
apt-get install ca-certificates curl
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update
apt-get install docker-ce-cli -y
git clone --depth 1 --branch branch-4.1 https://github.com/apache/hive.git
cd ./hive/
mvn clean install -DskipTests -T 1.5C
mvn clean package -pl packaging -DskipTests -Pdocker -T 1.5C
exit

git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
cd ./subprojects/tinycircus/hive/4.1.0-SNAPSHOT
docker buildx build -t ghcr.io/linghengqian/hive:4.1.0-SNAPSHOT-all-in-one .
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker push ghcr.io/linghengqian/hive:4.1.0-SNAPSHOT-all-in-one
```
