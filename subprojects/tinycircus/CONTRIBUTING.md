# Contributing

If you need to create and push a Linux Container, you can do something like this.

The password for `ghcr.io` comes from https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry#labelling-container-images 
and https://github.com/settings/tokens .

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/

cd ./subprojects/tinycircus/hive/4.0.1
docker buildx build -t ghcr.io/linghengqian/hive:4.0.1-all-in-one .
docker login ghcr.io/linghengqian/hive-server2-jdbc-driver --username linghengqian
docker push ghcr.io/linghengqian/hive:4.0.1-all-in-one
```

Follow the instructions at https://docs.github.com/zh/packages/learn-github-packages/connecting-a-repository-to-a-package 
and link the container to the specified repository at https://github.com/linghengqian?tab=packages .
