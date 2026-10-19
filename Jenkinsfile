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
        booleanParam(name: 'DEPLOY_TO_TOMCAT', defaultValue: true,
                     description: 'Also deploy the WAR to Tomcat (Task 8)')
        string(name: 'CONTAINER_PORT', defaultValue: '8083',
               description: 'Host port published by the Docker container')
    }

    environment {
        JAVA_HOME   = 'C:\\Program Files\\Java\\jdk-21'
        MAVEN_HOME  = 'C:\\Users\\shrut\\Downloads\\apache-maven-3.10.0-bin\\apache-maven-3.10.0'
        PATH        = "${JAVA_HOME}\\bin;${MAVEN_HOME}\\bin;C:\\Program Files\\Docker\\Docker\\resources\\bin;${PATH}"
        APP_NAME    = "order-tracker-${params.ENVIRONMENT}"
        TOMCAT_HOME = "${params.TOMCAT_HOME}"
        TOMCAT_PORT = "${params.TOMCAT_PORT}"
        REGISTRY       = 'localhost:5002'
        IMAGE_NAME     = 'order-tracker'
        IMAGE_TAG      = "0.1.0-${BUILD_NUMBER}"
        CONTAINER_NAME = "order-tracker-${params.ENVIRONMENT}-docker"
        CONTAINER_PORT = "${params.CONTAINER_PORT}"
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

        stage('Selenium Tests') {
            steps {
                bat "mvn -B test -Pselenium -Dapp.environment=${params.ENVIRONMENT}"
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/TEST-com.vit.ordertracker.selenium.*.xml'
                    archiveArtifacts artifacts: 'target/screenshots/*.png', allowEmptyArchive: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                bat "docker build -t ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG} -t ${env.REGISTRY}/${env.IMAGE_NAME}:latest ."
                bat "docker images ${env.REGISTRY}/${env.IMAGE_NAME}"
            }
        }

        stage('Docker Push') {
            steps {
                bat "docker push ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                bat "docker push ${env.REGISTRY}/${env.IMAGE_NAME}:latest"
                bat "curl -s http://${env.REGISTRY}/v2/${env.IMAGE_NAME}/tags/list"
            }
        }

        stage('Docker Deploy') {
            steps {
                // remove the previous container (ignore the error if there is none), then start a fresh one
                bat "docker rm -f ${env.CONTAINER_NAME} >nul 2>&1 || exit /b 0"
                bat "docker run -d --name ${env.CONTAINER_NAME} -p ${env.CONTAINER_PORT}:8080 -e APP_ENV=${params.ENVIRONMENT} ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                powershell '''
                    $ProgressPreference = 'SilentlyContinue'
                    [System.Net.WebRequest]::DefaultWebProxy = $null
                    $url = "http://localhost:$env:CONTAINER_PORT/actuator/health"
                    Write-Output "Waiting for $url"
                    Start-Sleep -Seconds 10
                    $ok = $false
                    for ($i = 1; $i -le 40; $i++) {
                        try {
                            $r = Invoke-WebRequest -UseBasicParsing -Uri $url -TimeoutSec 5
                            if ($r.Content -is [byte[]]) { $body = [System.Text.Encoding]::UTF8.GetString($r.Content) } else { $body = [string]$r.Content }
                            Write-Output "Attempt ${i}: HTTP $($r.StatusCode) $body"
                            if ($r.StatusCode -eq 200 -and $body -like '*"status":"UP"*') { $ok = $true; break }
                        } catch {
                            Write-Output "Attempt ${i}: $($_.Exception.Message)"
                        }
                        Start-Sleep -Seconds 4
                    }
                    if (-not $ok) { Write-Error "Container health check failed: $url"; exit 1 }
                    Write-Output "Container is healthy: http://localhost:$env:CONTAINER_PORT/"
                '''
                bat "docker ps --filter name=${env.CONTAINER_NAME}"
            }
        }

        stage('Deploy to Tomcat') {
            when {
                expression { return params.DEPLOY_TO_TOMCAT }
            }
            steps {
                bat "copy /Y target\\order-tracker.war \"${params.TOMCAT_HOME}\\webapps\\${env.APP_NAME}.war\""
                powershell '''
                    # Maven stamps files with a fixed 1980 date; set "now" so Tomcat notices the new WAR and redeploys
                    (Get-Item "$env:TOMCAT_HOME/webapps/$env:APP_NAME.war").LastWriteTime = Get-Date
                    Get-ChildItem "$env:TOMCAT_HOME/webapps" -Filter *.war | Format-Table Name, Length, LastWriteTime
                '''
                powershell '''
                    $ProgressPreference = 'SilentlyContinue'
                    [System.Net.WebRequest]::DefaultWebProxy = $null
                    $url = "http://localhost:$env:TOMCAT_PORT/$env:APP_NAME/actuator/health"
                    Write-Output "Waiting for $url"
                    # Give Tomcat time to notice the new WAR, undeploy the old version and start the new one
                    Start-Sleep -Seconds 20
                    $ok = $false
                    for ($i = 1; $i -le 40; $i++) {
                        try {
                            $r = Invoke-WebRequest -UseBasicParsing -Uri $url -TimeoutSec 5
                            # The actuator content type makes PowerShell return bytes, so convert to text first
                            if ($r.Content -is [byte[]]) { $body = [System.Text.Encoding]::UTF8.GetString($r.Content) } else { $body = [string]$r.Content }
                            Write-Output "Attempt ${i}: HTTP $($r.StatusCode) $body"
                            if ($r.StatusCode -eq 200 -and $body -like '*"status":"UP"*') { $ok = $true; break }
                        } catch {
                            Write-Output "Attempt ${i}: $($_.Exception.Message)"
                        }
                        Start-Sleep -Seconds 4
                    }
                    if (-not $ok) { Write-Error "Health check failed: $url"; exit 1 }
                    Write-Output "Deployed and healthy: http://localhost:$env:TOMCAT_PORT/$env:APP_NAME/"
                '''
            }
        }
    }

    post {
        success {
            echo "Image ${env.REGISTRY}/${env.IMAGE_NAME}:${env.IMAGE_TAG} running as ${env.CONTAINER_NAME} at http://localhost:${params.CONTAINER_PORT}/"
        }
        failure {
            echo 'Pipeline failed - check the stage logs.'
        }
    }
}
