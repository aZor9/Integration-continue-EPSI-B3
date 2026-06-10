# tp_epsi_integration_continue

Avec ce Dockerfile, il faut d'abord construire l'image puis lancer un conteneur.

### 1. Construire l'image

Depuis le dossier contenant le Dockerfile :

```bash
docker build -t bad-practices-app .
```

### 2. Lancer le conteneur

Si ton application écoute sur le port 8080 :

```bash
docker run -p 8080:8080 bad-practices-app
```

Ou en arrière-plan :

```bash
docker run -d -p 8080:8080 --name bad-practices-container bad-practices-app
```

### Vérifier les logs

```bash
docker logs -f bad-practices-container
```





## Action et Commande pour installer et utiliser Jenkins : 

### Pull image :
```
docker pull jenkins/jenkins:lts
```

### Démarrer (docker run)

```
docker run -d --name jenkins-ci -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home jenkins/jenkins:lts
```

### Récupérer le mot‑de‑passe admin

```
docker exec jenkins-ci cat /var/jenkins_home/secrets/initialAdminPassword
```

### Démarrer (docker‑compose)

```
docker-compose up -d
```

### Arrêter le service

```
docker stop jenkins-ci && docker rm jenkins-ci (ou docker-compose down)
```

### Supprimer le volume (départ clean)

```
docker volume rm jenkins_home
```