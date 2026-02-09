# Contributing

The password for `ghcr.io` comes from https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#labelling-container-images
and https://github.com/settings/tokens .

Follow the instructions at https://docs.github.com/zh/packages/learn-github-packages/connecting-a-repository-to-a-package
and link the container to the specified repository at https://github.com/linghengqian?tab=packages .

For instructions on handling `--provenance=false`, 
please refer to https://docs.docker.com/reference/cli/docker/buildx/build/#provenance and https://github.com/docker/build-push-action/issues/771 .

## For `4.0.1` on `apache/hive`

If you need to create and push a Linux Container, you can do something like this.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/

docker run --privileged --rm tonistiigi/binfmt:latest --install all
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker buildx build "--provenance=false" --push --platform linux/amd64,linux/arm64 -t ghcr.io/linghengqian/hive:4.0.1-all-in-one ./subprojects/tinycircus/hive/4.0.1
```

## For `4.1.0` on `apache/hive`

If you need to create and push a Linux Container, you can do something like this.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/

docker run --privileged --rm tonistiigi/binfmt:latest --install all
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker buildx build "--provenance=false" --push --platform linux/amd64,linux/arm64 -t ghcr.io/linghengqian/hive:4.1.0-all-in-one ./subprojects/tinycircus/hive/4.1.0
```

## For `4.2.0` on `apache/hive`

If you need to create and push a Linux Container, you can do something like this.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/

docker run --privileged --rm tonistiigi/binfmt:latest --install all
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker buildx build "--provenance=false" --push --platform linux/amd64,linux/arm64 -t ghcr.io/linghengqian/hive:4.2.0-all-in-one ./subprojects/tinycircus/hive/4.2.0
```

## For `4.3.0-SNAPSHOT` on `apache/hive`

**Warning: You can only build `apache/hive` binary from source code on Linux with `glibc`.**
1. Building `apache/hive` binary on `Windows 11 Home 24H2` will not work.
2. Building `apache/hive` binary on `Alpine Linux 3.22.1` will not work, **too**. This is related to https://github.com/protocolbuffers/protobuf-ci/issues/10 .

Multi-architecture image generation is not supported yet. 
If the device you are using is `amd64`, the generated image will be `amd64`,

This is why the current project is to build `apache/hive` binary in `Ubuntu 24.04` Linux Container.

```shell
docker run -it --rm -v /var/run/docker.sock:/var/run/docker.sock -v ${home}/.m2:/root/.m2 maven:3.9.11-eclipse-temurin-21-noble /bin/bash
apt update
apt-get remove docker.io docker-doc docker-compose docker-compose-v2 podman-docker containerd runc
apt-get install ca-certificates curl
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
chmod a+r /etc/apt/keyrings/docker.asc
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  tee /etc/apt/sources.list.d/docker.list > /dev/null
apt-get update
apt-get install docker-ce-cli -y
git clone --depth 1 --branch master https://github.com/apache/hive.git
cd ./hive/
mvn clean install -DskipTests -T 1.5C
mvn clean package -pl packaging -DskipTests -Pdocker -T 1.5C
exit

git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
docker run --privileged --rm tonistiigi/binfmt:latest --install all
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker buildx build "--provenance=false" --push -t ghcr.io/linghengqian/hive-snapshot:4.2.0-all-in-one ./subprojects/tinycircus/hive/4.3.0-SNAPSHOT
```
