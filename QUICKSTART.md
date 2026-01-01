# Quick Start Guide

## Fastest Way to Run

```bash
# 1. Navigate to project

# 2. Start everything with Docker
docker-compose up --build

# 3. Open browser
# http://localhost:8080
```

## Test Credentials

**Admin Account:**
- Username: `admin`
- Password: `admin123`

**Regular User Account:**
- Username: `user`
- Password: `user123`

## Quick Test Checklist

- [ ] Register a new user at http://localhost:8080/register
- [ ] Login with the new user
- [ ] View and update profile
- [ ] Logout
- [ ] Login as admin (admin/admin123)
- [ ] Access admin dashboard at http://localhost:8080/admin/dashboard
- [ ] View all users at http://localhost:8080/admin/users

## Run Tests

```bash
# Run all tests
./mvnw test

# Or if you have Maven installed
mvn test
```

## Stop Application

```bash
# Stop Docker containers
docker-compose down

# Stop and remove volumes (clean database)
docker-compose down -v
```

## Common Commands

```bash
# Clean build
mvn clean package

# Run without Docker (requires PostgreSQL running)
java -jar target/demo-0.0.1-SNAPSHOT.war

# Check if containers are running
docker-compose ps

# View application logs
docker-compose logs -f app

# View database logs
docker-compose logs -f postgres

# Restart just the app (after code changes)
docker-compose restart app
```

## Tips

- Don't forget to add Docker Hub secrets to GitHub for CI/CD

## Quick Troubleshooting

**App won't start?**
- Check if port 8080 is available: `lsof -i :8080`
- Check Docker containers: `docker-compose ps`

**Database issues?**
- Stop containers: `docker-compose down`
- Remove volumes: `docker-compose down -v`
- Rebuild: `docker-compose up --build`

**Tests failing?**
- Clean build: `mvn clean test`
- Check Java version: `java -version` (should be 21)

