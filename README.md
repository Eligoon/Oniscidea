# Oniscidea

## Vision


> This project is a backend API for managing training.
> The system allows user to customize their own exercises and training sessions.

---

## Links

Deployed application:  
<https://api.oniscidea.dk>

Source code repository:  
<https://github.com/Eligoon/Oniscidea>

Portfolio website:  
<https://eligoon.github.io/portfolio-2026/>

---

# Architecture

## System Overview

Example:

This project is built as a layered backend architecture using:

- Controller layer (REST endpoints)
- Service layer (business logic)
- Repository layer (database access)
- Tests

Technologies used:

- Java
- Javalin
- JPA / Hibernate
- PostgreSQL
- Maven
- JWT authentication
- Gherkin/Cucumber
- Cubby 
- Watchtower
- Docker Hub
- Git/GitHub

---

## Architecture Diagram

![img.png](img.png)


---

## Key Design Decisions
//TODO

Describe important technical decisions you made.

Examples:

- Why you chose your project structure
- How authentication works
- How errors are handled
- Why you chose specific libraries or frameworks

Example:

**Authentication**

Authentication is implemented using JWT tokens.  
Users log in and receive a token which must be included in subsequent API requests.

---

# Data Model

## ERD

![img_1.png](img_1.png)

Example entities:

- User
- Setlog
- Trainingsession
- Exercise
- trainingprogramexercise

Explenation:

---
// TODO
## Important Entities

Briefly describe the main entities in your system.

Examples:
### User

Represents a registered user in the system.

Fields:

- id
- username
- password
- role

### Activity

Represents a recorded workout activity.

Fields:

- id
- userId
- date
- description

---
// TODO
# API Documentation

Describe the most important endpoints.

## Example Endpoints

### Create User
POST /api/users


Request body:
{ “username”: “john”, “password”: “secret” }


Response:
201 Created


---
// TODO
### Get All Activities
GET /api/activities


Response:
[ { “id”:1, “userId”:1, “description”:“Running” } ]


---

# User Stories

- As a user, I want to register an account.
- As a user, I want to log in and out, and receive a token.
- As a user, I want to be able to pick or create a trainingsession on a calendar.
- As a user, I want bodyparts in an exercise to be color coded.
- As a user, I want to be able to write notes for my training sessions.
- As a user, I want be able to mark if I've done my trianing or not.
- As a user, I want to be able to writes seperate notes from training sessions.
- As a user, I want to be able to have several profiles which each their calendars and notes.
- As a user, I want to be able to view RIR (Reps in reserve) and it should be calculated automatically.

---
// TODO
# Development Notes

(Optional section)

Here you can briefly describe important challenges or lessons learned during development.

Examples:

- Handling authentication with JWT
- Structuring the service layer
- Testing the API