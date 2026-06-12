pipeline {
    agent any

    // Outils à définir dans la configuration Jenkins (Manage Jenkins -> Global Tool Configuration)
    tools {
        maven 'Maven' // Correspond au nom configuré pour Maven dans Jenkins
        jdk 'JDK 17'  // Correspond au nom configuré pour le JDK 17 dans Jenkins
    }

    environment {
        // Variables d'environnement
        SONAR_HOST_URL = 'http://sonarqube:9000'
    }

    stages {
        stage('Checkout') {
            steps {
                // Récupération du code source depuis Git
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compilation du projet...'
                if (isUnix()) {
                    sh 'mvn clean compile'
                } else {
                    bat 'mvn clean compile'
                }
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo 'Exécution des tests et génération du rapport JaCoCo...'
                if (isUnix()) {
                    sh 'mvn test'
                } else {
                    bat 'mvn test'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            environment {
                // Nécessite de configurer un secret text "sonar-token" dans Jenkins
                SONAR_TOKEN = credentials('sonar-token')
            }
            steps {
                echo 'Analyse de la qualité du code avec SonarQube...'
                if (isUnix()) {
                    sh "mvn sonar:sonar -Dsonar.projectKey=bad-practices-app -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
                } else {
                    bat "mvn sonar:sonar -Dsonar.projectKey=bad-practices-app -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_TOKEN}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // Attente du résultat du Quality Gate de SonarQube
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package & Docker Build') {
            steps {
                echo 'Création du JAR exécutable et de l\'image Docker...'
                
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
