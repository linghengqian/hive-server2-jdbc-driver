# tinycircus

This project is inspired by `apache/bigtop` and provides third-party Linux Containers for the distribution of big data
components.

Since developers lack a local development environment for Windows Containers and WASM modules,
the Windows Containers WASM module is not the focus of this project.

## `ghcr.io/linghengqian/hive`

The current project creates some Linux Containers based on `apache/hive` which contain more dependencies.
Currently, container includes:

- PostgreSQL JDBC Driver 42.7.7 `postgresql-42.7.7.jar` in `/opt/hive/lib`

### Usage

An interesting use of this Linux Container is to simplify the process of creating `sys Schema and information_schema Schema` 
in https://hive.apache.org/development/quickstart/ .

Assuming `Docker Engine` are installed, a possible use case is as follows. Create a `compose.yaml` file in the current directory,

```yaml
services:
  some-postgres:
    image: postgres:17.2-bookworm
    environment:
      POSTGRES_PASSWORD: "example"
  hiveserver2-standalone:
    image: ghcr.io/linghengqian/hive:4.0.1-all-in-one
    depends_on:
      - some-postgres
    environment:
      SERVICE_NAME: hiveserver2
      DB_DRIVER: postgres
      SERVICE_OPTS: >-
        -Djavax.jdo.option.ConnectionDriverName=org.postgresql.Driver
        -Djavax.jdo.option.ConnectionURL=jdbc:postgresql://some-postgres:5432/postgres
        -Djavax.jdo.option.ConnectionUserName=postgres
        -Djavax.jdo.option.ConnectionPassword=example
```

Then execute the shell command as follows to initialize the system schemas in HiveServer2.

```shell
docker compose up -d
docker compose exec hiveserver2-standalone /bin/bash
/opt/hive/bin/schematool -initSchema -dbType hive -metaDbType postgres -url jdbc:hive2://localhost:10000/default
exit
```
