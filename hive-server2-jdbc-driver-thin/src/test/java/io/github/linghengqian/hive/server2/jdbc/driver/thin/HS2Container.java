package io.github.linghengqian.hive.server2.jdbc.driver.thin;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.Transferable;

import java.io.IOException;
import java.net.ServerSocket;

@SuppressWarnings({"resource", "OctalInteger"})
public class HS2Container extends GenericContainer<HS2Container> {
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

    @Override
    protected void containerIsStarting(InspectContainerResponse containerInfo) {
        Integer mappedPort = getMappedPort(randomPortFirst);
        String command = """
                #!/bin/bash
                export SERVICE_OPTS='-Dhive.server2.support.dynamic.service.discovery=true -Dhive.zookeeper.quorum=foo:2181 -Dhive.server2.thrift.bind.host=0.0.0.0 -Dhive.server2.thrift.port=%s'
                /usr/local/bin/docker-entrypoint.sh
                """.formatted(mappedPort);
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
