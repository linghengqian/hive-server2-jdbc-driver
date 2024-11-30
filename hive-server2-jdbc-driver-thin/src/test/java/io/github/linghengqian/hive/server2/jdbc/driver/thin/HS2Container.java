/*
 * Copyright 2024 Qiheng He
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.linghengqian.hive.server2.jdbc.driver.thin;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

import java.io.IOException;
import java.net.ServerSocket;

@SuppressWarnings({"resource"})
public class HS2Container extends GenericContainer<HS2Container> {

    String zookeeperConnectionString;
    private static final String STARTER_SCRIPT = "/testcontainers_start.sh";
    private final int randomPortFirst = getRandomPort();

    public HS2Container(final String dockerImageName) {
        super(dockerImageName);
        withEnv("SERVICE_NAME", "hiveserver2");
        withExposedPorts(randomPortFirst);
        withCreateContainerCmdModifier(cmd ->
                cmd.withEntrypoint("sh", "-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT)
        );
    }

    public HS2Container withZookeeperConnectionString(final String zookeeperConnectionString) {
        this.zookeeperConnectionString = zookeeperConnectionString;
        return self();
    }

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
        String command = """
                #!/bin/bash
                export SERVICE_OPTS="-Dhive.server2.support.dynamic.service.discovery=true -Dhive.zookeeper.quorum=%s -Dhive.server2.thrift.bind.host=0.0.0.0 -Dhive.server2.thrift.port=%s"
                /entrypoint.sh
                """.formatted(zookeeperConnectionString, getMappedPort(randomPortFirst));
        copyFileToContainer(Transferable.of(command, 0777), STARTER_SCRIPT);
    }

    private int getRandomPort() {
        try (ServerSocket server = new ServerSocket(0)) {
            server.setReuseAddress(true);
            return server.getLocalPort();
        } catch (IOException exception) {
            throw new Error(exception);
        }
    }
}
