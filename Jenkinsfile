// ============================================================
// Jenkinsfile - AutoParts Portal CI/CD Pipeline
// ============================================================

pipeline {
    agent any

    environment {
        APP_NAME         = 'autoparts-portal'
        APP_VERSION      = '1.0.0'
        DOCKER_IMAGE     = "autoparts/${APP_NAME}"
        DOCKER_TAG       = "${APP_VERSION}-${BUILD_NUMBER}"
        REGISTRY         = credentials('docker-registry-credentials')
        JAVA_HOME        = tool 'JDK-17'
        MAVEN_HOME       = tool 'Maven-3.9'
        PATH             = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${env.PATH}"
        HEALTH_URL       = 'http://localhost:8080/actuator/health'
        DEPLOY_HOST      = '127.0.0.1'
        POSTGRES_CREDS   = credentials('postgres-db-credentials')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    triggers {
        // Poll SCM every 5 minutes for changes
        pollSCM('H/5 * * * *')
    }

    stages {
        // -----------------------------------------------
        stage('📋 Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --format="%H %s"'
                sh 'echo "Branch: ${GIT_BRANCH}"'
            }
        }

        // -----------------------------------------------
        stage('🔧 Build') {
            steps {
                sh 'mvn clean compile -B'
            }
            post {
                success { echo '✅ Build succeeded' }
                failure { echo '❌ Build failed' }
            }
        }

        // -----------------------------------------------
        stage('🧪 Unit Tests') {
            steps {
                sh 'mvn test -B -Dsurefire.reportFormat=xml'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/surefire-report',
                        reportFiles: 'index.html',
                        reportName: 'Unit Test Report'
                    ])
                }
            }
        }

        // -----------------------------------------------
        stage('🔍 Code Quality') {
            steps {
                sh 'mvn checkstyle:check -B || echo "Checkstyle warnings found"'
            }
        }

        // -----------------------------------------------
        stage('📦 Package') {
            steps {
                sh 'mvn package -DskipTests -B'
                archiveArtifacts artifacts: 'target/*.jar',
                                  fingerprint: true,
                                  onlyIfSuccessful: true
            }
        }

        // -----------------------------------------------
        stage('🐳 Docker Build') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} -t ${DOCKER_IMAGE}:latest ."
                    sh "docker images ${DOCKER_IMAGE}"
                }
            }
        }

        // -----------------------------------------------
        stage('🧪 Integration Tests') {
            steps {
                script {
                    // Start postgres for integration testing
                    sh """
                        docker run -d --name ci-postgres-${BUILD_NUMBER} \
                            -e POSTGRES_DB=autoparts \
                            -e POSTGRES_USER=postgres \
                            -e POSTGRES_PASSWORD=postgres \
                            -p 5433:5432 postgres:16-alpine
                        sleep 10
                    """
                    sh """
                        docker run --rm --name ci-app-${BUILD_NUMBER} \
                            --link ci-postgres-${BUILD_NUMBER}:postgres \
                            -e DB_URL=jdbc:postgresql://postgres:5432/autoparts \
                            -e DB_USERNAME=postgres \
                            -e DB_PASSWORD=postgres \
                            -p 8081:8080 \
                            -d ${DOCKER_IMAGE}:${DOCKER_TAG}
                        sleep 30
                        curl -f http://localhost:8081/actuator/health || (docker logs ci-app-${BUILD_NUMBER}; exit 1)
                        docker stop ci-app-${BUILD_NUMBER} || true
                    """
                }
            }
            post {
                always {
                    sh "docker stop ci-postgres-${BUILD_NUMBER} || true"
                    sh "docker rm ci-postgres-${BUILD_NUMBER} || true"
                    sh "docker rm ci-app-${BUILD_NUMBER} || true"
                }
            }
        }

        // -----------------------------------------------
        stage('📤 Docker Push') {
            when {
                branch 'main'
            }
            steps {
                script {
                    docker.withRegistry("https://${REGISTRY_USR}", 'docker-registry-credentials') {
                        def image = docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}")
                        image.push()
                        image.push('latest')
                    }
                    echo "✅ Image pushed: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                }
            }
        }

        // -----------------------------------------------
        stage('🚀 Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    sh """
                        docker stop autoparts-app || true
                        docker rm autoparts-app || true
                        docker-compose -f docker-compose.yml up -d autoparts-app
                    """
                }
            }
        }

        // -----------------------------------------------
        stage('❤️ Health Check') {
            when {
                branch 'main'
            }
            steps {
                script {
                    retry(5) {
                        sleep 15
                        sh "curl -sf ${HEALTH_URL} | grep -q '\"status\":\"UP\"'"
                        echo "✅ Application is healthy"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed. Status: ${currentBuild.result}"
            cleanWs()
        }
        success {
            echo "✅ Pipeline SUCCESS — Build #${BUILD_NUMBER} deployed"
        }
        failure {
            echo "❌ Pipeline FAILED — Build #${BUILD_NUMBER}"
            // mail to: 'devops@autoparts.local',
            //      subject: "Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //      body: "Check ${env.BUILD_URL}"
        }
    }
}
