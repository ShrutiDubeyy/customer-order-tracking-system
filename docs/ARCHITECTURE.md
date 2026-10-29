# Architecture

## Application
Three layers inside one Spring Boot application (Java 17, executable WAR):

| Layer | Contents |
|---|---|
| Presentation | Thymeleaf pages, MVC controllers, REST controller (`/api`), Actuator (`/actuator/health`) |
| Service | `ItemService`, `OrderService`, `AlertService` (stock rules, status transitions, alert rules) |
| Persistence | Spring Data JPA repositories, entities `Item` and `CustomerOrder`, H2 database (MySQL profile available) |

## Delivery toolchain
Plan (GitHub Issues) -> Code (Git, feature branches, pull requests) -> Build (Maven, Jenkins) -> Test (JUnit, Selenium)
-> Package (Docker image, local registry) -> Deploy (Tomcat WAR, Docker container) -> Operate (Ansible node, health check, rollback).

## Jenkins pipeline (Jenkinsfile)
| Stage | Action |
|---|---|
| Checkout | Fetch the develop branch |
| Build | `mvn clean compile` |
| Package | `mvn package` (13 unit tests), archive the WAR |
| Selenium Tests | `mvn test -Pselenium` (5 browser journeys), publish the report, archive failure screenshots |
| Docker Build / Push | Image `localhost:5002/order-tracker:0.1.0-<build>` and `latest` |
| Docker Deploy | Replace the container `order-tracker-<env>-docker` on port 8083, wait for health UP |
| Deploy to Tomcat | Copy the WAR to Tomcat on port 8082 (optional parameter) |

A failing stage stops all later stages, so a defective build is never deployed.

## Ansible node
`site.yml` provisions Ubuntu (WSL2): Java 17, user `ordertracker`, folders, environment file, systemd service.
`deploy.yml` installs `releases/<version>/order-tracker.war`, switches the `current` link, restarts the service and
checks `/actuator/health`; on failure it restores the previous release. `rollback.yml` switches back manually.

## Port plan
| Port | Used by |
|---|---|
| 8080 | Jenkins |
| 8081 | Local run of the application |
| 8082 | Tomcat |
| 8083 | Docker container |
| 8090 | Ansible node (systemd service) |
| 5002 | Local Docker registry |

## Diagrams
`docs/images/use-case-diagram.png`, `docs/images/architecture-diagram.png`, `docs/images/devops-lifecycle.png`.
