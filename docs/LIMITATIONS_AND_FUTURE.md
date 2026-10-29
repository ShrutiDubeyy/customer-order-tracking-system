# Limitations and future enhancements

## Limitations
- Everything runs on one laptop (Jenkins, Tomcat, Docker and the WSL node share its CPU and memory).
- The Docker registry is local and unencrypted (no TLS, no login).
- The database is in-memory H2: data is lost when the application restarts.
- The MVP has no user login or roles.
- Jenkins polls GitHub every two minutes instead of receiving webhooks (the laptop is not reachable from the internet).
- Selenium tests cover five journeys only and run on one browser (Chrome).
- The Ansible node is a WSL2 Ubuntu instance on the same laptop, not a separate server.
- Secrets (such as registry or database passwords) are not managed with a vault.

## Future enhancement plan
| Enhancement | Plan |
|---|---|
| Persistent database | Use MySQL (profile already prepared) with a volume or a managed database |
| Authentication and roles | Spring Security with staff and customer roles |
| Webhooks | GitHub webhook to Jenkins through a tunnel or a hosted Jenkins |
| Cloud deployment | Run the container on a cloud VM or a managed container service |
| Kubernetes | Deployments, rolling updates and automatic rollback |
| Registry | Docker Hub or a private registry with TLS and image scanning |
| Monitoring | Prometheus metrics and Grafana dashboards from the Actuator |
| Security in the pipeline | Dependency and image vulnerability scans, secrets in Jenkins credentials or Ansible Vault |
| More tests | API tests, more Selenium journeys, cross-browser runs in parallel |
