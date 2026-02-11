# Contributing

## Preparation

### For Ubuntu 24.04

Take Ubuntu WSL 24.04 as an example.
It is assumed that `Git` is configured.

```shell
sudo apt update && sudo apt upgrade --assume-yes
sudo apt-get remove docker.io docker-compose docker-compose-v2 docker-doc podman-docker containerd runc
sudo apt install ca-certificates curl --assume-yes
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

sudo tee /etc/apt/sources.list.d/docker.sources <<EOF
Types: deb
URIs: https://download.docker.com/linux/ubuntu
Suites: $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}")
Components: stable
Signed-By: /etc/apt/keyrings/docker.asc
EOF

sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin --assume-yes
sudo groupadd docker
sudo usermod -aG docker $USER
newgrp docker

sudo tee /etc/docker/daemon.json <<EOF
{
  "log-driver": "local"
}
EOF

sudo systemctl restart docker.service
sudo apt install --assume-yes unzip zip 
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 24.0.2-graalce
sudo apt-get install build-essential zlib1g-dev -y
sdk use java 24.0.2-graalce
```

### For Windows 11

Take Windows 11 as an example. **Please note that `Microsoft.VisualStudio.2022.Community` is commercial software that requires a license to use.**
It is assumed that `Git.Git`, `Microsoft.PowerShell` and `microsoft/WSL` are configured.

```powershell
winget install --source winget --exact --id Microsoft.VisualStudio.2022.Community --override "--passive --add Microsoft.VisualStudio.Workload.NativeDesktop --add Microsoft.VisualStudio.Component.VC.Tools.x86.x64 --add Microsoft.VisualStudio.Component.VC.ATL --add Microsoft.VisualStudio.Component.CppBuildInsights --add Microsoft.VisualStudio.Component.Debugger.JustInTime --add Microsoft.VisualStudio.Component.VC.DiagnosticTools --add Microsoft.VisualStudio.Component.VC.CMake.Project --add Microsoft.VisualStudio.Component.VC.TestAdapterForBoostTest --add Microsoft.VisualStudio.Component.VC.TestAdapterForGoogleTest --add Microsoft.VisualStudio.Component.IntelliCode --add Microsoft.VisualStudio.Component.VC.ASAN --add Microsoft.VisualStudio.Component.Windows11SDK.26100 --add Microsoft.VisualStudio.Component.Vcpkg --add Component.VisualStudio.GitHub.Copilot"
winget install --id SUSE.RancherDesktop --source winget --skip-dependencies
winget install --id version-fox.vfox --source winget --exact
if (-not (Test-Path -Path $PROFILE)) { New-Item -Type File -Path $PROFILE -Force }; Add-Content -Path $PROFILE -Value 'Invoke-Expression "$(vfox activate pwsh)"'
# Open a new PowerShell 7 terminal
rdctl start --container-engine.name=moby --kubernetes.enabled=false

@'
{
  "min-api-version": "1.41",
  "features": {
    "containerd-snapshotter": true
  },
  "log-driver": "local"
}
'@ | rdctl shell sudo tee /etc/docker/daemon.json

rdctl shutdown
rdctl start --container-engine.name=moby --kubernetes.enabled=false
vfox add java
vfox install java@24.0.2-graalce
vfox use --global java@24.0.2-graalce
```

## How to test

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw -T 1.5C clean test
```

## How to nativeTest under GraalVM Native Image

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw -PnativeTestInCustom clean test
```

### Special handling for Windows 11

If you execute `./mvnw -PnativeTestInCustom clean test` under Windows 11, 
a window may pop up asking you to set up `控制面板\系统和安全\Windows Defender 防火墙\允许的应用` for `native-tests.exe` 
like `D:\twinklingliftworks\git\public\hive-server2-jdbc-driver\thin\target\native-tests.exe.exe`. 
At this time, you need to set up `专用` and `公用` for this.

You need to approve `native-test.exe` more than once because this command builds multiple GraalVM Native Images.

## Fixes LICENSE issue

### For Ubuntu 24

- Execute the following command.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
docker run -it --rm -v $(pwd):/github/workspace apache/skywalking-eyes:0.8.0 header fix
```

### For Windows 11

- Execute the following command.

```
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
docker run -it --rm -v ${pwd}:/github/workspace apache/skywalking-eyes:0.8.0 header fix
```

## How to publish via the central portal

### Ubuntu

First set up GPG.
Take Ubuntu WSL 24.04 as an example and provide your real name and email address.

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
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=1.1.0
echo "test" | gpg --clearsign
./mvnw -T 1.5C -Ppublishing-via-the-central-portal -DskipTests clean deploy
./mvnw versions:set -DgenerateBackupPoms=false -DnewVersion=1.2.0-SNAPSHOT
```

Log in to https://central.sonatype.com/ and manually approve the publication.

Then, set a new git tag for the specific git commit, 
and write the new version information at https://github.com/linghengqian/hive-server2-jdbc-driver/releases with the git tag.

### windows

Download and install gpg4win from https://gpg4win.org/download.html . Then,

```shell
gpg --gen-key
gpg --list-keys
gpg --keyserver keyserver.ubuntu.com --send-keys {Aloha, your keyname}
```

According to https://central.sonatype.org/publish/publish-portal-maven/#credentials,
use the following command to change the content of `%USERPROFILE%/.m2/settings.xml`.

The contents of `%USERPROFILE%/.m2/settings.xml` might be as follows.

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
Suppose the release to be released is `1.8.0`, and the next version is `2.0.0-SNAPSHOT`.

```shell
git clone git@github.com:linghengqian/hive-server2-jdbc-driver.git
cd ./hive-server2-jdbc-driver/
./mvnw versions:set "-DgenerateBackupPoms=false" "-DnewVersion=1.8.0"
echo "test" | gpg --clearsign
./mvnw -T 1.5C -Ppublishing-via-the-central-portal -DskipTests clean deploy
./mvnw versions:set "-DgenerateBackupPoms=false" "-DnewVersion=2.0.0-SNAPSHOT"
```

Log in to https://central.sonatype.com/ and manually approve the publication.

Then, set a new git tag for the specific git commit,
and write the new version information at https://github.com/linghengqian/hive-server2-jdbc-driver/releases with the git tag.
