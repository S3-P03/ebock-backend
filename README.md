# ebock-backend
This repository contains the files for the REST API of EBock, built in Java, using Quarkus and MyBatis.

Copy the .env file from the Teams group into the root of this project. This file contains environment variables that are required to run the projet.


## Requirements
Make sure you have these installed before proceeding with this repository :

    - IntelliJ 
    - Java
    - Gradle JVM : Oracle OpenJDK 26.0.1 (Similar Gradle versions are also functional)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

If you use IntelliJ (recommended), you can also run the application using the play button with the option **ebock-backend.main**.
If any changes are detected to the files while running, the next API call will restart the API with the new changes.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Structure
The project is divided into packages, each containing a certain type of class for the API : 

    - adapter : Adapter to communicate with authentication system.
    - business : Model classes that match the expected mapping from the database.
    - converter : Interfaces to convert Payload to Business, or Business to Response.
    - dto/request : Model classes for the payloads used in request bodies.
    - dto/response : Model classes for the expected responses from API requests.
    - exception : Custom exception handlers.
    - mapper : MyBatis interfaces that associate a function to a SQL query.
    - service : Classes associated to API paths that are called by the client. These classes handle the logic and call the adapters, mappers and converters when necessary.
    - websocket : Websockets to browse real-time information to clients connected to the related websocket.
    - utils : Other useful custom functions used in the application.

## Contribution
Work must not be done directly in the main and dev branches. A branch must be created for every task, named after the associated Jira ticket.
Commits must specify the changes done.
Every feature is expected to be tested, using the **src/test** package, with unit tests and integration tests.
Once a feature is functional, tested and documented, a pull request must be opened toward the **dev** branch. Two reviews must then be submitted.
Once two contributors have reviewed the pull request, the creator can merge into dev using **Squash & merge**.
**IMPORTANT : The branch should be deleted once the pull request is merged.**