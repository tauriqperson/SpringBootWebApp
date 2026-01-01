# Spring Boot User Management System

A full-featured Spring Boot web application with user registration, authentication, profile management, and role-based access control (RBAC).

## Features

- **User Registration**: New users can sign up with username, email, password, and full name
- **Authentication**: Secure login/logout with session management
- **Profile Management**: Users can view and update their profile information
- **Role-Based Access Control**: 
  - Regular users can access their profile
  - Admin users can view dashboard and list all users
- **Security**: 
  - Passwords hashed with BCrypt
  - CSRF protection enabled
  - Session-based authentication
  - Protected endpoints
- **Validation**: Server-side form validation for all inputs
- **Responsive UI**: Clean, modern interface built with Thymeleaf and CSS

## Technologies Used

- **Framework**: Spring Boot 4.0.2
- **Language**: Java 21
- **Security**: Spring Security 6
- **ORM**: Hibernate/JPA (Spring Data JPA)
- **Database**: PostgreSQL (production), H2 (testing)
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito, Spring MockMvc
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions

## Prerequisites

- **Java 21** (Required - Java 25 is not yet supported by Spring Boot dependencies)
- Maven 3.9+
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (if running without Docker)

**Important**: This project requires **Java 21 exactly**. Java 25 will cause test failures due to Byte Buddy/Mockito compatibility issues. Docker uses Java 21 automatically.

## Getting Started

### Running Locally with Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd SpringBootWebApp
   ```

2. **Build and run with Docker Compose**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Open browser: http://localhost:8080
   - The app will start with two pre-created users:
     - **Admin**: username: `admin`, password: `admin123`
     - **User**: username: `user`, password: `user123`

### Running Locally without Docker

1. **Start PostgreSQL**
   ```bash
   # Make sure PostgreSQL is running on localhost:5432
   # Create database: userdb
   ```

2. **Build the application**
   ```bash
   mvn clean package
   ```

3. **Run the application**
   ```bash
   java -jar target/springbootwebapp-0.0.1-SNAPSHOT.war
   ```

4. **Access the application**
   - Open browser: http://localhost:8080

## Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=UserServiceTest
```

## Docker Commands

```bash
# Build Docker image
docker build -t spring-boot-user-management .

# Run with Docker Compose
docker-compose up -d

# Stop containers
docker-compose down

# View logs
docker-compose logs -f app

# Rebuild and restart
docker-compose up --build --force-recreate
```

## API Endpoints

### Public Endpoints
- `GET /` - Home page
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /register` - Registration page
- `POST /register` - Process registration

### Authenticated Endpoints
- `GET /profile` - View profile
- `POST /profile/update` - Update profile
- `POST /logout` - Logout

### Admin Endpoints (Admin role only)
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/users` - List all users

## Security Features

1. **Password Encryption**: BCrypt hashing algorithm
2. **CSRF Protection**: Enabled for all forms
3. **Session Management**: HTTP session-based authentication
4. **Role-Based Access**: Method-level security with `@EnableMethodSecurity`
5. **Input Validation**: Server-side validation with Bean Validation
6. **SQL Injection Prevention**: JPA/Hibernate parameterized queries

## Database Schema

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);
```

## CI/CD Pipeline

**Status**: GitHub Actions workflow is implemented and configured.

The CI/CD pipeline automatically:

1. **Runs tests** on every push and pull request
2. **Builds Docker image** after successful tests
3. **Pushes to Docker Hub** on push to main/master branches
4. **Fails the build** if any tests fail

### Workflow Details

The pipeline consists of two jobs:

**Test Job:**
- Triggers on every push and pull request
- Sets up JDK 21 with Maven caching
- Runs all unit and integration tests
- Generates and uploads test reports
- Runs on all branches

**Build-and-Push Job:**
- Only runs after tests pass
- Only triggers on push to main/master branches
- Uses Docker Buildx for optimized builds
- Tags images with `latest` and commit SHA
- Implements layer caching for faster builds
- Pushes to Docker Hub registry

### Setup Instructions

To enable the workflow, add these secrets to your GitHub repository (Settings → Secrets and variables → Actions):

- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub access token (not your password)

**How to create a Docker Hub access token:**
1. Log in to Docker Hub
2. Go to Account Settings → Security
3. Click "New Access Token"
4. Give it a name and copy the token
5. Add it to GitHub secrets as `DOCKER_PASSWORD`

### Viewing Workflow Results

- **Actions Tab**: View all workflow runs in the GitHub Actions tab
- **Pull Requests**: See test status checks before merging
- **Commits**: Each commit shows the workflow status
- **Docker Hub**: Check your Docker Hub repository for pushed images

## Deployment

**Status**: Application is not yet deployed to a cloud provider.

### Deployment Options

The application can be deployed to various cloud providers:

#### Option 1: Heroku
```bash
# Login to Heroku
heroku login

# Create app
heroku create your-app-name

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Deploy
git push heroku main
```

#### Option 2: Google Cloud Run
```bash
# Authenticate
gcloud auth login

# Set project
gcloud config set project PROJECT_ID

# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/spring-boot-webapp

# Deploy to Cloud Run with PostgreSQL Cloud SQL
gcloud run deploy spring-boot-webapp \
  --image gcr.io/PROJECT_ID/spring-boot-webapp \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars DB_HOST=your-cloud-sql-host,DB_NAME=userdb,DB_USER=postgres,DB_PASSWORD=your-password
```

#### Option 3: AWS Elastic Beanstalk
```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p docker spring-boot-webapp --region us-east-1

# Create environment with RDS
eb create spring-boot-env --database

# Deploy
eb deploy
```

#### Option 4: Azure Container Apps
```bash
# Login to Azure
az login

# Create resource group
az group create --name spring-boot-rg --location eastus

# Create container registry
az acr create --resource-group spring-boot-rg --name yourregistry --sku Basic

# Build and push image
az acr build --registry yourregistry --image spring-boot-webapp:latest .

# Deploy to Container Apps
az containerapp up \
  --name spring-boot-webapp \
  --resource-group spring-boot-rg \
  --image yourregistry.azurecr.io/spring-boot-webapp:latest \
  --target-port 8080 \
  --ingress external
```

### Deployment Log

**Status**: Not yet deployed

*To be updated once deployment is completed with:*
- Cloud provider chosen
- Deployment date
- Application URL
- Database configuration steps
- Any issues encountered and resolutions

## Development

### Adding New Features

1. Create entity in `model/` package
2. Create repository interface in `repository/`
3. Implement service in `service/`
4. Create controller in `controller/`
5. Add Thymeleaf template in `templates/`
6. Write tests in `test/`

## Troubleshooting

### Common Issues

**Port already in use**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection failed**
```bash
# Check PostgreSQL is running
docker-compose ps

# Check database credentials in application.properties
```

**Tests failing**
```bash
# Clean and rebuild
mvn clean install
```

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Docker Documentation](https://docs.docker.com/)

## Default Users

The application creates two default users on startup:

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Role: ADMIN
   - Can access all endpoints including admin dashboard

2. **Regular User**
   - Username: `user`
   - Password: `user123`
   - Role: USER
   - Can access profile and update own information
