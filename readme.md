


# COURSE MANAGEMENT SYSTEM

A full-stack web application for managing Courses and Units, built with Spring Boot (backend) and Angular (frontend).  
It supports creating, viewing, updating, and deleting courses and their units with pagination, filtering, and caching.

## 🧩 Project Structure
```
Course_Project/
│
├── backend/               # Spring Boot REST API
│   ├── src/
│   ├── pom.xml
│   └── README.md (this file)
│
├── frontend/              # Angular frontend
│   ├── src/
│   ├── package.json
│   ├── angular.json
│   └── README.md
│
└── docker-compose.yml     # For containerized setup
```

#  Frontend (Angular)

The frontend is built using Angular 20 and communicates with the backend via REST API endpoints.  
It provides a responsive UI for creating, editing, and managing courses and their units.

---

##  Features

* Create and view courses with filters for board, medium, grade, and subject.
* Add, edit, and delete units for each course.
* Search, pagination, and sorting of courses.
* Dynamic UI updates after CRUD operations.
* Uses Angular Reactive Forms and Services for API communication.
* Environment-based configuration for API URLs.
* Centralized error handling and loading indicators.

---

##  Tech Stack

* Framework: Angular 20
* Language: TypeScript
* UI: HTML5, SCSS,  Bootstrap 
* HTTP Client: Angular HttpClient
* Routing: Angular Router


## 🧭 Components Overview

| Component | Description |
|------------|-------------|
| `course-list` | Displays list of courses with filters and pagination |
| `create-course` | Form to create a new course |
| `create-unit` | Add units to a selected course |
| `course-details` | View and manage specific course  |

---

## 🧰 Scripts

Run these inside the `course-frontend/` directory:

| Command | Description |
|----------|-------------|
| `npm install` | Install dependencies |
| `ng serve` | Run app in development mode (`http://localhost:4200`) |
| `ng build` | Build the production-ready app in `dist/` |
| `ng test` | Run unit tests with Jasmine and Karma |

---

#  Backend
>>>>>>> 1c080ae1 (Updated readme.md)

A Spring Boot REST API for managing Courses and Units, supporting CRUD operations, filtering, pagination, and caching.



## Features

* Create, read, update (PUT), patch (PATCH), delete courses.
* Manage units for each course.
* Support for multiple values per field (`board`, `medium`, `grade`) stored as JSONB in PostgreSQL.
* Pagination, filtering, sorting, and search for courses.
* Caching using Spring `@Cacheable` for frequent queries.
* Global exception handling with meaningful error messages.
* SLF4J logging for requests, responses, and exceptions.

---

## Tech Stack

* **Backend:** Java 17+, Spring Boot
* **Database:** PostgreSQL (JSONB support for lists)
* **Dependencies:**
    * Spring Web
    * Spring Data JPA
    * PostgreSQL Driver
    * Spring Cache
    * Lombok
    * Jackson

---





## Database Schema

**Course Table**

| Column      | Type   | Notes                     |
| ----------- | ------ | ------------------------- |
| id          | UUID   | Primary Key               |
| name        | String | Not null                  |
| description | String | Not null                  |
| board       | JSONB  | List of strings, not null |
| medium      | JSONB  | List of strings, not null |
| grade       | JSONB  | List of strings, not null |
| subject     | String | Not null                  |

**Unit Table**

| Column    | Type   | Notes                                |
| --------- | ------ | ------------------------------------ |
| id        | UUID   | Primary Key                          |
| title     | String | Not null                             |
| content   | String | Not null                             |
| course_id | UUID   | Foreign Key to `course.id`, nullable |

---

## API Endpoints

### Courses

| Method | URL               | Description           | Request Body                                                                |
| ------ | ----------------- | --------------------- | --------------------------------------------------------------------------- |
| POST   | /api/courses      | Create a course       | Course JSON                                                                 |
| GET    | /api/courses      | Get all courses       | Query params: board, grade, subject, search, page, size, orderBy, direction |
| GET    | /api/courses/{id} | Get course by ID      | -                                                                           |
| PUT    | /api/courses/{id} | Update course         | Course JSON                                                                 |
| PATCH  | /api/courses/{id} | Partial update course | Map<String, Object>                                                         |
| DELETE | /api/courses/{id} | Delete course         | -                                                                           |

### Units (for a specific course)

| Method | URL                                    | Description          | Request Body       |
| ------ | -------------------------------------- | -------------------- | ------------------ |
| POST   | /api/courses/{courseId}/units          | Add unit to course   | Unit JSON          |
| GET    | /api/courses/{courseId}/units          | Get units for course | Pageable params    |
| PUT    | /api/courses/{courseId}/units/{unitId} | Update unit          | Unit JSON          |
| PATCH  | /api/courses/{courseId}/units/{unitId} | Partial update unit  | Map<String,Object> |
| DELETE | /api/courses/{courseId}/units/{unitId} | Delete unit          | -                  |

---

## Example JSON

### Create Course

```json
{
  "name": "Mathematics Basics",
  "description": "Basic math course for grade 1",
  "board": ["CBSE", "ICSE"],
  "medium": ["English", "Hindi"],
  "grade": ["1", "2"],
  "subject": "Mathematics"
}
```

### Add Unit

```json
{
  "title": "Introduction to Numbers",
  "content": "Numbers from 1 to 10, counting and basic operations"
}
```

---

## Caching

* Enabled for frequently accessed methods using `@Cacheable`.
* `create`, `update`, `patch`, `delete` use `@CacheEvict` to invalidate cache.
* Check cache hits in logs by enabling debug:

```properties
logging.level.org.springframework.cache.interceptor.SimpleCacheInterceptor=DEBUG
```

---

## Logging & Error Handling

* SLF4J logging used for requests, responses, and operations in service classes.
* Global exception handler returns client-friendly messages with HTTP status codes.
* Example error response:

```json
{
  "success": false,
  "message": "At least one medium is required"
}
```

---

## Notes

* `board`, `medium`, and `grade` are stored as JSONB lists in the database.
* `Unit` is linked to `Course` via a many-to-one relationship.
* Cache is managed automatically using Spring Boot default cache manager.
* All non-null fields are validated in service layer.
