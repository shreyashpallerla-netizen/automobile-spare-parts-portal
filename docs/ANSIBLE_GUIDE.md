# Ansible Automation, Idempotency & Rollback Guide

## 1. Playbook Architecture

```
ansible/
├── inventory.ini                  # Target hosts and host variables
├── setup-environment.yml          # Pre-requisite provisioning (Docker, dirs, firewall)
├── playbook.yml                   # Main deployment playbook
└── roles/
    └── autoparts/
        ├── tasks/
        │   ├── main.yml           # Deployment & container lifecycle tasks
        │   └── rollback.yml       # Rollback logic to previous-stable release
        ├── templates/
        │   └── nginx.conf.j2      # Nginx reverse proxy template
        └── vars/
            └── main.yml           # Application, database, and network defaults
```

---

## 2. Environment Setup (Playbook 1)

Provisions the server with required runtimes, system directories, and firewall rules:

```bash
ansible-playbook -i ansible/inventory.ini ansible/setup-environment.yml
```

Tasks executed:
- Installs `docker.io`, `docker-compose`, `curl`, and dependencies.
- Creates application directory structure: `/opt/autoparts/{config,logs,backups,scripts}`.
- Creates Docker network `autoparts-network`.
- Configures firewall rules for HTTP ports 80 and 8080.

---

## 3. Idempotent Deployment (Playbook 2)

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

### Demonstration of Idempotency:
- **First Run:** Tasks report `changed=true` (creates database container, tags images, launches application).
- **Second Run:** Tasks report `ok=true`, `changed=0`. Ansible detects that the desired container configuration, volumes, and ports already match the running state, making no disruptive alterations.

---

## 4. Reliability: Health Verification & Rollback

### Health Checks
- Following container startup, Ansible's `uri` module polls `http://localhost:8080/actuator/health`.
- Retries up to 10 times with a 10s backoff until receiving HTTP 200 and `"status":"UP"`.

### Automated Rollback Procedure
Before any deployment, the role tags the currently running container image as `autoparts/autoparts-portal:previous-stable`. If a newly deployed version fails health verification, the operator can execute an instantaneous rollback:

```bash
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --tags rollback
```

Rollback flow:
1. Stops the failing container.
2. Restarts the verified `previous-stable` image container with `DDL_AUTO=validate`.
3. Verifies health endpoint.
4. Alerts operator: *"Rollback successful — running previous-stable image"*.
