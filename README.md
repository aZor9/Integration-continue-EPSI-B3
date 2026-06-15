# tp_epsi_integration_continue

Stack CI/CD complète : **Jenkins** + **SonarQube** + **PostgreSQL**, lancés via Docker Compose sur le même réseau.

---

## 🚀 Lancer toute la stack

```bash
docker-compose up -d
```

Cela démarre 3 conteneurs :
- **jenkins-ci** → http://localhost:8080
- **sonarqube** → http://localhost:9000
- **postgresql** (base de données de SonarQube, pas d'interface)

### Arrêter la stack

```bash
docker-compose down
```

### Supprimer les volumes (repartir de zéro)

```bash
docker-compose down -v
```

---

## 🔑 Accès aux services

### Jenkins

Récupérer le mot de passe admin initial :

```bash
docker exec jenkins-ci cat /var/jenkins_home/secrets/initialAdminPassword
```

- URL : http://localhost:8080
- User : `admin`
- Password : (celui récupéré ci-dessus)

### SonarQube

- URL : http://localhost:9000
- User : `admin`
- Password : `admin` *(à changer dès la première connexion)*

---

## 🐳 Application (bonus : conteneurisation)

Le `Dockerfile` à la racine permet de construire l'image de l'application Java.

### Construire l'image manuellement

```bash
docker build -t bad-practices-app .
```

### Lancer le conteneur

```bash
docker run -d -p 8081:8080 --name bad-practices-container bad-practices-app
```

> ⚠️ On utilise le port **8081** en local pour ne pas entrer en conflit avec Jenkins (port 8080).

### Vérifier les logs

```bash
docker logs -f bad-practices-container
```

---

## 📦 Volumes persistants

| Volume | Contenu |
| --- | --- |
| `jenkins_home` | Config Jenkins, jobs, plugins |
| `sonarqube_data` | Données SonarQube |
| `sonarqube_logs` | Logs SonarQube |
| `postgresql` | Base de données PostgreSQL |

---

## ⚙️ Guide de configuration complet (dans l'ordre)

Suivez ces étapes **dans l'ordre** lors de la première installation.

---

### Étape 1 — Installer Maven dans Jenkins

Si votre pipeline échoue avec `mvn: not found` (code de retour 127) :

**Option A (Recommandée — via l'interface Jenkins) :**
1. Allez dans **Administrer Jenkins** → **Tools**.
2. Section **Maven** → cliquez sur **Ajouter Maven**.
3. Définissez le nom exact à `Maven` et cochez **Installer automatiquement**.
4. Assurez-vous que le bloc `tools { maven 'Maven' }` est présent dans le `Jenkinsfile`.

**Option B (Manuelle — directement dans le conteneur) :**
```bash
docker exec -u root jenkins-ci apt-get update
docker exec -u root jenkins-ci apt-get install -y maven
```
> Dans ce cas, le bloc `tools` n'est plus nécessaire dans le `Jenkinsfile`.

---

### Étape 2 — Installer Docker CLI dans Jenkins

Sans cette étape, le stage `Package & Docker Build` échoue avec `docker: not found`.

Le `docker-compose.yml` doit déjà exposer le socket Docker à Jenkins (vérifiez la présence de ces lignes dans le service `jenkins`) :

```yaml
volumes:
  - jenkins_home:/var/jenkins_home
  - /var/run/docker.sock:/var/run/docker.sock
user: root
```

Puis installez le client Docker dans le conteneur Jenkins :

```bash
docker exec -u root jenkins-ci bash -c "apt-get update && apt-get install -y docker.io"
```

Si vous obtenez une erreur de permission sur le socket :

```bash
docker exec -u root jenkins-ci chmod 666 /var/run/docker.sock
```

Vérification :

```bash
docker exec jenkins-ci docker --version
```

> ⚠️ Pas besoin de redémarrer le conteneur, relancez simplement le pipeline.

---

### Étape 3 — Configurer le Job Jenkins (Pipeline from SCM)

Pour que l'instruction `checkout scm` du `Jenkinsfile` fonctionne, le job doit être lié à votre dépôt Git :

1. Poussez votre projet sur un dépôt Git distant (ex: GitHub, GitLab, etc.).
2. Dans la configuration de votre job Jenkins, descendez jusqu'à la section **Pipeline**.
3. Changez le champ "Definition" de *Pipeline script* à **Pipeline script from SCM**.
4. Sélectionnez **Git** dans la liste déroulante "SCM" et collez l'URL de votre dépôt.
5. ⚠️ **Erreur courante (Branch Specifier)** : Par défaut, Jenkins cherche la branche `*/master`. Si votre dépôt utilise `main`, changez le champ "Branches to build" en `*/main`.
6. Assurez-vous que le champ "Script Path" contient bien `Jenkinsfile`.
7. Sauvegardez et lancez le build.

---

### Étape 4 — Installer le plugin SonarQube Scanner

Sans ce plugin, le pipeline échoue avec `No such DSL method 'waitForQualityGate'`.

1. Allez dans **Administrer Jenkins** → **Plugins** → **Available plugins**.
2. Cherchez et installez **SonarQube Scanner for Jenkins**.
3. **Redémarrez Jenkins** (obligatoire).

---

### Étape 5 — Générer un token SonarQube

1. Connectez-vous sur **SonarQube** (http://localhost:9000).
2. En haut à droite → **My Account** → **Security**.
3. Générez un nouveau token :
   - **Name** : `jenkins` (ou ce que vous voulez)
   - **Type** : `User Token`
4. **Copiez la valeur immédiatement** (elle ne s'affiche qu'une seule fois).

---

### Étape 6 — Ajouter le token SonarQube dans Jenkins

1. Allez dans **Jenkins** → **Administrer Jenkins** → **Credentials**.
2. Cliquez sur **System** → **Global credentials (unrestricted)** → **Add Credentials**.
3. Remplissez le formulaire :
   - **Kind** : `Secret text`
   - **Secret** : *(collez ici le token copié depuis SonarQube)*
   - **ID** : `sonar-token` *(ce nom doit correspondre exactement à celui utilisé dans le Jenkinsfile)*
4. Cliquez sur **Create**.

> 💡 `sonar-token` est simplement l'**identifiant** du credential dans Jenkins. La **valeur** est la suite de caractères générée par SonarQube (ex: `sqa_xxxx...`).

Pour tester que le token est valide :
```bash
docker exec -it jenkins-ci curl -u <VOTRE_TOKEN>: http://sonarqube:9000/api/authentication/validate
```
Si la réponse est `{"valid":true}`, le token est bon.

---

### Étape 7 — Configurer le serveur SonarQube dans Jenkins

1. Allez dans **Jenkins** → **Administrer Jenkins** → **System**.
2. Descendez jusqu'à la section **SonarQube servers**.
3. Cliquez sur **Add SonarQube** et remplissez :
   - **Name** : `SonarQube` *(ce nom doit correspondre exactement à celui utilisé dans `withSonarQubeEnv(...)` du Jenkinsfile)*
   - **Server URL** : `http://sonarqube:9000` *(et non `localhost:9000` — les conteneurs communiquent via le réseau Docker)*
   - **Server authentication token** : sélectionnez le credential `sonar-token` créé à l'étape précédente.
4. Sauvegardez.

---

### Étape 8 — Configurer le Webhook SonarQube → Jenkins

Sans ce webhook, Jenkins attendra indéfiniment la réponse de SonarQube au stage `Quality Gate`.

1. Connectez-vous sur **SonarQube** (http://localhost:9000).
2. Allez dans **Administration** → **Configuration** → **Webhooks**.
3. Cliquez sur **Create** et configurez :
   - **Name** : `jenkins`
   - **URL** : `http://jenkins:8080/sonarqube-webhook/`
   > ⚠️ Utilisez `jenkins` (nom du conteneur Docker) et **non** `localhost`.
4. Sauvegardez.

---

## 🛠️ Guide de résolution des erreurs (Troubleshooting)

### 1. Erreur Git : `dubious ownership`

Si Jenkins affiche une erreur de permissions Git lors du Checkout :

```bash
docker exec -it jenkins-ci /bin/bash
git config --global --add safe.directory /var/jenkins_home/workspace/<nom-du-job>
git config --global --add safe.directory /var/jenkins_home/workspace/<nom-du-job>@2
exit
```

> Le `@2` correspond au workspace secondaire que Jenkins crée parfois en parallèle. Pour tout autoriser d'un coup :
> ```bash
> docker exec -it jenkins-ci git config --global --add safe.directory '*'
> ```

Puis relancez le pipeline depuis l'interface Jenkins.

---

### 2. Erreur : `mvn: not found` (code de retour 127)

→ Voir **Étape 1** du guide de configuration.

---

### 3. Erreur : `No test report files were found. Configuration error?`

**Action 1 (côté Pipeline)** — Éviter que Jenkins ne plante en l'absence de tests :
```groovy
post {
    always {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
}
```

**Action 2 (côté Java / JUnit 5)** — Si vos tests ne s'exécutent pas (`Tests run: 0`), vérifiez que votre `pom.xml` intègre JUnit 5 et une version récente de Surefire :
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.2</version>
    <scope>test</scope>
</dependency>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

---

### 4. Erreur : `No such DSL method 'waitForQualityGate'`

→ Le plugin SonarQube Scanner n'est pas installé. Voir **Étape 4** du guide de configuration.

---

### 5. Erreur : `No previous SonarQube analysis found on this pipeline execution`

Le `mvn sonar:sonar` doit obligatoirement être exécuté **à l'intérieur** du bloc `withSonarQubeEnv(...)`. Vérifiez que votre stage ressemble à ceci :

```groovy
stage('SonarQube Analysis') {
    environment {
        SONAR_TOKEN = credentials('sonar-token')
    }
    steps {
        withSonarQubeEnv('SonarQube') {
            sh "mvn sonar:sonar -Dsonar.projectKey=bad-practices-app -Dsonar.token=${SONAR_TOKEN} -Dsonar.host.url=http://sonarqube:9000"
        }
    }
}
```

---

### 6. Erreur : `SonarQube installation defined in this job does not match any configured installation`

Le nom passé à `withSonarQubeEnv('...')` ne correspond pas exactement au nom configuré dans **Jenkins → System → SonarQube servers**. Le nom est **case-sensitive**.

---

### 7. Erreur : `Failed to connect to localhost:9000` (Connection refused)

L'URL du serveur SonarQube dans **Jenkins → System → SonarQube servers** pointe vers `localhost` au lieu du nom du conteneur Docker. Corrigez en `http://sonarqube:9000`.

---

### 8. Erreur : `Not authorized. Please check the user token` (sonar.token)

Le token SonarQube est invalide ou expiré. Procédure :
1. Regénérez un token dans SonarQube → My Account → Security.
2. Mettez à jour le credential `sonar-token` dans Jenkins → Credentials → Update.
3. Vérifiez que `-Dsonar.token` est bien utilisé (et non `-Dsonar.login`, déprécié depuis SonarQube 10+).

---

### 9. Quality Gate bloqué indéfiniment en `PENDING`

→ Le webhook SonarQube → Jenkins n'est pas configuré. Voir **Étape 8** du guide de configuration.

---

### 10. Erreur : `docker: not found` (code de retour 127)

→ Le client Docker n'est pas installé dans le conteneur Jenkins. Voir **Étape 2** du guide de configuration.