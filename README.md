# Company Management System

A Spring Boot REST API for managing employees, customers, and sales — with JWT authentication, role-based access control, and audit logging.

---

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Security (JWT)
- Spring Data JPA / Hibernate
- PostgreSQL
- Lombok
- Maven

---

## Setup

### 1. Create the database

```sql
CREATE DATABASE company_db;
```

### 2. Configure credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/company_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### 3. Run

```bash
mvn spring-boot:run
```

On first run a default **DIRECTOR** account is created:
- username: `director`
- password: `director123`

---

## Authentication

All endpoints (except `/api/auth/login`) require a Bearer JWT token.

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "director",
  "password": "director123"
}
```

Response:
```json
{
  "success": true,
  "data": {
    "token": "eyJ...",
    "username": "director",
    "role": "DIRECTOR"
  }
}
```

Add the token to all subsequent requests:
```
Authorization: Bearer eyJ...
```

---

## Role Matrix

| Action | DIRECTOR | HR_MANAGER | CUSTOMER_MANAGER | SALES_MANAGER | EMPLOYEE |
|--------|----------|------------|-----------------|--------------|---------|
| Create employee | ✓ | ✓ | ✗ | ✗ | ✗ |
| Update employee | ✓ | ✓ | ✗ | ✗ | ✗ |
| View employee | ✓ | ✓ | ✗ | ✗ | LIMITED |
| Archive employee | ✓ | ✓ | ✗ | ✗ | ✗ |
| Register customer | ✗ | ✗ | ✓ | ✗ | ✗ |
| Update customer | ✗ | ✗ | ✓ | ✗ | ✗ |
| View customer | ✓ | ✗ | ✓ | ✗ | ✗ |
| Archive customer | ✗ | ✗ | ✓ | ✗ | ✗ |
| View all customers | ✓ | ✗ | ✗ | ✗ | ✗ |
| Create advertisement | ✗ | ✗ | ✗ | ✓ | ✗ |
| Update advertisement | ✗ | ✗ | ✗ | PARTIAL | ✗ |
| View advertisements | ✓ | ✗ | ✗ | ✓ | ✗ |
| View statistics | ✓ | ✗ | ✗ | ✗ | ✗ |

---

## API Endpoints

### Auth
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login, get JWT token |

### Employees
| Method | Endpoint | Roles | Description |
|--------|----------|-------|-------------|
| POST | `/api/employees` | DIRECTOR, HR_MANAGER | Create employee |
| PUT | `/api/employees/{id}` | DIRECTOR, HR_MANAGER | Update employee |
| GET | `/api/employees/{id}` | DIRECTOR, HR_MANAGER, EMPLOYEE | Get by ID |
| DELETE | `/api/employees/{id}` | DIRECTOR, HR_MANAGER | Archive employee |
| GET | `/api/employees?page=0&size=10` | DIRECTOR, HR_MANAGER | Paginated list |
| GET | `/api/employees/stats/by-department` | DIRECTOR | Count per department |
| GET | `/api/employees/stats/by-age?minAge=20&maxAge=40` | DIRECTOR | Filter by age |
| GET | `/api/employees/stats/total-salary` | DIRECTOR | Total salary sum |

### Customers
| Method | Endpoint | Roles | Description |
|--------|----------|-------|-------------|
| POST | `/api/customers` | CUSTOMER_MANAGER | Register customer |
| PUT | `/api/customers/{id}` | CUSTOMER_MANAGER | Update customer |
| GET | `/api/customers/{id}` | DIRECTOR, CUSTOMER_MANAGER | Get by ID |
| PATCH | `/api/customers/{id}/archive` | CUSTOMER_MANAGER | Archive customer |
| GET | `/api/customers/my` | CUSTOMER_MANAGER | My registered customers |
| GET | `/api/customers/all` | DIRECTOR | All customers paginated |
| GET | `/api/customers/stats` | DIRECTOR | Customer statistics |

### Advertisements
| Method | Endpoint | Roles | Description |
|--------|----------|-------|-------------|
| POST | `/api/advertisements` | SALES_MANAGER | Create advertisement |
| PUT | `/api/advertisements/{id}` | SALES_MANAGER | Update (cost, duration, startedAt only) |
| GET | `/api/advertisements/{id}` | DIRECTOR, SALES_MANAGER | Get by ID |
| GET | `/api/advertisements?page=0&size=10` | DIRECTOR, SALES_MANAGER | Paginated list |
| GET | `/api/advertisements/stats` | DIRECTOR | Sales statistics |

---

## Advertisement Update Rules

| Field | Updatable? |
|-------|------------|
| `adType` | ❌ No — fixed at creation |
| `enteredBy` | ❌ No — fixed at creation |
| `cost` | ✅ Yes |
| `durationDays` | ✅ Yes |
| `startedAt` | ✅ Yes |

---

## Statistics Endpoints

### Employee Stats (`/api/employees/stats/...`)
- `by-department` — count and % per department
- `by-age?minAge=X&maxAge=Y` — paginated filter by age
- `total-salary` — sum of all active employee salaries

### Customer Stats (`/api/customers/stats`)
- Daily registered count
- Top registrar employee
- Top 3 registrar employees
- Last 30 days count
- Busiest day in last month

### Advertisement Stats (`/api/advertisements/stats`)
- Highest cost ad type
- Employee with most ad entries
- Ads launched in last month
- Ads ended in last month
- Count per ad type

---

## Audit Logging

Every controller operation is logged to `logs/company-management.log` with:

```
[AUDIT] user=director | table=employees | endpoint=/api/employees | http=POST | method=create | crud=CREATE | args=[...] | time=2024-01-15T10:30:00
```

---

## Project Structure

```
src/main/java/com/company/management/
├── ManagementApplication.java
├── audit/
│   └── AuditAspect.java          # AOP logging
├── config/
│   ├── DataInitializer.java      # Creates default director on startup
│   └── SecurityConfig.java       # JWT + role-based access rules
├── controller/
│   ├── AuthController.java
│   ├── EmployeeController.java
│   ├── CustomerController.java
│   └── AdvertisementController.java
├── dto/
│   ├── request/                  # Input DTOs with validation
│   └── response/                 # Output DTOs
├── entity/
│   ├── Employee.java
│   ├── Customer.java
│   └── Advertisement.java
├── enums/
│   ├── Role.java
│   ├── Department.java
│   └── AdType.java
├── exception/
│   ├── NotFoundException.java
│   ├── AlreadyExistsException.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── EmployeeRepository.java
│   ├── CustomerRepository.java
│   └── AdvertisementRepository.java
├── security/
│   ├── UserDetailsServiceImpl.java
│   └── jwt/
│       ├── JwtService.java
│       └── JwtAuthFilter.java
└── service/
    ├── EmployeeService.java
    ├── CustomerService.java
    ├── AdvertisementService.java
    └── impl/
        ├── EmployeeServiceImpl.java
        ├── CustomerServiceImpl.java
        └── AdvertisementServiceImpl.java
```
