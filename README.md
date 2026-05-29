# 🚗 Uber Ride Platform

A production-style ride-hailing backend built with Spring Boot 3, JWT, Kafka, and Docker. Supports three roles — Rider, Driver, and Admin — with a full ride state machine, real fare calculation, and event-driven architecture.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3.x |
| Security | Spring Security + JWT |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Messaging | Apache Kafka |
| Geo / Math | Haversine (custom util) |
| API Docs | Swagger / SpringDoc OpenAPI |
| Build | Maven |
| Deploy | Docker Compose |

---

## Ride State Machine

```
REQUESTED → ACCEPTED → ONGOING → COMPLETED
                ↓
            CANCELLED
```

| Status | Trigger |
|---|---|
| `REQUESTED` | Rider submits pickup + drop location |
| `ACCEPTED` | Available driver accepts — locked to that driver |
| `ONGOING` | Driver starts the ride, timer begins |
| `COMPLETED` | Driver ends ride, final fare calculated via Haversine |
| `CANCELLED` | Rider or Driver cancels before ONGOING |

---

## Project Structure

```
src/main/java/com/uber/
├── config/          # SecurityConfig, KafkaTopic, AppConfig, SwaggerConfig
├── controller/      # AuthController, RideController, DriverController, AdminController
├── DTO/             # Request + Response DTOs
├── entity/          # User, Driver, Ride, RideRating
├── exceptions/      # GlobalHandler + custom exceptions
├── filter/          # JwtFilter
├── kafka/           # RideEventProducer, RideEventConsumer
├── repository/      # UserRepo, DriverRepo, RideRepo, RideRatingRepo
├── services/        # AuthService, UserService, DriverService, AdminService (+ Impls)
└── Status.java      # Ride status enum
```

---

## API Endpoints

### Auth (Public)
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register/rider` | Register a new rider |
| POST | `/auth/register/driver` | Register a new driver |
| POST | `/auth/login` | Login — returns JWT token |

### Rider
| Method | Endpoint | Description |
|---|---|---|
| POST | `/rides/request` | Request a ride with pickup + drop coordinates |
| GET | `/rides/fare` | Get fare estimate before booking |
| PUT | `/rides/{rideId}/cancel` | Cancel a ride |
| GET | `/rides/history/{userId}` | View ride history |
| POST | `/rides/{rideId}/rating` | Rate a completed ride (1–5) |

### Driver
| Method | Endpoint | Description |
|---|---|---|
| GET | `/driver/rides/pending` | View all REQUESTED rides |
| PUT | `/driver/rides/{rideId}/accept` | Accept a ride |
| PUT | `/driver/rides/{rideId}/cancel` | Cancel an accepted ride |
| PUT | `/driver/rides/{rideId}/start` | Start a ride |
| PUT | `/driver/rides/{rideId}/end` | End a ride, finalise fare |
| GET | `/driver/rides/history` | View own ride history |
| GET | `/driver/earnings` | View earnings for a period (days) |
| POST | `/driver/rides/{rideId}/rating` | Rate a rider after completion |

### Admin
| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/users` | All users |
| GET | `/admin/drivers` | All drivers with stats |
| GET | `/admin/drivers/{driverId}` | Driver details |
| GET | `/admin/drivers/{driverId}/earnings` | Driver earnings for a period |

---

## Fare Calculation (Haversine)

Fare is calculated at **ride completion** using stored coordinates — not estimated upfront.

```
fare = 50 + (distanceKm × 10)
```

Distance is derived from the Haversine formula using real lat/lng coordinates — the same math production mapping systems use.

---

## Kafka Events

| Topic | Trigger |
|---|---|
| `ride-requested` | Rider submits a ride request |
| `ride-accepted` | Driver accepts a ride |
| `ride-cancelled` | Rider or driver cancels |
| `ride-completed` | Driver ends the ride |

---

## Running Locally

### Prerequisites
- Java 17
- Maven
- MySQL 8 (installed locally)
- Docker + Docker Compose (for Kafka + Zookeeper)

### Steps

```bash
# Clone the repo
git clone https://github.com/your-username/uber-ride-platform.git
cd uber-ride-platform

# Start Kafka + Zookeeper via Docker
docker-compose up -d

# Run the app locally
mvn spring-boot:run
```

### Environment / application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/uberdb
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.kafka.bootstrap-servers=localhost:9092

jwt.secret=your_jwt_secret
earth.radius=6371
```

---

## Running Tests

```bash
mvn test
```

Unit tests cover all service-layer branches using Mockito:
- `DriverServiceTests` — ride accept, cancel, start, end, earnings, ratings
- `UserServiceTests` — ride request, cancel, history, ratings

---

## Swagger Docs

Once the app is running, visit:

```
http://localhost:8080/swagger-ui.html
```

---

## Docker Compose

```bash
docker-compose up -d
```

Spins up:
- Apache Kafka
- Zookeeper

> MySQL runs locally. Make sure your `application.properties` points to your local MySQL instance.