# Jenkins shared library for Jira and Apwide Golive

Your are at the right place if you use Jira + [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira) + Jenkins and if **you love automation**!

You should use this open source [Jenkins Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) to easily exchange information between Jenkins, Jira and [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira).

If you prefer examples over documentation, jump directly to the [pipeline examples library](./examples) and come back here later.

## Pre-requisites

* [Pipeline Utility Steps Jenkins Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Utility+Steps+Plugin) installed
* [Http Request Jenkins Plugin](https://wiki.jenkins.io/display/JENKINS/HTTP+Request+Plugin) installed
* Running Jira server with [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira) installed
* Basic understanding of Apwide Golive's [key concepts](https://www.apwide.com/documentation)


## Get Started

1. [Import the Jenkins Shared Library](https://stackoverflow.com/questions/41162177/jenkins-pipeline-how-to-add-help-for-global-shared-library)
1. Create your first Hello World pipeline:

###### Push eCommerce Dev environment's deployed version to Apwide Golive
```groovy
steps {
    apwSetDeployedVersion(
        jiraBaseUrl: 'http://admin:admin@mycompany.com/jira',
        application: 'eCommerce',
        category: 'Dev',
        version: '0.0.1-SNAPSHOT'
    )
}
```
In this example script, we have set:
* **jiraBaseUrl**, user and password to connect to Jira with Apwide Golive (keep reading to learn how to get rid of these ugly hard coded values...)
* deployed **version** of the "eCommerce Dev" environment to "0.0.1-SNAPSHOT"

## A bit cleaner

You can use Jenkins credentials instead of hard coding user/password in your pipeline.
Usage of predefined global variables also makes your pipeline more readable:

```groovy
environment {
    APW_JIRA_BASE_URL = 'http://mycompany.com/jira'
    APW_JIRA_CREDENTIALS_ID = 'jira-credentials'
    APW_APPLICATION = 'eCommerce'
    APW_CATEGORY = 'Dev'
}
steps {
    apwSetDeployedVersion version: '0.0.1-SNAPSHOT'
    apwSetEnvironmentStatus status: 'Up'
}
```

Much more concise, isn't it ?

Using Jenkins variable is very powerful. Learn more how use them at different levels:
* [Jenkins Global Environment Variables](https://wiki.jenkins.io/display/JENKINS/Global+Variable+String+Parameter+Plugin)
* [In pipeline environment directive at pipeline or stage level](https://jenkins.io/doc/book/pipeline/syntax/#environment)
* [Using Pipeline Basic Step withEnv on local portion](https://jenkins.io/doc/pipeline/steps/workflow-basic-steps/#withenv-set-environment-variables)

## More powerful

You just need a single step to check the url of all your Apwide environments:

```groovy
environment {
    APW_JIRA_BASE_URL = 'http://mycompany.com/jira'
    APW_JIRA_CREDENTIALS_ID = 'jira-credentials'
    APW_UNAVAILABLE_STATUS = 'Down'
    APW_AVAILABLE_STATUS = 'Up'
}
steps {
    apwCheckEnvironmentsStatus
}
```

This single step will automatically call the url of each environment and set its status to "Up" (valid Http response) or "Down" (Http error).
Quite powerful, isn't it ? ;-)


## Direct calls to Jira and Apwide Golive Rest API

You can also make direct calls to any endpoints of Jira and Apwide Golive REST API using this more generic step:

```groovy
steps {
    apwCallJira httpMode: 'GET', path: '/rest/api/2/project/10000'
    apwCallJira httpMode: 'POST', path: '/rest/api/2/versions', body:[:]
}
```

To add more predefined steps: fork the project and add your own script sugars! We will be happy to merge your pull requests! ;-)

## More Examples

Browse our "examples" folder to get inspired and to reuse portion of scripts to write your own pipelines. They start from the easiest  to the most advanced ones. A quick overview:

### Environment Monitoring
* [Single environment](./examples/monitoring/single-environment): monitor one single environment
* [Custom check logic](./examples/monitoring/custom-check) : implement a custom logic to check the status of an environment
* [Single application](./examples/monitoring/single-application):  monitor all environments of an application
* [Multiple applications](./examples/monitoring/multi-application): monitor environments of several applications
* [Advanced selection of environments](./examples/monitoring/criteria-selection): monitor a custom set of environments using search criteria
 
### Deployment tracking
* [Deployment workflow](./examples/deployment/simple-build-deploy): push build and deployment information to Jira and Apwide Golive

### Self-Service Environments
* [Environment self-service](./examples/self-service/): users can trigger the creation of new environments and deployments from Jira

## Predefined Global Variables
To avoid duplication in your pipelines, Jenkins global variables can be set and overriden at different levels.

Here are the available predefined global variables:

### Jira global variables
* **APW_JIRA_BASE_URL** : Jira base url. (e.g. http://localhost:8080 or if you use a context http://localhost:2990/jira). Replace **jiraBaseUrl** parameter.
* **APW_JIRA_CREDENTIALS_ID** : Id of the Jenkins credentials use to to call Jira Rest API. Replace **jiraCredentialsId** parameter. If not provided the shared library
will look for the credentials id 'jira-credentials'
* **APW_JIRA_PROJECT** : id of key of a given jira project that will be used by steps using a Jira project (ex: creation of Jira versions)

Note that you can also override the global variables using inline properties at step level like in this example:
```groovy
environment {
    APW_JIRA_BASE_URL = 'http://mycompany.com/jira'
    APW_JIRA_CREDENTIALS_ID = 'jira-credentials'
}
def project = apwCallJira(
    jiraBaseUrl: 'http://localhost:2990/jira',
    jiraCredentialsId: 'localhost-jira-admin',
    httpMode: 'GET',
    path: '/rest/api/2/project/10000'
)
```
This allows you to easily deal with multiple Jira and Apwide Golive servers if required.

### Apwide Golive global variables
* **APW_APPLICATION** : Environment application name used in Apwide Golive (e.g. 'eCommerce'). Replace **application** parameter.
* **APW_CATEGORY** : Environment category name used in Apwide Golive (e.g. 'Dev', 'Demo', 'Staging'...). Replace **category** parameter
* **APW_UNAVAILABLE_STATUS** : Status name when environment is considered as not available by enmvironment status check. Replace **unavailableStatus** parameter
* **APW_AVAILABLE_STATUS** : Status name when environment is considered as available by environment check status. Replace **availableStatus** parameter
* **APW_ENVIRONMENT_ID** : Id of the Apwide Golive Environment (used when updating environment details, attributes). Replace **environmentId** parameter

### Browse documentation in Jenkins UI
You can browse the list of step parameters and global variables of the shared lib in [Parameters](./src/com/apwide/jenkins/util/Parameters.groovy) Global Variable Reference. 
This documentation will be visible from the pipeline editor only **after [having successfully ran the job once](https://stackoverflow.com/questions/41162177/jenkins-pipeline-how-to-add-help-for-global-shared-library)**.

## Provided Pipeline Steps

### Call any REST endpoint
* [apwCallJira](/vars/apwCallJira.txt)

### Manage Environment
* [apwCreateEnvironment](/vars/apwCreateEnvironment.txt)
* [apwCreateEnvironmentAndCategory](/vars/apwCreateEnvironmentAndCategory.txt)
* [apwUpdateEnvironment](/vars/apwUpdateEnvironment.txt)
* [apwSetDeployedVersion](/vars/apwSetDeployedVersion.txt)
* [apwSetEnvironmentStatus](/vars/apwSetEnvironmentStatus.txt)

### Search and List Environments
* [apwGetEnvironment](/vars/apwGetEnvironment.txt)
* [apwGetEnvironments](/vars/apwGetEnvironments.txt)
* [apwSearchEnvironments](/vars/apwSearchEnvironments.txt)
* [apwWithEnvironments](/vars/apwWithEnvironments.txt)

### Manage Application
* [apwCreateApplication](/vars/apwCreateApplication.txt)
* [apwUpdateApplication](/vars/apwUpdateApplication.txt)
* [apwDeleteApplication](/vars/apwDeleteApplication.txt)

### Manage Category
* [apwCreateEnvironmentCategory](/vars/apwCreateEnvironmentCategory.txt)
* [apwUpdateEnvironmentCategory](/vars/apwUpdateEnvironmentCategory.txt)
* [apwDeleteEnvironmentCategory](/vars/apwDeleteEnvironmentCategory.txt)

### Monitor Environments
* [apwCheckEnvironmentStatus](/vars/apwCheckEnvironmentStatus.txt)
* [apwCheckEnvironmentsStatus](/vars/apwCheckEnvironmentsStatus.txt)

### Jira Project
* [apwJiraGetProject](/vars/apwJiraGetProject.txt)

### Jira Version
* [apwJiraGetProjectVersions](/vars/apwJiraGetProjectVersions.txt)
* [apwJiraCreateVersion](/vars/apwJiraCreateVersion.txt)
* [apwJiraUpdateVersion](/vars/apwJiraUpdateVersion.txt)


## References
* [How to setup a Jenkins shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/)
* [How to configure credentials](https://jenkins.io/doc/book/using/using-credentials/)
* [Jenkins Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)
