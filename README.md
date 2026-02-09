# PMT - Project Management Tool

## Presentation

PMT est une application web de gestion de projets collaboratifs permettant de creer des projets, gerer des equipes avec un systeme de roles, assigner des taches et suivre leur avancement via un dashboard Kanban.

Ce projet a ete developpe dans le cadre de la certification RNCP Niveau 7.

## Stack Technique

| Composant        | Technologie                |
|------------------|----------------------------|
| Backend          | Spring Boot 3.2.3, Java 17 |
| Frontend         | Angular 17, TypeScript     |
| Base de donnees  | MySQL 8.0                  |
| Tests Backend    | JUnit 5, Mockito, JaCoCo   |
| Tests Frontend   | Jasmine, Karma, Istanbul   |
| Containerisation | Docker, Docker Compose     |
| CI/CD            | GitHub Actions             |

## Prerequis

- **Docker** et **Docker Compose** (methode recommandee)
- OU **Java 17+**, **Node.js 20+** et **MySQL 8.0**

## Lancer l'application

### Avec Docker

```bash
git clone https://github.com/Mateodiet/Project-Tool-Management.git
cd Project-Tool-Management
cd backend
docker compose up -d
```

| Service      | URL                              |
|--------------|----------------------------------|
| Frontend     | http://localhost:80              |
| Backend API  | http://localhost:8080/api        |
| Health Check | http://localhost:8080/api/health |

### Sans Docker

```bash
# Terminal 1 - Backend
cd backend
./mvnw spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm install --legacy-peer-deps
npm start
```

Le frontend sera accessible sur http://localhost:4200 et le backend sur http://localhost:8080.

## Comptes de test

Des comptes sont pre-charges au demarrage de l'application via `DataInitializer` :

| Email          | Mot de passe |
|----------------|--------------|
| admin@pmt.com  | password123  |
| john@pmt.com   | password123  |
| marie@pmt.com  | password123  |
| pierre@pmt.com | password123  |

## Lancer les tests

Les captures des rapports de couverture sont disponibles dans le dossier `Frontend and Backend Coverage/` a la racine du projet.

### Backend

```bash
cd backend
./mvnw clean test jacoco:report
```

### Frontend

```bash
cd frontend
npm run test -- --no-watch --no-progress --browsers=ChromeHeadless --code-coverage
```

## Structure du projet

```
pmt-app/
├── backend/
│   ├── src/main/java/com/project/projectmanagment/
│   │   ├── config/             # Configuration (CORS, securite, donnees initiales)
│   │   ├── controller/         # Controleurs REST
│   │   ├── entities/           # Entites JPA
│   │   ├── models/             # DTOs et objets de requete/reponse
│   │   ├── repositories/       # Repositories Spring Data JPA
│   │   └── services/           # Services metier
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   ├── schema.sql
│   │   └── data.sql
│   ├── src/test/               # Tests unitaires (JUnit 5 + Mockito)
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/app/
│   │   ├── components/         # Composants Angular (login, dashboard, projects...)
│   │   ├── services/           # Services HTTP (auth, user, project, task)
│   │   ├── guards/             # Route guards
│   │   └── models/             # Interfaces TypeScript
│   ├── src/environments/       # Configuration par environnement
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml
└── .github/workflows/ci-cd.yml
```

## API REST

### Utilisateurs (`/api/user`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| POST    | `/register` | Inscription |
| POST    | `/login` | Connexion |
| GET     | `/all` | Liste des utilisateurs |
| GET     | `/{userId}` | Utilisateur par ID |
| GET     | `/email/{email}` | Utilisateur par email |
| PUT     | `/{userId}` | Modifier un utilisateur |
| DELETE  | `/{userId}` | Supprimer un utilisateur |
| PUT     | `/{userId}/deactivate` | Desactiver un compte |

### Projets (`/api/project`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| POST    | `/create?creatorEmail={email}` | Creer un projet |
| GET     | `/all` | Liste des projets |
| GET     | `/{projectId}` | Projet par ID |
| GET     | `/name/{projectName}` | Projet par nom |
| GET     | `/user/{email}` | Projets d'un utilisateur |
| PUT     | `/{projectName}` | Modifier un projet |
| DELETE  | `/{projectName}` | Supprimer un projet |
| POST    | `/invite` | Inviter un membre |
| GET     | `/accept-invite/{email}/{projectName}` | Accepter une invitation |
| GET     | `/{projectName}/members` | Membres du projet |
| GET     | `/{projectName}/member-role/{email}` | Role d'un membre |
| PUT     | `/{projectName}/member-role/{email}?role={role}` | Modifier le role |
| DELETE  | `/{projectName}/member/{email}` | Retirer un membre |

### Taches (`/api/task`)

| Methode | Endpoint | Description |
|---------|----------|-------------|
| POST    | `/create` | Creer une tache |
| GET     | `/all` | Liste des taches |
| GET     | `/{taskId}` | Tache par ID |
| GET     | `/project/{projectId}` | Taches d'un projet (par ID) |
| GET     | `/project/name/{projectName}` | Taches d'un projet (par nom) |
| GET     | `/user/{userId}` | Taches assignees a un utilisateur |
| GET     | `/status/{status}` | Taches par statut |
| PUT     | `/{taskId}?updatedBy={userId}` | Modifier une tache |
| DELETE  | `/{taskId}` | Supprimer une tache |
| GET     | `/{taskId}/history` | Historique des modifications |
| GET     | `/dashboard/{email}` | Statistiques dashboard |

## Roles par projet

Les roles sont attribues par projet via la table de liaison `project_member_tl` :

| Role        | Description |
|-------------|-------------|
| ADMIN       | Gestion complete du projet et de ses membres |
| MEMBRE      | Creation et modification des taches |
| OBSERVATEUR | Consultation en lecture seule |

## Pipeline CI/CD

Le workflow GitHub Actions (`ci-cd.yml`) s'execute sur push vers `main`, `master` et `develop` :

1. **Backend** : compilation, execution des tests, generation du rapport JaCoCo, packaging du JAR
2. **Frontend** : installation des dependances, execution des tests avec couverture, build de production
3. **Docker** (uniquement sur `main`/`master`) : build et push des images vers Docker Hub

Les rapports de couverture sont disponibles en tant qu'artifacts dans l'onglet Actions de GitHub.

## Base de donnees

| Table               | Description |
|---------------------|-------------|
| `user_tl`           | Utilisateurs de l'application |
| `project_tl`        | Projets |
| `project_member_tl` | Association utilisateur-projet avec role et statut |
| `task_tl`           | Taches avec statut, priorite et assignation |
| `task_history_tl`   | Historique des modifications de taches |

Le schema est initialise automatiquement via `schema.sql` et les donnees de test via `data.sql` au demarrage de l'application.
