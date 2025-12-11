# firepulse-api

## Development Setup

### Requirements

- IntelliJ IDEA
- Docker
- Docker Compose

### Running the Application

1. Ensure that you are on the INSA Toulouse network or connected via VPN.

2. Forward the database port to your local machine:
    ```bash
    ssh -N -L 5432:localhost:5432 <user>@192.168.37.100
    ```

3. Create a `.env` file in the root directory and set up the environment variables as described in [`.env.example`](.env.example).

4. Start the application using Docker Compose:
    ```bash
    docker compose up --build -d --wait
    ```

### Accessing the API

- Discovery service: [http://localhost:8761](http://localhost:8761)
- Gateway service: [http://localhost:8080/api/actuator/health](http://localhost:8080/api/actuator/health)
  - Accounts service: [http://localhost:8080/api/accounts-service/docs/ui.html](http://localhost:8080/api/accounts-service/docs/ui.html)
  - Detection service: [http://localhost:8080/api/detection-service/docs/ui.html](http://localhost:8080/api/detection-service/docs/ui.html)
  - Planning service: [http://localhost:8080/api/planning-service/docs/ui.html](http://localhost:8080/api/planning-service/docs/ui.html)
  - Registry service: [http://localhost:8080/api/registry-service/docs/ui.html](http://localhost:8080/api/registry-service/docs/ui.html)
