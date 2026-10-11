pipeline {
    agent any

    options {
        disableConcurrentBuilds()
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['dev', 'test', 'staging'],
               description: 'Target environment: used in the WAR name, URL and page header')
        string(name: 'TOMCAT_HOME', defaultValue: 'C:\\tomcat10',
               description: 'Tomcat installation folder')
        string(name: 'TOMCAT_PORT', defaultValue: '8082',
               description: 'Tomcat HTTP port (used for the health check)')
    }

    environment {
        JAVA_HOME  = 'C:\\Program Files\\Java\\jdk-21'
        MAVEN_HOME = 'C:\\Users\\shrut\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0'
        PATH       = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;${PATH}"
        APP_NAME   = "order-tracker-${params.ENVIRONMENT}"
	 TOMCAT_PORT = "${params.TOMCAT_PORT}"

    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat "mvn -B clean compile -Dapp.environment=${params.ENVIRONMENT}"
            }
        }

        stage('Package') {
            steps {
                bat "mvn -B package -Dapp.environment=${params.ENVIRONMENT}"
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
                success {
                    archiveArtifacts artifacts: 'target/*.war', fingerprint: true
                }
            }
        }

        stage('Deploy') {
            steps {
                bat "copy /Y target\\order-tracker.war \"${params.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war\""
                bat "dir \"${params.TOMCAT_HOME}\\webapps\\*.war\""
                powershell '''
                    $url = "http://localhost:$env:TOMCAT_PORT/$env:APP_NAME/actuator/health"
                    $ok = $false
                    for ($i = 1; $i -le 20; $i++) {
                        try {
                            $r = Invoke-WebRequest -UseBasicParsing -Uri $url -TimeoutSec 5
                            if ($r.StatusCode -eq 200 -and $r.Content -match 'UP') { $ok = $true; break }
                        } catch { }
                        Start-Sleep -Seconds 3
                    }
                    if (-not $ok) { Write-Error "Health check failed: $url"; exit 1 }
                    Write-Output "Deployed and healthy: http://localhost:$env:TOMCAT_PORT/$env:APP_NAME/"
                '''
            }
        }
    }

    post {
        success {
            echo "Deployed to ${params.ENVIRONMENT}: http://localhost:${params.TOMCAT_PORT}/${env.APP_NAME}/"
        }
        failure {
            echo 'Pipeline failed - check the stage logs.'
        }
    }
}
