import jenkins.model.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*;
{% if jenkins_deploy_aws_keys -%}
import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl
{% endif %}

domain = Domain.global()
store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

{% for key in jenkins_deploy_ssh_files %}
// SSH KEYS
privateKey = new BasicSSHUserPrivateKey(
    CredentialsScope.GLOBAL,
    "{{ key.keyname }}",
    "{{ key.username }}",
    new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource("{{jenkins_home}}/.ssh/{{key.keyname}}"),
    "{{ key.passphrase | default('') }}",
    "{{ key.description | default('') }}"
)
store.addCredentials(domain, privateKey)
{% endfor %}

{% for key in jenkins_deploy_aws_keys %}
// AWS KEYS
aws_cred = new AWSCredentialsImpl(
    CredentialsScope.GLOBAL,
    "{{ key.id }}",
    "{{ key.key }}",
    "{{ key.secret }}",
    "{{ key.desc }}",
    "",
    ""
)
store.addCredentials(domain, aws_cred)
{% endfor %}
