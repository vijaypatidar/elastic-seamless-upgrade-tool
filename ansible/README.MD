
# Ansible 

This Ansible playbook automates the rolling upgrade of Elasticsearch clusters and Kibana.

## 📋 Prerequisites  
- **Ansible installed**
- **SSH access** to the target servers  
- A running Elasticsearch and Kibana cluster 

## 📜 Inventory Setup  
Create an inventory file (`inventory.ini`) by taking `inventory.example` as a reference.

## 🚀 Running the Playbook
To upgrade Elasticsearch and Kibana, run:
```bash
  ansible-playbook -i inventory -e elk_version=8.17.0 main.yml 
```

## 🔗 Links
[![Hyperflex](https://cdn.prod.website-files.com/66c869ab4cb5836cc9292b68/66d1dfaa6e96d9058013d283_image%20(7)%201.png)](https://www.hyperflex.co/) 

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/company/hyperflexco/)

## Authors
- Written by: Nitesh Parmar