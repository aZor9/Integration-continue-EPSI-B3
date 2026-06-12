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

## 📦 Volumes persistants

| Volume | Contenu |
|---|---|
| `jenkins_home` | Config Jenkins, jobs, plugins |
| `sonarqube_data` | Données SonarQube |
| `sonarqube_logs` | Logs SonarQube |
| `postgresql` | Base de données PostgreSQL |
