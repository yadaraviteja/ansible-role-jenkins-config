jenkins_deploy
==============

Role focuses on post installation, configuration of jenkins.

Requirements
------------

* ansible > 2.1
* molecule


Local testing:
```bash
$ ansible-galaxy install -r requirements.yml
$ molecule test
```

Role Variables
--------------

```yaml
---
jenkins_home: /var/lib/jenkins
jenkins_deploy_css_url: https://jenkins-contrib-themes.github.io/jenkins-material-theme/dist/material-blue.css
jenkins_deploy_js_url: ""

jenkins_deploy_config_master_exe: 0
jenkins_deploy_config_disableusage_stats: true

#
# Utilize GitHub Oauth for authorization and permissions
###############################################################
jenkins_deploy_gh_oauth: {}
#  url: https://github.com
#  api_url: https://api.github.com
#  client_id: 12314124412
#  secret_id: 23424324324
#  scope: "read:org"

jenkins_deploy_gh_oauth_strategy: {}
#  admins:
#    - msheiny
#  organizations:
#    - organization
#  github_webhook: true
#  authread_all: true

#
# Utilize GitHub Org to populate pipeline jobs
###############################################################
jenkins_deploy_gh_folder: []
#  - job_name: "GitHub Scanner"
#    owner: mygithuborg
#    scan_cred_id: gh_token_id
#    health_recursive: false
#    scan_pattern: repo_name (OR leave off for all in org)
#    origin_branch: true
#    origin_branch_pr: true
#    origin_pr_merge: false
#    origin_pr_head: false
#    fork_pr_merge: false
#    fork_pr_head: false
#    prune_dead_branch: true
#    prune_days: 0
#    prune_number: 0

#
# Jenkins credentials
###############################################################
jenkins_deploy_ssh_files: []
# - keyname: mykey
#   username: root
#   privkey: Asdjadsoijadjasdlkajdljadjs
#   passphrase: yay
#   description: 'This is my key for service x'

jenkins_deploy_aws_keys: []
# - id: jenkinsawskey
#   key: ASdoKJDASDOAJD
#   secret: ASDJADKJAKDLSAJDLKAJDLSAJDLAS
#   desc: "Jenkins AWS user for EC2 building"

jenkins_deploy_userpass_creds: []
# - id: usernamepass
#   username: myuser
#   password: password
#   description: "My user credentials"

jenkins_deploy_secret_creds: []
#  - id: slack-token
#    desc: "Slack credential token"
#    secret: "124908124908124/12489012840912/124890124021408"

jenkins_slack_settings: {}
#  domain: mydomain
#  cred_id: slack-token
#  room: "#fav-room"

jenkins_deploy_plugin_files: []

#
# Jenkins EC2 cloud builders
###############################################################
jenkins_deploy_ec2_clouds: []
#  - name: MyEC2Cloud
#    credential_id: awscredid
#    instance_cap: 5
#    region: us-east-1
#    private_key: "{{ ssh_private_key }}"
#    workers:
#      - ami_id: asdadaisdo
#        description: UbuntuWorker
#        security_groups: SSHSG
#        remote_fs_root: /home/jenkins/workspace
#        ebs_optimized: false
#        instance_type: t2.small
#        labels: aws-builders
#        mode: normal # NORMAL = Utilize as much as possible, EXCLUSIVE = only for tied jobs
#        executors: 1
#        remoteadmin: jenkins
#        connectsshprocess: true

#
# nginx reverse proxy settings used in testing
###############################################################
jenkins_nginx_usercontent: |
  location /userContent {
    root {{ jenkins_home }}/;
        if (!-f $request_filename){
           rewrite (.*) /$1 last;
       break;
        }
    sendfile on;
  }

jenkins_master_pkgs: []

jenkins_nginx_root: |
  location / {
    sendfile off;
    proxy_pass         http://127.0.0.1:8080;
    proxy_redirect     default;

    proxy_set_header   Host              $host;
    proxy_set_header   X-Real-IP         $remote_addr;
    proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
    proxy_set_header   X-Forwarded-Proto https;
    proxy_max_temp_file_size 0;
    proxy_http_version 1.0;

    #this is the maximum upload size
    client_max_body_size       10m;
    client_body_buffer_size    128k;

    proxy_connect_timeout      90;
    proxy_send_timeout         90;
    proxy_read_timeout         90;

    proxy_buffer_size          4k;
    proxy_buffers              4 32k;
    proxy_busy_buffers_size    64k;
    proxy_temp_file_write_size 64k;
  }

jenkins_static_configs:
  # Disable legacy insecure CLI over remoting option
  - src: disable.remote.cli.xml
    dest: "{{ jenkins_home }}/jenkins.CLI.xml"
  # Enable slave to master security system
  - content: "false"
    dest: "{{ jenkins_home }}/secrets/slave-to-master-security-kill-switch"
```

Dependencies
------------

* `geerlingguy.jenkins` - Used for installing jenkins, java, and plugins
* `jdauphant.nginx` - Used for installing and configuring nginx reverse proxy

Example Playbook
----------------

Check out `./playbook.yml`

License
-------

MIT
