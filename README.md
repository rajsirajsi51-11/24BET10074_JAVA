# Student Management REST API

A beginner-friendly Java 17, Spring Boot, Maven, and MySQL project for managing university student records.

## Architecture

An HTTP request reaches `StudentController`, which validates the request and calls `StudentService`. The service contains the application logic and uses `StudentRepository` to read or write records. Spring Data JPA implements the repository using Hibernate, which maps `Student` objects to rows in MySQL. `GlobalExceptionHandler` converts expected errors into useful JSON responses.

```text
Postman -> Controller -> Service -> Repository -> JPA/Hibernate -> MySQL
               ^
               +---- GlobalExceptionHandler formats errors
```

## Prerequisites

- JDK 17 or later
- Maven 3.6.3 or later (or IntelliJ IDEA's bundled Maven)
- MySQL Server running locally
- Postman
- IntelliJ IDEA, Eclipse, or Spring Tool Suite (optional)

The project uses Spring Boot 3.5.16, Spring Web, Spring Data JPA, Jakarta Bean Validation, and the MySQL Connector/J driver. See [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html).

## Project structure

```text
src/main/java/com/example/studentmanagement/
├── StudentManagementApplication.java
├── controller/StudentController.java
├── entity/Student.java
├── exception/GlobalExceptionHandler.java
├── exception/ResourceNotFoundException.java
├── repository/StudentRepository.java
└── service/StudentService.java
src/main/resources/application.properties
pom.xml
```

## Database setup

1. Start MySQL Server and connect using MySQL Workbench or the MySQL command line.
2. Create the database once:

   ```sql
   CREATE DATABASE student_management_db;
   ```

3. The default connection uses `localhost:3306`, username `root`, and an empty password. Set your own credentials through environment variables; do not put a real password in source control.

   PowerShell, for the current terminal only:

   ```powershell
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "your_mysql_password"
   ```

   Optionally set a different JDBC URL with `DB_URL`. The default is `jdbc:mysql://localhost:3306/student_management_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`.

4. Hibernate's `ddl-auto=update` creates or updates the `students` table when the application starts. `show-sql=true` prints SQL statements for learning/debugging. For a real deployment, use a managed schema migration and avoid `update`.

## Build and run

From the project root:

```powershell
.\apache-maven-3.10.0\bin\mvn.cmd -s .maven-settings.xml spring-boot:run
```

This workspace includes a local Maven distribution and a local dependency cache setting for this computer. On another computer, install Maven and use `mvn spring-boot:run`, or open this folder as a Maven project in IntelliJ/Eclipse and run `StudentManagementApplication`. The API base URL is `http://localhost:8080/api/students`.

## Files and important code

- `StudentManagementApplication.java`: starts Spring Boot and component scanning.
- `entity/Student.java`: JPA entity and table mapping. `@Id` and `@GeneratedValue` let MySQL generate the ID. Bean Validation annotations reject blank names/courses, malformed email, and missing or nonpositive age.
- `repository/StudentRepository.java`: extends `JpaRepository` for standard create/read/update/delete methods. `findByCourseIgnoreCase` is a derived Spring Data query.
- `service/StudentService.java`: owns the CRUD operations and raises `ResourceNotFoundException` for missing IDs.
- `controller/StudentController.java`: maps HTTP routes to service operations. POST returns `201 Created` and a `Location` header; DELETE returns `204 No Content`.
- `exception/GlobalExceptionHandler.java`: maps missing records to 404, invalid input to 400, database constraint violations to 409, malformed JSON to 400, and unexpected failures to 500.
- `application.properties`: database connection and Hibernate settings. Credentials are read from environment variables.

All request examples below use header `Content-Type: application/json` when sending a body. Replace `1` in URLs with the ID returned by your POST request.

## Postman endpoint guide

### 1. Add a student

- Method: `POST`
- URL: `http://localhost:8080/api/students`
- Headers: `Content-Type: application/json`
- Body: select **raw** and **JSON**

  ```json
  {
    "name": "Aarav Sharma",
    "email": "aarav.sharma@example.com",
    "course": "Computer Science",
    "age": 20
  }
  ```

- Expected: `201 Created`, `Location: http://localhost:8080/api/students/1` (the ID will vary), and response body:

  ```json
  {
    "id": 1,
    "name": "Aarav Sharma",
    "email": "aarav.sharma@example.com",
    "course": "Computer Science",
    "age": 20
  }
  ```

### 2. Retrieve all students

- Method: `GET`
- URL: `http://localhost:8080/api/students`
- Headers/body: none
- Expected: `200 OK`, JSON array (empty `[]` if there are no records), for example:

  ```json
  [
    {
      "id": 1,
      "name": "Aarav Sharma",
      "email": "aarav.sharma@example.com",
      "course": "Computer Science",
      "age": 20
    }
  ]
  ```

### 3. Retrieve a student by ID

- Method: `GET`
- URL: `http://localhost:8080/api/students/1`
- Headers/body: none
- Existing ID expected: `200 OK` and the student JSON shown in step 1.
- Nonexistent ID expected (for example `/api/students/99999`): `404 Not Found`:

  ```json
  {
    "timestamp": "2026-11-13T10:00:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Student not found with id: 99999"
  }
  ```

  The timestamp is generated at request time.

### 4. Update student details

- Method: `PUT`
- URL: `http://localhost:8080/api/students/1`
- Headers: `Content-Type: application/json`
- Body (PUT supplies all fields):

  ```json
  {
    "name": "Aarav Sharma",
    "email": "aarav.sharma@example.com",
    "course": "Software Engineering",
    "age": 21
  }
  ```

- Existing ID expected: `200 OK` and the updated student JSON.
- Nonexistent ID expected: `404 Not Found` with the error format above.

### 5. Delete a student

- Method: `DELETE`
- URL: `http://localhost:8080/api/students/1`
- Headers/body: none
- Existing ID expected: `204 No Content` with an empty response body.
- Nonexistent ID expected: `404 Not Found` with the error format above.

### 6. Retrieve students by course

- Method: `GET`
- URL: `http://localhost:8080/api/students/course/Computer%20Science`
- Headers/body: none
- Expected: `200 OK` and a JSON array of matching students. Matching ignores letter case. If no student is enrolled in that course, the response is `200 OK` with `[]`.

### Validation example (400)

Send POST with a blank name, invalid email, blank course, or age `0`:

```json
{
  "name": " ",
  "email": "not-an-email",
  "course": "",
  "age": 0
}
```

Expected: `400 Bad Request`, with a `fieldErrors` object describing invalid fields, for example:

```json
{
  "timestamp": "2026-11-13T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "name": "Name must not be blank",
    "email": "Email must be a valid email address",
    "course": "Course must not be blank",
    "age": "Age must be a positive number"
  }
}
```

## Suggested Postman run order

1. POST Aarav Sharma and note the generated ID.
2. GET all and GET by that ID.
3. GET `/api/students/99999` to see the 404 response.
4. PUT the existing ID with updated details.
5. GET `/api/students/course/Software%20Engineering` to find the updated student.
6. DELETE the ID.
7. GET by the deleted ID and confirm the 404 response.

## Postman screenshots checklist

Capture the request method and URL, response status, and response body for: successful POST (201), GET all (200), GET by ID (200), missing ID (404), successful PUT (200), successful DELETE (204), course filter (200), validation failure (400), and GET after deletion (404). Avoid including database credentials in screenshots.

## Common troubleshooting

- **Access denied for user**: check `DB_USERNAME` and `DB_PASSWORD`; restart the app from the same terminal where you set them.
- **Communications link failure / connection refused**: make sure MySQL is running and the host/port in `DB_URL` are correct.
- **Unknown database**: execute `CREATE DATABASE student_management_db;` in MySQL.
- **Port 8080 already in use**: stop the other application or add `server.port=8081` to `application.properties` and update the Postman URLs.
- **Validation annotations not applied**: ensure `spring-boot-starter-validation` is present, the body parameter has `@Valid`, and Postman sends JSON with `Content-Type: application/json`.
- **Email already exists**: each student email must be unique. Use another email for a new record.
- **Maven cannot resolve dependencies**: check network access and run Maven again; the first build downloads dependencies.

## Upload to GitHub

1. Create an empty repository on GitHub (do not add a README if keeping this local README).
2. In a terminal at this project root, run:

   ```powershell
   git init
   git add .
   git commit -m "Build student management REST API"
   git branch -M main
   git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
   git push -u origin main
   ```

3. Replace the remote URL with your repository URL. Confirm `.gitignore` excludes build output and local IDE files before pushing. Never commit actual database passwords.
