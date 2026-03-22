# tic-tac-toe

Web-based Tic-Tac-Toe with:
- Frontend: Angular 19 in `client/` (no `@angular/material`, renders through custom CSS grid now)
- Backend: Spring Boot 4 in `server/` (Java 21, WebSocket endpoint `/websocket`)
- Docker + compose: runs frontend in Nginx and backend in Spring Boot within isolated services

---


## ✅ Prerequisites
### Docker flow (recommended)
- Docker Desktop / Engine
- Docker Compose (v2 embedded in Docker Desktop)

### Local dev (no Docker)
- Node 20+, npm (for frontend)
- Java 21 (for backend)
- Maven or Maven wrapper (`mvnw`)

---

## 🔥 Run with Docker Compose (full stack)
From repo root:

```bash
docker compose up --build -d
```

- Frontend: `http://localhost:4200`
- Backend WS: `ws://localhost:8185/websocket`

Check status:
```bash
docker compose ps
```

View logs:
```bash
docker compose logs -f frontend
docker compose logs -f backend
```

Stop and remove:
```bash
docker compose down
```

### Verify
- open browser devtools, confirm UI game board shows.
- check WS connection to `/websocket` is established.

---

## 🛠️ Local development (without Docker)
### Frontend
```bash
cd client
npm install
npm run build -- --configuration production    # or npm start for live dev
```

To run in dev mode with live reload:
```bash
npm start
```

### Backend
```bash
cd server
./mvnw spring-boot:run
```

or build + run:
```bash
./mvnw -DskipTests package
java -jar target/*.jar
```

- App will stay on `http://localhost:4200` in frontend, and `ws://localhost:8185/websocket` for backend socket.

---

## 🧾 Common checks
1. **Cache**: Remove `.angular/cache` and `dist` if behavior looks stale.
2. **Nginx welcome page**: trigger hard refresh (Ctrl+F5) after re-deploy.
3. **Material leftovers**: no `mat-` selectors in `client/dist/client` after proper rebuild.
4. **Backend health**: `curl -v http://localhost:8185` (HTTP root serves Spring Boot endpoint). 

---

## 📌 Notes
- The app uses runtime WebSocket URL construction in `app.component.ts` to support local and container hostnames.
- The project now departs from Angular Material and uses a clean plain CSS 3x3 grid for game board rendering.
- Docker ensures production artifacts are independent of local dev env and uses `npm ci` for deterministic install.
