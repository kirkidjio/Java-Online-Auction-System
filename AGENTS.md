You will be working on an online auction project.

The project has the following architecture:

* The `auction-authorization-service` directory contains a service implemented as a modular monolith. It consists of two modules: `lots` and `users`.

  * `lots` is responsible for the auction business logic. If necessary, the business rules can be found in `business requirements.txt` located in the service directory.
  * `users` is responsible for user management, authorization, authentication, and all related functionality.
* The `notifications-microservice` directory contains a notification microservice written in Kotlin.

RabbitMQ is used as the message broker.

The services follow Clean Architecture principles. Therefore, it is important to maintain a clear separation between business logic (where applicable), infrastructure, the service layer, and the API layer.

Code implementation must be accompanied by documentation in the form of comments in english directly in the code.

Each service contains a PowerShell script that generates a database dump containing information about the database tables and their columns. Before starting work with a service, run its dump script to inspect the current database schema.
