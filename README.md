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

## ⚠️ Problème Git : dubious ownership

Si Jenkins affiche une erreur `dubious ownership` lors du Checkout, c'est un problème de permissions Git dans le conteneur.

**Solution :**

```bash
docker exec -it jenkins-ci /bin/bash
git config --global --add safe.directory /var/jenkins_home/workspace/<nom-du-job>
exit
```

Puis relancer le pipeline depuis l'interface Jenkins.

---

## 🛠️ Installer Maven et JDK dans Jenkins

Si votre pipeline échoue car Maven ou JDK ne sont pas configurés (et que vous ne souhaitez pas passer par l'interface de Jenkins "Global Tool Configuration"), vous pouvez les installer directement dans le conteneur avec ces commandes :

```bash
docker exec -u root jenkins-ci apt-get update
docker exec -u root jenkins-ci apt-get install -y maven
```

*(Note : Si vous utilisez cette méthode, le bloc `tools` n'est plus nécessaire dans le `Jenkinsfile` car les outils seront accessibles globalement dans le système).*

---

## 📦 Volumes persistants

| Volume | Contenu |
| --- | --- |
| `jenkins_home` | Config Jenkins, jobs, plugins |
| `sonarqube_data` | Données SonarQube |
| `sonarqube_logs` | Logs SonarQube |
| `postgresql` | Base de données PostgreSQL |

---

## ⚙️ Configuration du Job Jenkins (Pipeline from SCM)

Pour que l'instruction `checkout scm` du `Jenkinsfile` fonctionne, le job doit être lié à votre dépôt Git. Voici la procédure à suivre dans l'interface Jenkins :

1. Poussez votre projet sur un dépôt Git distant (ex: GitHub, GitLab, etc.).
2. Dans la configuration de votre job Jenkins, descendez jusqu'à la section **Pipeline**.
3. Changez le champ "Definition" de *Pipeline script* à **Pipeline script from SCM**.
4. Sélectionnez **Git** dans la liste déroulante "SCM" et collez l'URL de votre dépôt.
5. ⚠️ **Erreur courante (Branch Specifier)** : Par défaut, Jenkins cherche la branche `*/master`. Si votre dépôt Git utilise la branche `main` (ce qui est le cas par défaut sur GitHub), vous obtiendrez l'erreur `couldn't find remote ref refs/heads/master`. **Changez le champ "Branches to build" en `*/main**`.
6. Assurez-vous que le champ "Script Path" contient bien `Jenkinsfile`.
7. Sauvegardez et lancez le build.

---

## 🔑 Configuration du Token SonarQube dans Jenkins

Lors de l'exécution, si le pipeline s'arrête net avec une erreur `ERROR: sonar-token`, c'est que Jenkins n'a pas l'autorisation de se connecter à SonarQube. Voici la procédure à suivre :

1. Allez sur **SonarQube** (http://localhost:9000), connectez-vous, puis allez dans **My Account** (en haut à droite) > **Security**.
2. Générez un nouveau token de type "User Token" et **copiez-le**.
3. Allez sur **Jenkins** > **Administrer Jenkins** (Manage Jenkins) > **Credentials**.
4. Cliquez sur **System** (sous la liste "Stores scoped to Jenkins"), puis sur **Global credentials (unrestricted)**, et enfin sur **Add Credentials**.
5. Remplissez le formulaire ainsi :
* **Kind** : `Secret text`
* **Secret** : *(collez ici le token copié depuis SonarQube)*
* **ID** : `sonar-token` *(attention, ce nom doit correspondre exactement à celui demandé dans le Jenkinsfile)*


6. Cliquez sur **Create**. Le pipeline pourra désormais s'authentifier et envoyer le code à analyser !

---

## 🛠️ Guide de Résolution des Erreurs (Troubleshooting)

### 1. Erreur Git : `dubious ownership`

Si Jenkins affiche une erreur de permissions Git lors de l'étape Checkout :

```bash
docker exec -it jenkins-ci /bin/bash
git config --global --add safe.directory /var/jenkins_home/workspace/<nom-du-job>
exit

```

### 2. Erreur : `mvn: not found` (Code de retour 127)

Se produit si l'exécutable Maven n'est pas accessible par l'agent Jenkins. Deux solutions sont possibles :

* **Option A (Recommandée - Automatique via Jenkins) :**
1. Allez dans **Administrer Jenkins** → **Tools** (Configuration globale des outils).
2. Allez à la section **Maven** et cliquez sur **Ajouter Maven**.
3. Définissez le nom exact à `Maven` et cochez la case **Installer automatiquement**.
4. Décommentez le bloc `tools { maven 'Maven' }` en haut de votre `Jenkinsfile`.


* **Option B (Manuelle - Au niveau système du conteneur) :**
Exécutez ces commandes pour forcer l'installation globale de Maven directement à l'intérieur du conteneur :
```bash
docker exec -u root jenkins-ci apt-get update
docker exec -u root jenkins-ci apt-get install -y maven

```



### 3. Erreur : `No test report files were found. Configuration error?`

Cette erreur interrompt brutalement le pipeline si aucun test n'a été détecté ou généré par Maven.

* **Action 1 (Côté Pipeline) :** Pour éviter que Jenkins ne plante en l'absence temporaire de tests, modifiez le bloc `post` du stage de Test dans votre `Jenkinsfile` :
```groovy
post {
    always {
        junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
    }
}

```


* **Action 2 (Côté Java / JUnit 5) :** Si vos tests ne s'exécutent pas (`Tests run: 0`), vérifiez que votre `pom.xml` intègre l'agrégateur moderne JUnit 5 et une version récente du plugin Surefire :
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



### 4. Erreur : `No such DSL method 'waitForQualityGate'` (ou Pipeline bloqué indéfiniment)

L'étape de Quality Gate nécessite un pont de communication bidirectionnel entre Jenkins et SonarQube.

1. **Installer le Plugin :** Allez dans **Administrer Jenkins** → **Plugins** → **Available plugins**, cherchez et installez le plugin **SonarQube Scanner for Jenkins**, puis redémarrez Jenkins.
2. **Configurer le Webhook (Crucial) :** Sans cela, Jenkins attendra indéfiniment la réponse de SonarQube :
* Connectez-vous sur **SonarQube** (http://localhost:9000).
* Allez dans **Administration** → **Configuration** → **Webhooks**.
* Cliquez sur **Create** et configurez l'URL réseau Docker interne vers Jenkins : `http://jenkins:8080/sonarqube-webhook/`


```