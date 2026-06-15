pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet...'
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo 'Exécution des tests et génération du rapport JaCoCo...'
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                SONAR_TOKEN = credentials('sonar-token')
            }
            steps {
                echo 'Analyse de la qualité du code avec SonarQube...'
                withSonarQubeEnv('SonarQube') {
                    script {
                        if (isUnix()) {
                            sh "mvn sonar:sonar -Dsonar.projectKey=bad-practices-app -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=http://sonarqube:9000"
                        } else {
                            bat "mvn sonar:sonar -Dsonar.projectKey=bad-practices-app -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=http://sonarqube:9000"
                        }
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package & Docker Build') {
            steps {
                echo 'Création du JAR exécutable et de l\'image Docker...'
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                        sh 'docker build -t epsi/bad-practices-app:latest .'
                    } else {
                        bat 'mvn package -DskipTests'
                        bat 'docker build -t epsi/bad-practices-app:latest .'
                    }
                }
            }
        }
    }
}