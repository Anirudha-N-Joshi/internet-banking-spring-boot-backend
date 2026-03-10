# NovaPay — Internet Banking Backend

A secure, production-ready REST API for an internet banking application built with Java, Spring Boot, and PostgreSQL. Implements JWT authentication with refresh token rotation, role-based access control, and full banking domain logic.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Programming language |
| Spring Boot 3 | Application framework |
| Spring Security | Authentication and authorization |
| Spring Data JPA | Database access layer |
| PostgreSQL | Relational database |
| JWT (jjwt) | Access token generation and validation |
| Hibernate | ORM |
| Lombok | Boilerplate reduction |
| Maven | Build tool |

---

## Features

- JWT access token (15 min expiry) + refresh token (7 days) with rotation
- Role-based access control — `USER` and `ADMIN` roles
- User registration, login, profile update, account deletion
- Bank account management (SAVINGS, CURRENT, FIXED_DEPOSIT)
- Fund transfers with balance validation
- Transaction history with incoming/outgoing filters
- Card management (create, block/unblock, delete)
- Beneficiary management
- PDF bank statement generation
- Admin endpoints — system stats, user lifecycle, account suspension
- Refresh token persistence in PostgreSQL with expiry validation
- Email notifications via Gmail SMTP for all key banking events

---

## Prerequisites

Make sure the following are installed before proceeding:

- **Java 17 or higher** → [Download](https://adoptium.net)
- **Maven 3.8+** → [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 14+** → [Download](https://www.postgresql.org/download)
- **Postman** (optional, for API testing) → [Download](https://www.postman.com)

Check your versions:
```bash
java --version
mvn --version
psql --version
```

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Anirudha-N-Joshi/internet-banking-spring-boot-backend.git
cd internet-backend
```

### 2. Create the PostgreSQL database

Open your PostgreSQL shell or pgAdmin and run:

```sql
CREATE DATABASE novapay;
```

### 3. Configure application properties

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/novapay
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=your-very-long-secret-key-minimum-256-bits-for-hs256-algorithm
jwt.access-token-expiry=900000

# Email (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-gmail-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# App name shown in email subject lines
app.name=NovaPay
```

> **Important:** Replace `yourpassword` with your actual PostgreSQL password and set a strong `jwt.secret` (minimum 32 characters).

> **Gmail App Password:** Gmail requires an App Password instead of your regular password when 2FA is enabled. Go to [Google Account](https://myaccount.google.com) → Security → 2-Step Verification → App Passwords → Generate one for "Mail". Never use your real Gmail password here.

### 4. Build the project

```bash
./mvnw clean install -DskipTests
```

On Windows:
```bash
mvnw.cmd clean install -DskipTests
```

### 5. Run the application

```bash
./mvnw spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/novapay-backend-0.0.1-SNAPSHOT.jar
```

The server will start at **http://localhost:8080**

> On first run, Hibernate will automatically create all tables in your database via `ddl-auto=update`.

---

## Project Structure


```
novapay-backend/
├── src/main/java/com/internetbanking/
│   ├── authentication/
│   │   ├── controller/LoginController.java       # Login, refresh, logout endpoints
│   │   ├── dto/LoginRequestDTO.java
│   │   ├── dto/LoginResponseDTO.java
│   │   ├── dto/RefreshRequestDTO.java
│   │   ├── dto/RefreshResponseDTO.java
│   │   └── service/AuthenticationWritePlatformService.java
│   ├── user/
│   │   ├── controller/UserController.java
│   │   ├── entity/User.java
│   │   ├── dto/UserRequestDto.java
│   │   ├── dto/UserResponseDTO.java
│   │   ├── dto/Role.java                         # Enum: USER, ADMIN
│   │   ├── dto/UserStatus.java                   # Enum: ACTIVE, INACTIVE
│   │   ├── repository/UserRepository.java
│   │   └── service/
│   │       ├── UserReadPlatformService.java
│   │       ├── UserReadPlatformServiceImpl.java
│   │       ├── UserWritePlatformService.java
│   │       └── UserWritePlatformServiceImpl.java
│   ├── account/
│   │   ├── controller/AccountController.java
│   │   ├── entity/Account.java
│   │   ├── dto/AccountRequestDTO.java
│   │   ├── dto/AccountResponseDTO.java
│   │   ├── dto/AccountStatus.java               # Enum: ACTIVE, SUSPENDED, CLOSED
│   │   └── repository/AccountRepository.java
│   ├── transaction/
│   │   ├── controller/TransactionController.java
│   │   ├── entity/Transaction.java
│   │   ├── dto/TransactionRequestDTO.java
│   │   ├── dto/TransactionResponseDTO.java
│   │   └── repository/TransactionRepository.java
│   ├── card/
│   │   ├── controller/CardController.java
│   │   ├── entity/Card.java
│   │   └── repository/CardRepository.java
│   ├── beneficiary/
│   │   ├── controller/BeneficiaryController.java
│   │   ├── entity/Beneficiary.java
│   │   └── repository/BeneficiaryRepository.java
│   ├── admin/
│   │   ├── controller/AdminController.java
│   │   ├── dto/AdminStatsDTO.java
│   │   └── service/
│   │       ├── AdminReadPlatformService.java
│   │       ├── AdminReadPlatformServiceImpl.java
│   │       ├── AdminWritePlatformService.java
│   │       └── AdminWritePlatformServiceImpl.java
│   ├── refreshtoken/
│   │   ├── entity/RefreshToken.java
│   │   ├── repository/RefreshTokenRepository.java
│   │   └── service/RefreshTokenService.java
│   ├── notification/
│   │   └── service/MailService.java              # All email notification logic
│   └── config/
│       ├── SecurityConfig.java                   # Spring Security + CORS config
│       └── JwtUtil.java                          # JWT generation and validation
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

---

## API Endpoints

### Authentication
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/authentication/login` | Public | Login, returns access + refresh token |
| POST | `/api/v1/authentication/refresh` | Public | Get new access token using refresh token |
| POST | `/api/v1/authentication/logout` | Public | Invalidate refresh token |

### Users
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/v1/users/register` | Public | Register new user |
| GET | `/api/v1/users/currentUser?userId=` | USER | Get current user profile |
| PUT | `/api/v1/users/{id}` | USER | Update user profile |
| DELETE | `/api/v1/users/{id}` | USER | Delete own account |

### Accounts
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/accounts/create` | USER | Create new bank account |
| GET | `/api/accounts/user/{userId}` | USER | Get all accounts for a user |
| GET | `/api/accounts/{accountNumber}/lookup` | USER | Look up account by number |
| GET | `/api/accounts/{accountId}/statement` | USER | Download PDF statement |
| DELETE | `/api/accounts/{id}` | USER | Delete account |

### Transactions
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/transactions/transfer` | USER | Transfer funds |
| GET | `/api/transactions/account/{accountId}` | USER | Get all transactions |
| GET | `/api/transactions/outgoing/{accountId}` | USER | Get outgoing transactions |
| GET | `/api/transactions/incoming/{accountId}` | USER | Get incoming transactions |

### Cards
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/cards/create` | USER | Create new card |
| GET | `/api/cards/user` | USER | Get all cards for user |
| PUT | `/api/cards/{cardId}/status` | USER | Block or unblock card |
| DELETE | `/api/cards/{cardId}` | USER | Delete card |

### Beneficiaries
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/beneficiaries/add` | USER | Add beneficiary |
| GET | `/api/beneficiaries/user` | USER | Get all beneficiaries |
| PUT | `/api/beneficiaries/{id}` | USER | Update beneficiary |
| DELETE | `/api/beneficiaries/{id}` | USER | Delete beneficiary |

### Admin
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/v1/admin/stats` | ADMIN | System-wide statistics |
| GET | `/api/v1/admin/users` | ADMIN | All users |
| PUT | `/api/v1/admin/users/{id}/status?status=` | ADMIN | Activate or deactivate user |
| DELETE | `/api/v1/admin/users/{id}` | ADMIN | Delete user |
| GET | `/api/v1/admin/accounts` | ADMIN | All accounts |
| PUT | `/api/v1/admin/accounts/{id}/status?status=` | ADMIN | Suspend or activate account |
| GET | `/api/v1/admin/transactions` | ADMIN | All transactions |

---

## Sample Request Bodies

### Login
```json
POST /api/v1/authentication/login
{
  "userName": "johndoe",
  "password": "password123"
}
```

### Register
```json
POST /api/v1/users/register
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "userName": "johndoe",
  "password": "password123",
  "phoneNumber": "9876543210",
  "address": "123 Main Street, Bangalore"
}
```

### Transfer
```json
POST /api/transactions/transfer
{
  "fromAccountNumber": "1234567890",
  "toAccountNumber": "0987654321",
  "amount": 500.00,
  "description": "Rent payment"
}
```

### Refresh Token
```json
POST /api/v1/authentication/refresh
{
  "refreshToken": "uuid-refresh-token-here"
}
```

---

## Security Configuration

CORS is configured to allow requests from the React frontend:

```java
config.setAllowedOrigins(List.of("http://localhost:5173"));
config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
config.setAllowedHeaders(List.of("*"));
config.setAllowCredentials(true);
```

Route security:
- `/api/v1/authentication/**` — public
- `/api/v1/users/register` — public
- `/api/v1/admin/**` — ADMIN role only
- Everything else — requires valid JWT

---

## Email Notifications

The app sends automatic emails for all key banking events using Gmail SMTP via `spring-boot-starter-mail`.

---

### Where to call each email method

Inject `EmailService` into the relevant service class and call after the main operation succeeds.

---

### Gmail App Password Setup

1. Go to [myaccount.google.com](https://myaccount.google.com)
2. Security → 2-Step Verification → turn it ON
3. Search for **App Passwords** → Select app: Mail → Select device: Other → name it "NovaPay"
4. Copy the 16-character password Google gives you
5. Paste it into `spring.mail.password` in `application.properties`

---

## Default Admin Account

After the first run, manually insert an admin user via SQL (password must be BCrypt encoded).
