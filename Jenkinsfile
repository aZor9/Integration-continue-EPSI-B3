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
                // TODO : Ajouter la commande Maven pour nettoyer et compiler le projet (sans lancer les tests)
                // sh '...'
            }
        }

        stage('Test & Code Coverage') {
            steps {
                echo 'Exécution des tests et génération du rapport JaCoCo...'
                // TODO : Ajouter la commande Maven pour lancer les tests
                // sh '...'
            }
            post {
                always {
                    // TODO : Indiquer à Jenkins où récupérer les rapports de tests au format XML (indice : plugin junit)
                    // junit '...'
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
                // TODO : Ajouter la commande Maven pour lancer l'analyse SonarQube
                // N'oubliez pas de passer les propriétés : sonar.projectKey, sonar.host.url et sonar.login
                // sh '...'
            }
        }

        stage('Quality Gate') {
            steps {
                // TODO : Ajouter l'étape pour attendre le résultat du Quality Gate de SonarQube
                // Indice : Il faut utiliser un 'timeout' englobant 'waitForQualityGate abortPipeline: true'
                
            }
        }

        stage('Package & Docker Build') {
            steps {
                echo 'Création du JAR exécutable et de l\'image Docker...'
                
                // TODO : Ajouter la commande Maven pour créer le package complet (JAR) en ignorant les tests
                // sh '...'
                
                // TODO : Ajouter la commande Docker pour construire l'image avec le tag "epsi/bad-practices-app:latest"
                // sh '...'
            }
        }
    }
}
