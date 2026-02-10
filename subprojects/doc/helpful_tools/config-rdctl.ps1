#Requires -Version 7.4

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

# This file is only used in the PowerShell 7 in GitHub Actions environment and should not be executed manually in a development environment.
iex "& { $(irm https://raw.githubusercontent.com/microsoft/Windows-Containers/refs/heads/Main/helpful_tools/Install-DockerCE/uninstall-docker-ce.ps1) } -Force"
irm https://raw.githubusercontent.com/jazzdelightsme/WingetPathUpdater/v1.2/WingetPathUpdaterInstall.ps1 | iex
winget install --id SUSE.RancherDesktop --source winget --skip-dependencies
rdctl start --application.start-in-background --container-engine.name=moby --kubernetes.enabled=false
./subprojects/doc/helpful_tools/wait-for-rancher-desktop-backend.ps1

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
rdctl start --application.start-in-background --container-engine.name=moby --kubernetes.enabled=false
./subprojects/doc/helpful_tools/wait-for-rancher-desktop-backend.ps1
