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