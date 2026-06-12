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
|---|---|
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
5. ⚠️ **Erreur courante (Branch Specifier)** : Par défaut, Jenkins cherche la branche `*/master`. Si votre dépôt Git utilise la branche `main` (ce qui est le cas par défaut sur GitHub), vous obtiendrez l'erreur `couldn't find remote ref refs/heads/master`. **Changez le champ "Branches to build" en `*/main`**.
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
   - **Kind** : `Secret text`
   - **Secret** : *(collez ici le token copié depuis SonarQube)*
   - **ID** : `sonar-token` *(attention, ce nom doit correspondre exactement à celui demandé dans le Jenkinsfile)*
6. Cliquez sur **Create**. Le pipeline pourra désormais s'authentifier et envoyer le code à analyser !

