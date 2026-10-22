# Node configuration with Ansible

Prepares an Ubuntu node (WSL2) to run the Customer Order Tracking System.

| Item | Value |
|------|-------|
| Packages | openjdk-17-jre-headless, curl, ca-certificates |
| User / group | ordertracker / ordertracker (system account, no login) |
| Folders | /opt/order-tracker, /opt/order-tracker/releases, /var/log/order-tracker, /etc/order-tracker |
| Files | /etc/order-tracker/order-tracker.env, /etc/systemd/system/order-tracker.service |
| Port | 8090/tcp (set through PORT in the environment file) |
| Service | order-tracker (systemd), enabled at boot |

## Run
    sudo apt install -y ansible
    ansible-playbook -i ansible/inventory.ini ansible/site.yml --check --diff --ask-become-pass   # dry run
    ansible-playbook -i ansible/inventory.ini ansible/site.yml --ask-become-pass                   # apply
    ansible-playbook -i ansible/inventory.ini ansible/site.yml --ask-become-pass                   # again: changed=0
