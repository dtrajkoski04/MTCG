# Documentation MCTG

I will document the progress of my project in this file.

## Table of Contents

- Project-definition
- UML-Diagram
- Design Decisions
- Implementation Progress
- Testing Strategy
- Challenges and Solutions

## Project-definition

The Monster Trading Card Game (MCTG) is a REST-based server application built using Java or C# to enable users to participate in a magical card game. Players can trade, manage, and battle with unique monster and spell cards. The project focuses exclusively on server-side development to support a variety of front-end interfaces (e.g., web, console, or desktop applications).

## UML-Diagram

![img.png](img.png)

## Design Decisions

### Overall Structure
The project is structured into logical packages to enforce separation of concerns and modularity. Each package is responsible for a specific layer or functionality, ensuring a clear boundary between HTTP handling, application logic, data persistence, and utility functions.

---

### httpserver Package

#### Purpose
The `httpserver` package handles the HTTP protocol implementation and request-response lifecycle. It abstracts the complexity of HTTP communication, providing a standardized interface for the application layer.

#### Subpackages and Classes
- **http Subpackage**:
    - `ContentType`: Enum defining supported content types (`PLAIN_TEXT`, `HTML`, `JSON`) for flexibility in response formats.
    - `HttpStatus`: Enum listing standard HTTP status codes to maintain consistency in server responses.
    - `Method`: Enum defining HTTP methods (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`) to standardize request processing.

- **server Subpackage**:
    - `HeaderMap`: Manages HTTP headers, enabling easy access to metadata like `Content-Length` and custom headers.
    - `Request`: Represents HTTP requests, encapsulating method, URL, headers, body, and path parts.
    - `Response`: Constructs HTTP responses with status, content type, and body.
    - `Server`: Initializes and starts the HTTP server, tying it to a specific port and router.
    - `Service`: Interface defining a contract for application services to process requests and generate responses.

---

### utils Package

#### Purpose
The `utils` package provides helper classes for routing, request handling, and building structured request objects. It acts as a bridge between raw HTTP communication and the server logic.

#### Classes
- `RequestBuilder`: Parses raw HTTP input streams and converts them into `Request` objects.
- `RequestHandler`: Manages individual client connections, ensuring thread safety and correct request processing.
- `Router`: Manages the mapping between URL paths and corresponding services, enabling dynamic and scalable routing.

---

### application Package

#### Purpose
The `application` package contains the main business logic and entities for the card game. It is divided into controllers, models, and services to follow the MVC pattern, ensuring separation of concerns.

#### Subpackages
- **controller Subpackage**:
    - Acts as a mediator between the services and external interfaces (e.g., API clients).
    - Classes include `Card`, `Deck`, `GameStats`, `Package`, `TradeDeal`, and `User`, which correspond to core entities of the game.

- **model Subpackage**:
    - Defines the core entities of the application:
        - `Card`: Represents cards with attributes like name, damage, and element type.
        - `Deck`: Represents a collection of cards selected for battles.
        - `GameStats`: Tracks user stats, including games played and ELO rating.
        - `Package`: Represents bundles of cards available for purchase.
        - `TradeDeal`: Captures details of card trades and associated conditions.
        - `User`: Represents a player with credentials, coins, and cards.
    - These classes serve as the backbone of the business logic, separating data representation from processing logic.

- **service Subpackage**:
    - Centralizes business logic, with one class per functional area:
        - `CardService`: Handles card management.
        - `GameService`: Implements game and battle mechanics, including ELO calculations.
        - `PackageService`: Manages card package creation and purchases.
        - `TradeService`: Facilitates card trading between players.
        - `UserService`: Manages user authentication, profile updates, and stats tracking.
- **persistence Subpackage**:
  - abstracts persistence, allowing the application to interact with the database or in-memory storage without exposing implementation details.
    - `repository package`: Contains the needed repositories for the different models.
      - `UserRepository`: Interface for the UserRepositoryImpl.
      - `UserRepositoryImpl`: Handles the transaction from a User Object to a Database Entry.
    - `DataAccessException`: Handles database access-related exceptions, encapsulating database-specific errors into a generic exception for higher-level handling.
    - `DatabaseManager`: Manages database connections, ensuring proper initialization, usage, and cleanup.
    - `UnitOfWork`: Implements the Unit of Work design pattern, managing transactions and ensuring atomicity for multiple database operations within a single business transaction.
---

### Design Rationale
1. **Separation of Concerns**: Each package has a clearly defined responsibility, minimizing dependencies and simplifying maintenance.
2. **Modularity**: By isolating HTTP handling, utilities, business logic, and persistence, the project supports scalability and extensibility.
3. **Reusability**: Models and services are reusable across different controllers, enabling future extension for additional features.
4. **Scalability**: The routing and service mechanisms allow dynamic addition and removal of features without modifying core server logic.
5. **Testability**: The design facilitates unit testing for individual components, such as repositories, services, and utilities.

This structure provides a robust foundation for building the Monster Trading Card Game server, ensuring clean organization and efficient handling of HTTP requests and business logic.


## Implementation Process

1. Using the given Structure from the `httpserverbase` Structure for the MTCG Project
2. Testing the Given Structure and Code so the Server runs correctly
3. Connecting the database to the application
4. Changing the model for the `User` Model to Register/Login with a generated token
5. Changing the routes for the `REST-API` so the Register/Login can be tested
6. Changing the used database for storing the Users and tokens into the database
7. Implementing the curl Script
8. Changing the Readme

## Intermediate Hand-In
### Overview

This intermediate submission focuses on the foundational implementation of the MTCG server. Key achievements include setting up a REST API server, configuring the database for user data storage, and ensuring that users can securely register and log in.

### Implemented Features

**Technology Stack**:
   
- The application is implemented in Java.
No HTTP helper frameworks were used, adhering to the project's requirements.
Server Setup:

- A custom HTTP server was built to listen for client connections on a defined port.
It includes mechanisms for parsing HTTP requests, managing headers, handling query parameters, and processing request bodies.
Core Features:

**User Registration**: Enables users to register with unique usernames and passwords.

**User Login**: Validates user credentials and issues session tokens for authentication.
**Routing**: A routing mechanism maps incoming requests to appropriate controllers based on URL paths.

### The User Model

The User model represents a player in the system. Each user has:

- A **username** that uniquely identifies them.
- A **password** for authentication.
- A **token** issued upon successful login to manage sessions securely.

This model is central to the server's ability to authenticate and track user sessions during gameplay.

### The Card Model

The Card model serves as the base class for all cards in the system, representing both MonsterCard and SpellCard. This abstraction ensures extensibility and adherence to the specification.

**Attributes**:
- name: The unique name of the card.
- damage: A constant value representing the card's attack power.
- elementType: Indicates the elemental type (e.g., fire, water, normal).

**Card Types**:
- MonsterCard: Represents cards that are primarily monsters with physical attacks. Element types do not affect pure monster vs. monster battles.
- SpellCard: Represents cards that use elemental-based spells, allowing for advantages or disadvantages based on elemental interactions.

**Key Features**:
- Both card types inherit from the Card base class.
- Abstract methods like getCardType are overridden in subclasses to specify the card type.
- Extensibility is ensured, allowing additional card-specific behaviors or attributes to be implemented later.
- Usage in Gameplay: Cards are integral to the battle and trading mechanics, serving as the primary resources players use to compete or trade in the MTCG system.

### Database Configuration

The server uses a PostgreSQL database for persistence. A Docker container was set up to run the database, configured with the following schema:

- **Users Table**: Stores user credentials with a unique username and a password.
- **User Tokens Table**: Stores session tokens for logged-in users, linked to the corresponding username.

The database is configured to enforce relationships between these tables, ensuring data integrity. For instance:

A user's token is automatically deleted if the user is removed from the system.

### How Users and Tokens Are Stored

**Registration** Process:

When a user registers, their username and password are validated and stored in the database.
The system ensures that usernames are unique, preventing duplicate entries.
Login Process:

During **login**, the provided credentials are checked against the database.
If valid, a session token is generated and stored. Tokens are updated for subsequent logins, ensuring users can only have one active session at a time.
Session Tokens:

Tokens are uniquely formatted to associate them with the user and session. These tokens allow users to securely perform actions on the server.
### Challenges and Solutions

Challenge: Storing passwords securely.
Solution: Passwords are currently stored as plaintext for simplicity, but future improvements will incorporate password hashing techniques.
Challenge: Efficient database operations.
Solution: Implemented a transaction management system that ensures efficient database interaction and maintains data consistency.

### Testing and Validation

Endpoints were thoroughly tested using curl commands. Tests included:

- Registering users with various input scenarios to ensure validation works as expected.
- Logging in with valid and invalid credentials to verify token generation and error handling.

### Summary

This submission successfully delivers the foundational components of the MTCG server, including:

- A custom REST API server with routing and request-handling mechanisms.
User registration and login functionalities.
- A well-structured database for secure storage of user credentials and session tokens.
These features establish a strong basis for expanding the application in the next development phase, incorporating gameplay mechanics, trading, and more advanced functionalities.

## Testing Strategy
