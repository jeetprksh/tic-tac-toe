# Build stage
FROM node:20-alpine AS builder
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --silent

# Copy sources and build
COPY . ./
RUN npm run build -- --configuration production

# Production image
FROM nginx:stable-alpine
# Copy built files (Angular places browser build under dist/client/browser)
COPY --from=builder /app/dist/client/browser/ /usr/share/nginx/html/

# Ensure single-page-app routing works (serve index.html for unknown paths)
# Optional: you can provide a custom nginx config if you need history mode
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
