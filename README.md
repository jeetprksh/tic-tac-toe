# tic-tac-toe

A small web-based Tic-Tac-Toe game written with an Angular 19 frontend and a Spring Boot 4 backend that communicates with the client using a WebSocket endpoint.

---

## 🚀 Quick overview
- **Frontend:** Angular 19 app (client/) — built to static assets and served by Nginx in Docker.
- **Backend:** Spring Boot 4 application (server/) — exposes a WebSocket at `ws://<host>:8185/websocket` and runs on port **8185**.
- **Orchestration:** `docker-compose.yml` starts both services together so you can run the whole stack with one command.

---

## ✅ Prerequisites
- Docker (20+ recommended)
- Docker Compose (v2 integrated in Docker Desktop)
- (Optional for development) Node 20+, NPM, Maven/Java 21

---

## 🔧 Build & run with Docker Compose
From the repository root:

```bash
# Build images and start services in the background
docker compose up --build -d

# Check container status
docker compose ps

# Tail logs (backend / frontend)
docker compose logs -f backend
docker compose logs -f frontend

# Stop & remove containers
docker compose down
```

- Frontend will be available at: `http://localhost:4200`
- WebSocket endpoint: `ws://localhost:8185/websocket` (the frontend constructs the URL at runtime).

> ⚠️ If you still see the nginx welcome page after starting the frontend, try a hard refresh (Ctrl+F5) or open the site in an incognito window — sometimes the browser caches the default nginx page. If that doesn’t help, rebuild the frontend image: `docker compose up --build --force-recreate frontend -d` or remove the image and rebuild.

---

## 🧑‍💻 Development (run locally without Docker)
Frontend:
```bash
cd client
npm install
npm start          # runs ng serve on the host (usually port 4200)
```
Backend:
```bash
cd server
# Using Maven wrapper
./mvnw spring-boot:run
# or build and run jar
./mvnw -DskipTests package
java -jar target/*.jar
```

During development, the client will connect to `ws://localhost:8185/websocket` by default. You can inspect the WebSocket in browser DevTools → Network → WS.

---

## 🧪 Troubleshooting tips
- Make sure nothing else on the host is listening on **4200** or **8185**.
- If WebSocket fails, check backend logs (`docker compose logs -f backend`) and browser console for errors (CORS, connection refused).
- Confirm the backend is reachable: `curl -v http://localhost:8185` (should return 200 or at least an app response).

---