# TaskFlow

TaskFlow is a beginner-friendly task and project manager with a React frontend, Java 21 / Spring Boot REST API, and MySQL database. The backend uses a conventional controller → service → repository → JPA entity structure. Project progress is derived from task completion; project status remains manually managed.

## Project layout

```text
TaskFlow/
├── backend/                 Spring Boot REST API
│   ├── pom.xml
│   └── src/main/java/com/taskflow/{entity,repository,service,controller,dto,exception}/
└── frontend/                React + Vite single-page app
    └── src/{components,services,styles}/
```

## Requirements and setup

Install JDK 21, Maven 3.9+, MySQL 8+, Node.js 20+, and Postman. Example Windows installation commands (run in an Administrator PowerShell if required): `winget install EclipseAdoptium.Temurin.21.JDK`, `winget install Oracle.MySQL`, `winget install OpenJS.NodeJS.LTS`, and `winget install Postman.Postman`. For Maven, download the official binary and add its `bin` folder to PATH:

```powershell
$mavenZip = Join-Path $env:TEMP 'apache-maven-3.10.0-bin.zip'
Invoke-WebRequest 'https://dlcdn.apache.org/maven/maven-3/3.10.0/binaries/apache-maven-3.10.0-bin.zip' -OutFile $mavenZip
Expand-Archive $mavenZip -DestinationPath 'C:\Tools'
$env:Path += ';C:\Tools\apache-maven-3.10.0\bin'
mvn -version
```

Restart the terminal after system-wide installers so PATH updates apply. Java 21 and Node/npm are available in this workspace. MySQL 8.4 is installed and running; Maven 3.10.0 was downloaded locally for the verified build.

1. Create a MySQL database and user (or use your local MySQL account):

   ```sql
   CREATE DATABASE taskflow_db;
   CREATE USER 'taskflow'@'localhost' IDENTIFIED BY 'taskflow123';
   GRANT ALL PRIVILEGES ON taskflow_db.* TO 'taskflow'@'localhost';
   ```

2. Set database credentials. Edit `backend/src/main/resources/application.properties`, or define `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables. Hibernate creates/updates the two tables at startup (`ddl-auto=update`). For this checkout, TaskFlow has a separate local MySQL instance on port 3307, stored in the ignored `.runtime/mysql-data` directory so it does not alter any other MySQL database. To restart that instance if needed:

   ```powershell
   & 'C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe' --basedir='C:\Program Files\MySQL\MySQL Server 8.4' --datadir="$PWD\.runtime\mysql-data" --port=3307 --bind-address=127.0.0.1 --mysqlx=0
   ```

   In the backend terminal, point Spring Boot to it before launching:

   ```powershell
   $env:DB_URL = 'jdbc:mysql://127.0.0.1:3307/taskflow_db?createDatabaseIfNotExist=true&serverTimezone=UTC'
   $env:DB_USERNAME = 'taskflow'
   $env:DB_PASSWORD = 'taskflow123'
   ```

   When using your own standard MySQL on port 3306 instead, use the SQL setup above and the default properties.
3. Start the backend from `backend`:

   ```powershell
   mvn spring-boot:run
   ```

   API root: `http://localhost:8080/api`. Health check: `GET http://localhost:8080/api/projects`.
4. In another terminal, start the frontend from `frontend`:

   ```powershell
   npm install
   npm run dev
   ```

   Open the URL printed by Vite (usually `http://localhost:5173`). Set `VITE_API_URL` if the backend is hosted somewhere other than `http://localhost:8080/api`.

## Features

- Dashboard totals, completion rate, project progress, status mix, upcoming deadlines, and recently created tasks are read from the API/database.
- Project and task create/read/update/delete flows; project-level task lists; status, priority, project, and overdue filters.
- Kanban status controls write changes immediately. Deadlines are ordered and overdue tasks are highlighted.
- Empty, loading, success, and API error states; responsive navigation and layouts.
- Deleting a project deletes its tasks through the JPA relationship cascade.

## File guide

- `backend/pom.xml` declares Spring Web, Spring Data JPA, Bean Validation, and the MySQL driver.
- `backend/src/main/resources/application.properties` configures port 8080, environment-overridable MySQL credentials, and Hibernate schema updates.
- `backend/src/main/java/com/taskflow/TaskflowApplication.java` is the Spring Boot entry point.
- `backend/src/main/java/com/taskflow/entity/Project.java` and `Task.java` define the JPA tables and their one-to-many relationship.
- `backend/src/main/java/com/taskflow/repository/ProjectRepository.java` and `TaskRepository.java` provide database CRUD and derived filters.
- `backend/src/main/java/com/taskflow/service/ProjectService.java`, `TaskService.java`, and `DashboardService.java` hold validation, CRUD rules, overdue logic, and computed statistics.
- `backend/src/main/java/com/taskflow/controller/ProjectController.java`, `TaskController.java`, and `DashboardController.java` map HTTP routes to those services.
- `backend/src/main/java/com/taskflow/dto/TaskRequest.java`, `TaskResponse.java`, `DashboardSummaryDTO.java`, and `ProjectProgressDTO.java` define validated input and API response shapes.
- `backend/src/main/java/com/taskflow/exception/ResourceNotFoundException.java` and `GlobalExceptionHandler.java` return consistent 404/400 JSON errors.
- `frontend/package.json`, `vite.config.js`, `index.html`, `src/main.jsx`, and `src/styles.css` configure Vite/React, mount the app, and style the responsive interface.
- `frontend/src/App.jsx` implements dashboard, project, task, Kanban, deadline, search, and detail views.
- `frontend/src/components/TaskForm.jsx` contains reusable task/project forms; `frontend/src/services/api.js` centralizes Axios calls.

Key annotations: `@SpringBootApplication` starts component scanning and auto-configuration; `@Entity` maps a class to a table; `@Id` and `@GeneratedValue` define generated primary keys; `@OneToMany` and `@ManyToOne` connect projects and tasks; `@JoinColumn` stores the foreign key; `@RestController` and `@RequestMapping` expose JSON APIs; `@Service` marks business logic; `@Transactional` keeps related database work in one transaction; `@Valid` triggers request validation; and `@RestControllerAdvice` handles errors across controllers.

## REST API / Postman walkthrough

Use `http://localhost:8080/api` as the base URL. JSON request examples:

**Create project** `POST /projects` → `201 Created`

```json
{"name":"Java Assignment","description":"Build a task tracker","startDate":"2026-11-13","deadline":"2026-12-10","status":"In Progress"}
```

**Create task** `POST /tasks` → `201 Created` (use a real project ID)

```json
{"title":"Design database","description":"Create project and task entities","priority":"High","status":"To Do","dueDate":"2026-11-25","projectId":1}
```

**Update project** `PUT /projects/1`:

```json
{"name":"Java Assignment v2","description":"Spring Boot and React","startDate":"2026-11-13","deadline":"2026-12-15","status":"In Progress"}
```

**Update task** `PUT /tasks/1`:

```json
{"title":"Design database schema","description":"Add JPA relationships","priority":"Medium","status":"In Progress","dueDate":"2026-11-28","projectId":1}
```

**Update status** `PUT /tasks/1/status` with body `{"status":"Completed"}`.

Try `GET /projects`, `GET /projects/1`, `GET /tasks`, `GET /tasks/project/1`, `GET /tasks/status/To%20Do`, `GET /tasks/priority/High`, and `GET /tasks/overdue`. Dashboard endpoints: `GET /dashboard/summary` and `GET /dashboard/project-progress`.

Expected success codes: reads/updates 200, create 201, delete 204. Invalid fields return 400 with a message; unknown IDs return 404. `GET /dashboard/summary` returns `totalProjects`, `totalTasks`, `pendingTasks`, `inProgressTasks`, `completedTasks`, `overdueTasks`, `completionPercentage`. Project progress rows contain project ID/name, task counts, and percentage.

Example summary response:

```json
{"totalProjects":1,"totalTasks":2,"pendingTasks":1,"inProgressTasks":0,"completedTasks":1,"overdueTasks":0,"completionPercentage":50.0}
```

For Postman, choose **Body → raw → JSON** for POST/PUT requests, set `Content-Type: application/json`, and send the examples above in sequence (create project first, then use its returned `id` as `projectId`). A create returns the saved resource with its generated ID. A delete returns an empty body with 204. A missing ID returns `{"error":"Task 99 not found"}` or the corresponding project message.

## Frontend demonstration

Start MySQL, backend, then frontend. Use the sidebar to add a project, add tasks and assign them to the project, change status in Tasks or Kanban, and revisit Dashboard/Deadlines to see calculated results. Search filters visible tasks and projects. All mutations refresh dashboard and list data through Axios.

## Implementation notes

- `@Entity`, `@Id`, and `@GeneratedValue` map Java objects to auto-increment database rows. `@OneToMany` / `@ManyToOne` model project ownership; JSON returns a small project summary to avoid circular serialization.
- Controllers expose HTTP routes; services validate and apply business rules; repositories derive database queries from method names.
- Project status is explicitly controlled by the user and isn’t silently changed when task status changes. Progress is a separate computed percentage.
- Task overdue means `dueDate < today` and status is not `Completed`.
- MySQL must be running and credentials valid before launching the backend. Dependency installation with `npm install` did not finish because registry access was unavailable here; install dependencies with network access before running. Maven and MySQL also need installation/configuration on this machine before the backend can launch.
