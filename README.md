# Bookstore
Books I have at home :^)

## Getting Started
### For Users
This API allows you to browse books, read reviews, and interact with the community by creating your own reviews and comments.

#### Authentication
To create, edit, or delete reviews and comments, you must be authenticated.

* **Login**: Send a `POST` request to `/api/v1/auth/login` with `username` and `password` to get your session cookie.
  ```bash
  curl -X POST "http://localhost:8080/api/v1/auth/login" \
       -H "Content-Type: application/json" \
       -d '{
           "username": "user1",
           "password": "password"
       }'
  ```
* **Register**: Send a `POST` request to `/api/v1/auth/register` with `username`, `password`, and `displayName` to create a new user account.
  ```bash
  curl -X POST "http://localhost:8080/api/v1/auth/register" \
       -H "Content-Type: application/json" \
       -d '{
           "username": "newuser",
           "password": "password123",
           "displayName": "New User"
       }'
  ```

-----

### API Documentation

If you are logged in as an **ADMIN**, you can access the interactive API documentation at:

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### API Endpoints

All API endpoints are prefixed with `/api/v1`.

| Method | Endpoint | Description | Authentication | Example `curl` |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/books` | Get a list of all books | None | `curl "http://localhost:8080/api/v1/books"` |
| **GET** | `/books/search` | Search for books by title, genre, or rating | None | `curl "http://localhost:8080/api/v1/books/search?query=Harry%20Potter"` |
| **GET** | `/books/{id}` | Get a specific book by ID | None | `curl "http://localhost:8080/api/v1/books/1"` |
| **GET** | `/books/{bookId}/reviews` | Get all reviews for a specific book | None | `curl "http://localhost:8080/api/v1/books/1/reviews"` |
| **POST** | `/books/{bookId}/reviews` | Create a new review for a book | **Required** | `curl -X POST "http://localhost:8080/api/v1/books/1/reviews" -H "Content-Type: application/json" -d '{"rating": 5.0, "content": "Great read!"}'` |
| **GET** | `/reviews/{id}` | Get a specific review by ID | None | `curl "http://localhost:8080/api/v1/reviews/1"` |
| **PATCH** | `/reviews/{id}` | Update an existing review | **Required** (Author only) | `curl -X PATCH "http://localhost:8080/api/v1/reviews/1" -H "Content-Type: application/json" -d '{"content": "Updated content."}'` |
| **DELETE** | `/reviews/{id}` | Delete a review | **Required** (Author only) | `curl -X DELETE "http://localhost:8080/api/v1/reviews/1"` |
| **GET** | `/reviews/{reviewId}/comments` | Get all comments for a specific review | None | `curl "http://localhost:8080/api/v1/reviews/1/comments"` |
| **POST** | `/reviews/{reviewId}/comments` | Create a new comment for a review | **Required** | `curl -X POST "http://localhost:8080/api/v1/reviews/1/comments" -H "Content-Type: application/json" -d '{"content": "I agree!"}'` |
| **GET** | `/comments/{id}` | Get a specific comment by ID | None | `curl "http://localhost:8080/api/v1/comments/1"` |
| **PATCH** | `/comments/{id}` | Update an existing comment | **Required** (Author only) | `curl -X PATCH "http://localhost:8080/api/v1/comments/1" -H "Content-Type: application/json" -d '{"content": "Updated comment."}'` |
| **DELETE** | `/comments/{id}` | Delete a comment | **Required** (Author only) | `curl -X DELETE "http://localhost:8080/api/v1/comments/1"` |

-----

### For Developers

#### Prerequisites

- **Java 21 or higher**: The project is built using Java 21.
- **Maven**: To manage dependencies and build the project.
- **PostgreSQL**: The database used for storing all application data.

#### Setup

1.  **Clone the Repository**:

    ```bash
    git clone https://github.com/am-cid/Bookstore.git
    cd Bookstore
    ```

2. **Configure Environment Variables**

    Spring Boot automatically reads system environment variables. You must set the following before running the application.

    ```bash
    export PSQL_USERNAME=your_db_username
    export PSQL_PASSWORD=your_db_password
    export BS_USERNAME=admin
    export BS_PASSWORD=1234
    ```

    The `UserSeeder` uses these variables to create the default admin user. If `BS_USERNAME` and `BS_PASSWORD` are not provided, they default to `admin` and `1234` respectively, as configured in `application.properties`.
    `PSQL_*` are your PostgreSQL credentials that you want to use.


3. **Create Database Table (Conditional)**
    - **For `dev` profile**: No action is required. Your `application-dev.properties` is set to `spring.jpa.hibernate.ddl-auto=create-drop`, which means the database schema is automatically created and destroyed on each application restart.
    - **For `prod` profile**: Before running the application for the first time, you must ensure the `bookstore` database exists in your PostgreSQL instance. The schema itself will be managed by Liquibase. You can create the database with a command like `CREATE DATABASE bookstore;` in your PostgreSQL client.


4.  **Run with Maven**:

    Run the application in `dev` mode, which will automatically seed the database.

    ```bash
    ./mvnw spring-boot:run
    ```

    To run in `prod` mode, which enables Liquibase and skips data seeding, use:

    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
    ```

#### Database and Profiles

- **`dev` profile**:

    - `spring.jpa.hibernate.ddl-auto=create-drop`: The database schema is created and destroyed on each application restart. This is ideal for development and testing.
    - `spring.liquibase.enabled=false`: Liquibase is disabled.
    - Seeding is enabled via `CommandLineRunner`s to populate the database with sample data.

- **`prod` profile**:

    - `spring.jpa.hibernate.ddl-auto=update`: The database schema is updated incrementally.
    - `spring.liquibase.enabled=true`: Liquibase is used for schema migrations.
    - Seeding is disabled. The application expects the database to be managed via Liquibase.
