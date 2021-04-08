# MeFit

MeFit is a personal fitness goal app which allows users to set weekly workout goals. This repository
contains the backend portion of the case. 


## Deployment
A running instance can be found at https://expwefit.herokuapp.com and the documentation at <https://expwefit.herokuapp.com/>

After building the project with `gradlew bootJar`, deploy the project to heroku with:

```
heroku login
heroku create [appName] --region eu
heroku container:login
heroku container:push web --app [appName]
heroku container:release -a [appName] web
```

A running instance of Keycloak is required for auth, with three roles defined:`USER`, `CONTRIBUTOR` and `ADMIN` and a role mapper to add these roles to the token under "roles"

The backend requires two clients to be defined in Keycloak: one with access-type "public" for client logins and one with access type "confidential", service account enabled and permissions to administrate users. 
See `application.properties` for the environment variables. 
## Developers

Kristian Andersen, Lasse Minet and Tor Leeberg


