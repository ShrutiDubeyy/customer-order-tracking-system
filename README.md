# Customer Order Tracking System

A small web application for tracking customer orders, built and delivered through a **Jenkins-based DevOps pipeline**
(Git/GitHub -> Maven -> Jenkins -> Selenium -> Docker -> Puppet/Ansible).

Course project: DevOps (B.E. Semester VII, Department of Computer Engineering, VIT).

## MVP features
- Item catalogue (add, edit, search items and stock)
- Create / update orders
- Order and stock status tracking
- Search and filter orders
- Exception alerts (low stock, delayed orders)

## Technology stack
| Area | Choice |
|------|--------|
| Language / framework | Java 17+, Spring Boot 3.3, Thymeleaf |
| Build | Maven 3.9+ |
| Database | H2 (default), MySQL (optional profile) |
| Server | Tomcat 10.1 (WAR) |
| CI/CD | Git, GitHub, Jenkins, Docker, Puppet/Ansible, Selenium |

## Getting started
Prerequisites: JDK 17+, Maven 3.9+, Git.

    git clone <repository-url>
    cd customer-order-tracking-system
    mvn clean package
    mvn spring-boot:run

The application starts on http://localhost:8080. If that port is busy, set another one first:

    set PORT=8081          (Windows)
    export PORT=8081       (Linux / macOS)

Health check: `/actuator/health`

## Project structure
    src/main/java/com/vit/ordertracker/   application code (web, service, repository, model)
    src/main/resources/                   templates, static files, configuration
    src/test/java/                        unit and integration tests
    docs/                                 requirements and architecture diagrams
    .github/                              issue templates and pull request template
    CONTRIBUTING.md                       branch policy and commit rules

## Branching
`main` (stable releases) <- `develop` (integration) <- `feature/*`, `bugfix/*`. See [CONTRIBUTING.md](CONTRIBUTING.md).

## Documentation
- [Use-case diagram](docs/images/use-case-diagram.png)
- [Architecture diagram](docs/images/architecture-diagram.png)
- [DevOps lifecycle](docs/images/devops-lifecycle.png)

## Author
Shruti Dubey (Roll No. 23102A0045)
