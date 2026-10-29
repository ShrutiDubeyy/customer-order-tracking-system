# Live demonstration script (about 10 minutes)

## Before the demonstration
- Docker Desktop shows "Engine running"; container `order-registry` is up (`docker ps`).
- Jenkins is open at http://localhost:8080 (job `order-tracker-pipeline`); Tomcat is running on 8082.
- An Ubuntu window is open in the repository folder (`/mnt/c/Users/shrut/Documents/customer-order-tracking-system`).
- Browser tabs: GitHub repository, Jenkins, http://localhost:8083/, http://localhost:8090/.

## Script
| Time | Action | What to say |
|---|---|---|
| 0:00 | Show the GitHub repository: branches, issues, pull requests, tags | Branch policy, issues from the backlog, every change goes through a pull request |
| 1:00 | Edit the heading in `src/main/resources/templates/index.html`, commit on a feature branch, push, open and merge the pull request into develop | The commit that starts the pipeline |
| 2:30 | In Jenkins click Build with Parameters (or wait for polling) and show the Stage View | Checkout, Build, Package with 13 unit tests, then the Selenium gate with 5 browser journeys |
| 5:00 | While it runs: show a failed run (the deliberate defect), its archived screenshot and the green rerun | A failed test stops deployment; unit tests alone did not catch it |
| 7:00 | Open http://localhost:5002/v2/order-tracker/tags/list and `docker ps`, then http://localhost:8083/ | Versioned image in the registry, fresh container, new heading visible |
| 8:30 | In Ubuntu run `ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e app_version=1.0.0` | Idempotent: changed=0; health check UP at http://localhost:8090/ |
| 9:30 | Show the bad-release rollback evidence (or run it with `-e app_version=2.0.0-bad -e war_src=/tmp/broken.war -e health_retries=6`) | Health check fails, the previous release is restored automatically |
| 10:00 | Questions | |
