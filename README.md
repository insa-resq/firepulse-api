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

3. Start the application using Docker Compose:
    ```bash
    docker compose up --build -d --wait
    ```

### Accessing the API

- Config service: [http://localhost:8000/actuator/default](http://localhost:8000/actuator/default)
- Discovery service: [http://localhost:8001](http://localhost:8001)
- BrigadeFlow service: [http://localhost:8002](http://localhost:8002/docs/ui.html)
- PyroSense service: [http://localhost:8003](http://localhost:8003/docs/ui.html)
- StationLogix service: [http://localhost:8004](http://localhost:8004/docs/ui.html)
