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

```
docker run -d --name jenkins-ci -p 8080:8080 -p 50000:50000 -v jenkins_home:/var/jenkins_home -v //c/Users/FA506/Downloads/Code/Integration-continue-EPSI-B3:/workspace -u 1000:1000 jenkins/jenkins:lts
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



# ⚠️ ce qui est en dessous est en test

# si probleme dans jenkins lors du `file:///workspace` (dubious ownership.)

souvent le dossier `workspace` n'est pas reconnu par jenkins, il faut donc l'autoriser.

```
docker exec -it jenkins-ci /bin/bash
git config --global --add safe.directory /workspace
git config --global --get-all safe.directory
exit
```
puis supprimer le container et re run le container (ne pas supprimer le volume)