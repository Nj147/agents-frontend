
# Agents frontend

agents-frontend is a microservice which interacts with ARF (agents-registration-frontend) and AB (agents-backend). The microservice consists of a "Log in" and "Dashboard" system, both of which, send API requests to the backend. 

### Info

This project is a Scala Web application using <a href="https://github.com/hmrc/hmrc-frontend-scaffold.g8">code scaffolds</a>

### Running the service

```
sbt run
```
### Running the service tests

```
sbt test it:test
```

### Running the service tests with Coverage Report

```
sbt clean coverage test it:test coverageReport
```

### Dependencies

This service is dependant the folowing services:
* agents-registration-frontend
* agents-backend
* client-backend

### Routes

Start the service locally by going to http://localhost:9005/agents-frontend/agent-login

## Authors

* **Ayub Yusuf**
* **Isabel Lee**
* **Nathan Jackson**
* **Chetan Pardeep**
* **Ekip Kalir**
* **Daniel Carter**
