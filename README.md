# Jenkins shared library for Jira and Apwide Golive

Your are at the right place if you use Jira + [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira) + Jenkins and that **you love automation**! ;-)

You should use this open source [Jenkins Shared Library](https://jenkins.io/doc/book/pipeline/shared-libraries/) to easily push/retrieve information from Jenkins, Jira and [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira).

If you prefer examples over documentation, jump directly to the [pipeline examples library](./examples) and come back here later ;-).

## Pre-requisites

* [Pipeline Utility Steps Jenkins Plugin](https://wiki.jenkins.io/display/JENKINS/Pipeline+Utility+Steps+Plugin) installed
* [Http Request Jenkins Plugin](https://wiki.jenkins.io/display/JENKINS/HTTP+Request+Plugin) installed
* Jira user account with permission to update [Apwide Golive](https://marketplace.atlassian.com/apps/1212239/golive-environment-release-for-jira) data


## Getting Started

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
* the **jiraBaseUrl** with user and password to reach our Jira instance running Apwide Golive (keep reading to learn how to get rid of these ugly hard coded values...)
* the deployed **version** of the "eCommerce Dev" environment to "0.0.1-SNAPSHOT"

## Getting a bit cleaner

You can use Jenkins credentials instead of setting user/password to connect to Jira in your pipeline.
You can also use predefined global variables to make your pipeline more readable:

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

## Getting more powerful

A single step to check the url of all your environments to automatically set their status to "Up" (valid Http response) or "Down" (Http error):

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

Just pick one of the examples. They start from the easiest one to the most advanced use cases.

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
* **APW_JIRA_BASE_URL** : Jira base url. (e.g. http://localhost:8080 or if you use a context http://localhost:2990/jira). Replace **jiraBaseUrl** parameter.
* **APW_JIRA_CREDENTIALS_ID** : Id of the Jenkins credentials use to to call Jira Rest API. Replace **jiraCredentialsId** parameter. If not provided the shared library
will look for the credentials id 'jira-credentials'
* **APW_JIRA_PROJECT** : id of key of a given jira project that will be used by steps using a Jira project (ex: creation of Jira versions)
* **APW_APPLICATION** : Environment application name used in Apwide Golive (e.g. 'eCommerce'). Replace **application** parameter.
* **APW_CATEGORY** : Environment category name used in Apwide Golive (e.g. 'Dev', 'Demo', 'Staging'...). Replace **category** parameter
* **APW_UNAVAILABLE_STATUS** : Status name when environment is considered as not available by enmvironment status check. Replace **unavailableStatus** parameter
* **APW_AVAILABLE_STATUS** : Status name when environment is considered as available by environment check status. Replace **availableStatus** parameter
* **APW_ENVIRONMENT_ID** : Id of the Apwide Golive Environment (used when updating environment details, attributes). Replace **environmentId** parameter

You can override the global variables using inline properties at step level:
```groovy
def project = apwCallJira(
    jiraBaseUrl: 'http://localhost:2990/jira',
    jiraCredentialsId: 'localhost-jira-admin',
    httpMode: 'GET',
    path: '/rest/api/2/project/10000'
)
```
With global variable
```groovy
def project = apwCallJira httpMode: 'GET', path: '/rest/api/2/project/10000'
```

To know name of parameters, please consult [Parameters](./src/com/apwide/jenkins/util/Parameters.groovy) Global Variable Reference on the pipeline where you've imported the shared lib
after [having successfully ran the job once](https://stackoverflow.com/questions/41162177/jenkins-pipeline-how-to-add-help-for-global-shared-library).

## References
* [How to setup a Jenkins shared library](https://jenkins.io/doc/book/pipeline/shared-libraries/)
* [How to configure credentials](https://jenkins.io/doc/book/using/using-credentials/)
* [Jenkins Shared Libraries](https://jenkins.io/doc/book/pipeline/shared-libraries/)
