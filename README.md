# Monster Trading Cards Game
This project is a REST-based Java server designed to support a card game where players can trade, battle, and manage magical cards.

## Features

- User Management: Register, log in, and edit profiles with secure token-based authentication.
- Card Management: Acquire, organize, and trade cards with specific attributes and requirements.
- Battle System: Compete with other players using a deck of cards, with element-based battle mechanics and ELO scoring.
- Trading: Create and accept trade deals based on card types, attributes, and requirements.
- Scoreboard: View rankings based on ELO and game stats.
- Database Persistence: Data is stored securely using PostgreSQL.

Getting Started

1. Clone the repository:
   `git clone https://github.com/yourusername/mtcg.git`
2. Configure the Docker Container:
   `docker pull postgres:latest`
   `docker run --name MTCG -e POSTGRES_USER=MTCG -e POSTGRES_PASSWORD=MTCG -e POSTGRES_DB=MTCG -p 5432:5432 -d postgres:latest`
   `docker exec -it MTCG psql -U MTCG -d MTCG`
4. Or use your own Docker Container and change the credentials in the `DatabaseManager.java`
5. Create the tables:
   ```
   CREATE TABLE users (
       id SERIAL PRIMARY KEY,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL
   );
   
   CREATE TABLE user_tokens (
       username VARCHAR(255) UNIQUE NOT NULL,
       token VARCHAR(255) NOT NULL,
       FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
   );
   ```
6. Run the `Main.java` Class
7. Run the `ìntegration_tests.sh` script from the root of the Project:
   `cd src/test/testscripts`
   `./integration_tests.sh`