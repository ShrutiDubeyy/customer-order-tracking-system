# Troubleshooting guide

Problems met while building this project, with the cause and the fix.

| Symptom | Cause | Fix |
|---|---|---|
| `mvn` is not recognized | The Maven `bin` folder is not in the Path variable | Add the Maven `bin` folder to the user Path and open a new terminal |
| Application will not start: port 8080 already in use | Jenkins already uses 8080 | Set `PORT=8081` before `mvn spring-boot:run` (see the port plan) |
| Tomcat starts but the browser cannot connect | The commented example Connector in server.xml was edited instead of the real one | Change the real `<Connector port="8080"` to 8082 (and the Server port to 8006) |
| Pipeline health check URL had an empty port | On the first run of a new pipeline job the build parameters are not yet environment variables | Define `TOMCAT_PORT = "${params.TOMCAT_PORT}"` in the Jenkinsfile environment block |
| A new WAR was not redeployed by Tomcat | Spring Boot builds stamp files with a fixed 1980 date; Tomcat redeploys only newer files | Set the WAR's LastWriteTime to now after copying it |
| Health check failed although the app answered HTTP 200 and status UP | The actuator content type makes PowerShell return the body as bytes | Convert the bytes to text before matching the status |
| Selenium: Unable to obtain msedgedriver | Selenium 4.25 downloads the Edge driver from an address Microsoft retired | Use Chrome as the default browser (or upgrade Selenium) |
| Selenium tests time out waiting for a form field under Jenkins | The test opened the next page before the redirect after Save finished (race) | Wait for the result of every form submit; raise the default wait to 20 s |
| A unit test failed after a review fix (SKU not trimmed) | An edit replaced the SKU trim line instead of adding the name trim line | Restore `setSku(...trim())` above `setName(...trim())` |
| docker: error during connect ... dockerDesktopLinuxEngine | Docker Desktop's engine is not running | Start Docker Desktop and wait for Engine running |
| docker run: name or port already in use (registry, 5001) | Containers from earlier labs use them | Use the name order-registry and port 5002 |
| Jenkins cannot use Docker | The service account cannot reach Docker Desktop | Check with a docker-check job; if it fails, expose the daemon on tcp://localhost:2375 and set DOCKER_HOST, or run the service as your user |
| Ansible: Timed out waiting for become success | The new Ubuntu uses sudo-rs, whose password prompt Ansible does not recognise | Allow passwordless sudo for the lab user in /etc/sudoers.d and run without --ask-become-pass |
| Ansible: System has not been booted with systemd | systemd is disabled in WSL | Add `[boot]` and `systemd=true` to /etc/wsl.conf, then `wsl --shutdown` |
| Pull request merged into main instead of develop | A reopened pull request kept its old base branch | Synchronise develop with a pull request main to develop; read the base line before every merge |
| A commit landed on local develop instead of the feature branch | `git checkout -b` failed because the branch already existed | `git checkout -B <branch>`, push it, then `git branch -f develop origin/develop` |
| Every merge starts two Jenkins builds | The old freestyle job and the pipeline job both poll develop | Expected; disable the freestyle job if the machine is slow |
