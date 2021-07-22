
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

* **Ayub Yusuf** - [JBansal10](https://github.com/JBansal10)
* **Isabel Lee** - [sforsteracademytrainee](https://github.com/sforsteracademytrainee)
* **Nathan Jackson** - [qa-ihussain](https://github.com/qa-ihussain)
* **Chetan Pardeep** - [JakeReid2020](https://github.com/JakeReid2020)
* **Ekip Kalir** - [ayub96](https://github.com/ayub96)
* **Daniel Carter** - [ayub96](https://github.com/ayub96)
