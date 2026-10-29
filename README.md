# Customer Order Tracking System

A web application for tracking customer orders, delivered through a complete **Jenkins-based DevOps workflow**:
Git/GitHub -> Maven -> Jenkins -> Selenium quality gate -> Docker -> Ansible.

Course project: DevOps (B.E. Semester VII, Department of Computer Engineering, VIT) - Shruti Dubey, Roll No. 23102A0045.

## Features (MVP)
Item catalogue, create/update orders, order and stock status, search and filter, exception alerts (low stock, delayed orders),
REST API and health check.

## Technology
| Area | Choice |
|---|---|
| Application | Java 17, Spring Boot 3.3, Thymeleaf, Spring Data JPA, H2 (MySQL profile) |
| Build and tests | Maven, JUnit 5, Selenium 4 (Chrome) |
| CI/CD | Git, GitHub, Jenkins (Jenkinsfile), Docker, local registry |
| Servers | Tomcat 10.1 (WAR), Docker container, Ubuntu node configured with Ansible |

## Run locally
    mvn clean package
    set PORT=8081
    mvn spring-boot:run
Open http://localhost:8081 (health: `/actuator/health`). Browser tests: `mvn test -Pselenium`.

## Run in Docker
    mvn clean package -DskipTests
    docker build -t order-tracker:0.1.0 .
    docker run -d --name order-tracker -p 8083:8080 order-tracker:0.1.0

## Pipeline
The `Jenkinsfile` runs Checkout, Build, Package (unit tests), Selenium Tests (quality gate), Docker Build, Docker Push,
Docker Deploy and Deploy to Tomcat. A failing stage stops the pipeline.

## Provision a node with Ansible (Ubuntu / WSL2)
    ansible-playbook -i ansible/inventory.ini ansible/site.yml
    ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e app_version=1.0.0
    ansible-playbook -i ansible/inventory.ini ansible/rollback.yml

## Documentation
- [Architecture](docs/ARCHITECTURE.md)
- [Troubleshooting guide](docs/TROUBLESHOOTING.md)
- [Limitations and future enhancements](docs/LIMITATIONS_AND_FUTURE.md)
- [Demonstration script](docs/DEMO_SCRIPT.md)
- [Branch policy and commit rules](CONTRIBUTING.md)
- Diagrams: `docs/images/`

## Branches and releases
`main` (releases, tagged v0.1.0 and v1.0.0) <- `develop` (integration) <- `feature/*`, `bugfix/*`. Changes reach `develop` only through pull requests.
