# Job Portal API

A production-ready Spring Boot REST API for a job portal with JWT authentication, role-based access control, CRUD operations, validation, search, pagination, tests, and Swagger/OpenAPI docs.

## Features

- Authentication: `/auth/register`, `/auth/login`
- Roles: `ADMIN`, `EMPLOYER`, `CANDIDATE`
- User CRUD: `/users` restricted to admins
- Job CRUD: `/jobs` with employer/admin write access and public read access
- Applications: candidates apply, employers review, admins manage all
- Search and pagination: `/jobs/search?keyword=&location=&company=&employmentType=&minSalary=&maxSalary=&page=&size=`
- Validation with `@Valid`, `@NotBlank`, `@Email`, and structured JSON errors
- BCrypt password hashing and stateless JWT security
- H2 for local/test use, MySQL profile for production
- Swagger UI at `/swagger-ui.html`

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- Spring Security
- H2 and MySQL
- JUnit 5, Mockito, MockMvc
- Springdoc OpenAPI

## Run Locally

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API runs at `http://localhost:8081`.

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

## Production Profile

Set environment variables before running with the `prod` profile:

```bash
export DB_URL=jdbc:mysql://localhost:3306/job_portal
export DB_USERNAME=root
export DB_PASSWORD=secret
export JWT_SECRET=a-long-random-production-secret
export JWT_EXPIRATION_MINUTES=120
export PORT=8081
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Render Deployment

Use the included `render.yaml` blueprint or create a new Render web service manually.

Required environment variables on Render:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=your-supabase-postgres-url
DB_USERNAME=your-db-user
DB_PASSWORD=your-db-password
JWT_SECRET=a-long-random-secret
JWT_EXPIRATION_MINUTES=120
DDL_AUTO=update
```

Render will assign your live URL after the service deploys. That URL is the one to use in your CV.

## Core Endpoints

| Method | Endpoint | Access | Description |
| --- | --- | --- | --- |
| POST | `/auth/register` | Public | Register and receive JWT |
| POST | `/auth/login` | Public | Login and receive JWT |
| GET | `/users` | Admin | List users |
| GET | `/users/{id}` | Admin | Get user |
| POST | `/users` | Admin | Create user |
| PUT | `/users/{id}` | Admin | Update user |
| DELETE | `/users/{id}` | Admin | Delete user |
| GET | `/jobs` | Public | Paginated job list |
| GET | `/jobs/{id}` | Public | Job details |
| GET | `/jobs/search` | Public | Search jobs |
| POST | `/jobs` | Admin, Employer | Create job |
| PUT | `/jobs/{id}` | Admin, owning Employer | Update job |
| DELETE | `/jobs/{id}` | Admin, owning Employer | Delete job |
| GET | `/applications` | Authenticated | Role-aware list |
| GET | `/applications/{id}` | Authenticated | Role-aware details |
| POST | `/applications` | Candidate | Apply to a job |
| PATCH | `/applications/{id}/status` | Admin, owning Employer | Update application status |
| DELETE | `/applications/{id}` | Admin, visible owner | Delete application |

## Example Requests

Register an employer:

```json
{
  "name": "Acme Recruiter",
  "email": "recruiter@example.com",
  "password": "password123",
  "role": "EMPLOYER"
}
```

Create a job with `Authorization: Bearer <token>`:

```json
{
  "title": "Backend Engineer",
  "description": "Build secure APIs with Spring Boot",
  "company": "Acme",
  "location": "Pune",
  "employmentType": "FULL_TIME",
  "minSalary": 800000,
  "maxSalary": 1600000
}
```

Apply as a candidate:

```json
{
  "jobId": 1,
  "coverLetter": "I have built several Spring Boot APIs and would love to contribute."
}
```

## Error Format

```json
{
  "timestamp": "2026-04-23T06:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/auth/register",
  "validationErrors": {
    "email": "must be a well-formed email address"
  }
}
```

## Tests

```bash
./mvnw test
```

Test profile uses an in-memory H2 database and MockMvc integration tests.

## Deployment Notes

- Use the `prod` profile with MySQL.
- Set a strong `JWT_SECRET`; never use the development default in production.
- Run `./mvnw clean package` to produce the deployable jar.
- Keep database migrations under version control before moving beyond prototype deployments.
