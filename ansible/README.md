# Node configuration, deployment and rollback with Ansible

Target node: Ubuntu (WSL2) with systemd. All commands are run from the repository root in Ubuntu.

| Playbook | Purpose |
|----------|---------|
| site.yml | Provisions the node: Java 17, service user, folders, config files, systemd unit (Task 13) |
| deploy.yml | Deploys a release, checks /actuator/health, rolls back automatically if the check fails |
| rollback.yml | Manual rollback to the previous release |
| teardown.yml | Returns the node to a clean state |

## Release layout
    /opt/order-tracker/releases/<version>/order-tracker.war
    /opt/order-tracker/current  -> releases/<live version>
    /opt/order-tracker/previous -> releases/<previous version>

## Commands
    ansible-playbook -i ansible/inventory.ini ansible/teardown.yml
    ansible-playbook -i ansible/inventory.ini ansible/site.yml
    ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e app_version=1.0.0
    ansible-playbook -i ansible/inventory.ini ansible/site.yml                              # again: changed=0
    ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e app_version=1.0.0       # again: changed=0
    ansible-playbook -i ansible/inventory.ini ansible/deploy.yml -e app_version=2.0.0-bad -e war_src=/tmp/broken.war -e health_retries=6
    ansible-playbook -i ansible/inventory.ini ansible/rollback.yml

Build the WAR first on Windows: `mvn clean package -DskipTests` (creates target/order-tracker.war).
