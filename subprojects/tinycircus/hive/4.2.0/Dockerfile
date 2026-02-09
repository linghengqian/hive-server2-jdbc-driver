# Copyright 2026 Qiheng He
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM alpine:3.22.1 AS prepare
RUN apk add --no-cache wget
RUN wget https://repo1.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/12.10.1.jre8/mssql-jdbc-12.10.1.jre8.jar --directory-prefix=/opt/hive/lib
RUN wget https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.3.0/mysql-connector-j-9.3.0.jar --directory-prefix=/opt/hive/lib
RUN wget https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc8/23.8.0.25.04/ojdbc8-23.8.0.25.04.jar --directory-prefix=/opt/hive/lib
RUN wget https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar --directory-prefix=/opt/hive/lib

FROM apache/hive:4.2.0
LABEL org.opencontainers.image.authors="Qiheng He linghengqian@outlook.com"
LABEL org.opencontainers.image.source="https://github.com/linghengqian/hive-server2-jdbc-driver"
LABEL org.opencontainers.image.description="Linux Container of HiveServer2 and Hive Metastore Server built by the third party"
COPY --from=prepare /opt/hive/lib /opt/hive/lib
