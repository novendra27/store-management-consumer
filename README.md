# Sales Transaction Kafka Consumer

A Spring Boot application that consumes sales transaction messages from Apache Kafka, processes them, and stores the transaction data in a PostgreSQL database with automatic stock management.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.x-black.svg)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.7-blue.svg)](https://www.postgresql.org/)

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Database Schema](#-database-schema)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Kafka Message Format](#-kafka-message-format)
- [Logging](#-logging)
- [Error Handling](#-error-handling)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

## ✨ Features

- **Real-time Transaction Processing**: Consumes sales transactions from Kafka topics in real-time
- **Automatic Stock Management**: Updates product inventory automatically upon transaction
- **Dual Date Format Support**: Accepts both array `[2026,2,13]` and string `"2026-02-13"` date formats
- **Transaction Logging**: Comprehensive audit trail with stock change logs
- **Low Stock Alerts**: Automatic warnings when product stock falls below threshold
- **Error Handling**: Robust exception handling with custom business exceptions
- **Structured Logging**: Multiple log files (application, Kafka, transaction, error) with rotation
- **Database Transaction Management**: ACID-compliant with automatic rollback on failures
- **Flexible JSON Parsing**: Supports both `camelCase` and `snake_case` field naming

## 🏗️ Architecture

```
┌─────────────┐         ┌──────────────────┐         ┌──────────────┐
│   Kafka     │ ──────> │  Spring Boot     │ ──────> │  PostgreSQL  │
│   Broker    │  JSON   │  Consumer App    │  SQL    │   Database   │
└─────────────┘         └──────────────────┘         └──────────────┘
                               │
                               ▼
                        ┌──────────────┐
                        │  Log Files   │
                        │  (./logs/)   │
                        └──────────────┘
```

### Transaction Flow

1. **Message Reception**: Kafka consumer receives JSON message from topic
2. **Validation**: Validates JSON format and skips non-JSON messages
3. **Deserialization**: Converts JSON to DTO with custom date deserializer
4. **Business Logic**:
   - Validates product existence
   - Checks stock availability
   - Creates transaction history record
   - For each item:
     - Inserts transaction detail
     - Creates stock log entry (negative quantity)
     - Updates product stock
   - Calculates and updates total price
5. **Commit**: Database transaction committed or rolled back on error
6. **Logging**: All steps logged to appropriate log files

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 4.0.2 | Application Framework |
| **Spring Data JPA** | 4.0.2 | Database ORM |
| **Spring Kafka** | Latest | Kafka Integration |
| **PostgreSQL** | 17.7 | Database |
| **Hibernate** | 7.2.1 | JPA Implementation |
| **Lombok** | Latest | Code Generation |
| **Jackson** | 2.20.2 | JSON Processing |
| **Logback** | Latest | Logging Framework |
| **Maven** | 3.x | Build Tool |

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java Development Kit (JDK) 21** or higher
- **Apache Maven 3.6+** for building the project
- **PostgreSQL 12+** database instance
- **Apache Kafka 3.x** broker (or access to Kafka cluster)
- **Git** for cloning the repository

## 🗄️ Database Schema

The application uses the following tables:

### `product`
Main product information with current stock levels.
```sql
- id (PK)
- sku
- product_name
- category_id (FK)
- supplier_id (FK)
- current_stock (auto-updated)
- price
- created_at
- updated_at
```

### `transaction_history`
Master record for each sales transaction.
```sql
- id (PK)
- transaction_date
- total_price (calculated)
- created_at
```

### `transaction_detail`
Line items for each transaction.
```sql
- id (PK)
- transaction_id (FK)
- product_id (FK)
- qty
- price (snapshot)
- total_price (qty × price)
- created_at
```

### `stock_log`
Audit trail for all stock movements.
```sql
- id (PK)
- product_id (FK)
- quantity_change (negative for sales)
- log_type ("SALE")
- created_at
```

### `category` & `supplier`
Reference tables for product categorization.

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/sales-transaction-consumer.git
cd sales-transaction-consumer
```

### 2. Configure Database

Create a PostgreSQL database and update the connection details in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configure Kafka

Update Kafka connection settings in `application.properties`:

```properties
kafka.bootstrap-servers=localhost:9092
kafka.topic.sales-transaction=sales-transaction-topic
spring.kafka.consumer.group-id=sales-consumer-group
```

### 4. Build the Project

```bash
./mvnw clean install
```

Or on Windows:
```cmd
mvnw.cmd clean install
```

## ⚙️ Configuration

### Application Properties

Create `src/main/resources/application.properties`:

```properties
# Application Name
spring.application.name=consumer
spring.main.web-application-type=none

# Database Configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/sales_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# HikariCP Connection Pool
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.idle-timeout=30000

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Kafka Configuration
kafka.bootstrap-servers=localhost:9092
kafka.topic.sales-transaction=sales-transaction-topic

# Kafka Consumer Settings
spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=sales-consumer-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.enable-auto-commit=true
```

### Logging Configuration

Logging is configured in `src/main/resources/logback-spring.xml`. Logs are written to:

- `logs/consumer-app.log` - All application logs
- `logs/consumer-app-error.log` - Error logs only (retained 90 days)
- `logs/kafka-consumer.log` - Kafka consumer specific logs
- `logs/transaction.log` - Transaction processing logs

Log rotation: 10MB max file size, 30 days retention, archived to `logs/archive/`

## 🎯 Running the Application

### Using Maven

```bash
./mvnw spring-boot:run
```

Or on Windows:
```cmd
mvnw.cmd spring-boot:run
```

### Using Java

```bash
./mvnw clean package
java -jar target/consumer-0.0.1-SNAPSHOT.jar
```

### Expected Console Output

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v4.0.2)

[INFO] Started ConsumerApplication in 9.227 seconds
[INFO] Kafka consumer connected to broker: localhost:9092
[INFO] Subscribed to topic: sales-transaction-topic
[INFO] Kafka consumer is ready to process messages...
```

## 📨 Kafka Message Format

### Input Message Schema

Send JSON messages to the Kafka topic in this format:

```json
{
  "transaction_date": [2026, 2, 13],
  "items": [
    {
      "productId": 1,
      "qty": 2
    },
    {
      "productId": 2,
      "qty": 1
    }
  ]
}
```

**Alternative Date Format** (also supported):
```json
{
  "transaction_date": "2026-02-13",
  "items": [...]
}
```

**Alternative Field Names** (also supported):
```json
{
  "transaction_date": [2026, 2, 13],
  "items": [
    {
      "product_id": 1,  // snake_case
      "qty": 2
    }
  ]
}
```

### Field Descriptions

| Field | Type | Format | Required | Description |
|-------|------|--------|----------|-------------|
| `transaction_date` | Array or String | `[YYYY, M, D]` or `"YYYY-MM-DD"` | Yes | Transaction date |
| `items` | Array | Object array | Yes | List of products sold |
| `items[].productId` | Integer | Number | Yes | Product ID from database |
| `items[].qty` | Integer | Number > 0 | Yes | Quantity sold |

### Testing with Kafka Console Producer

```bash
# Start Kafka console producer
kafka-console-producer.bat --bootstrap-server localhost:9092 --topic sales-transaction-topic

# Paste JSON message and press Enter
{"transaction_date":[2026,2,13],"items":[{"productId":1,"qty":1}]}
```

## 📊 Logging

### Log Files

All logs are written to the `logs/` directory:

```
logs/
├── consumer-app.log          # All application logs
├── consumer-app-error.log    # Error logs only (90 day retention)
├── kafka-consumer.log        # Kafka consumer activity
├── transaction.log           # Transaction processing details
└── archive/                  # Rotated log files
    ├── consumer-app-2026-02-13.1.log
    ├── kafka-consumer-2026-02-13.1.log
    └── ...
```

### Log Format

```
2026-02-13 09:48:17.661 [thread-name] INFO  logger.name - Log message
```

### Sample Logs

**Successful Transaction:**
```log
2026-02-13 09:48:17.661 INFO  - ✓ Parsed message: date=2026-02-13, items=1
2026-02-13 09:48:17.793 INFO  - Transaction history created with ID: 5
2026-02-13 09:48:17.841 INFO  - ✓ SUCCESS - Transaction processed: id=5, totalPrice=8500000.00
```

**Stock Warning:**
```log
2026-02-13 09:48:17.841 WARN  - ⚠️ Low stock alert for product ID: 1 (Laptop). Current stock: 5
```

**Error Handling:**
```log
2026-02-13 09:48:17.972 ERROR - ✗ Business Error [PRD001]: Product not found: id=999
```

## 🛡️ Error Handling

### Business Exceptions

The application uses custom business exceptions with error codes:

| Error Code | Description | Action |
|------------|-------------|--------|
| `PRD001` | Product Not Found | Transaction rolled back |
| `PRD002` | Insufficient Stock | Transaction rolled back |
| `TRX001` | Transaction Processing Error | Transaction rolled back |
| `KFK001` | Kafka Message Parsing Error | Message skipped, logged |

### Validation Rules

1. ✅ Product ID must exist in database
2. ✅ Quantity must be greater than 0
3. ✅ Available stock must be >= requested quantity
4. ✅ Transaction date cannot be null
5. ✅ Items array cannot be empty
6. ✅ JSON format must be valid

### Error Response

Errors are logged but do not crash the consumer. Invalid messages are skipped with warning logs:

```log
⚠️ Skipped non-JSON message: 2026-02-13 09:36:17 - invalid format...
```

## 📁 Project Structure

```
consumer/
├── src/
│   ├── main/
│   │   ├── java/javadev/project/consumer/
│   │   │   ├── configuration/
│   │   │   │   └── AppConfig.java              # Spring & Kafka config
│   │   │   ├── dto/
│   │   │   │   ├── TransactionRequestDTO.java  # Input DTO
│   │   │   │   ├── TransactionItemDTO.java     # Transaction item DTO
│   │   │   │   └── TransactionDateDeserializer.java  # Custom deserializer
│   │   │   ├── entity/
│   │   │   │   ├── product.java                # Product entity
│   │   │   │   ├── category.java               # Category entity
│   │   │   │   ├── supplier.java               # Supplier entity
│   │   │   │   ├── transactionHistory.java     # Transaction master
│   │   │   │   ├── transactionDetail.java      # Transaction detail
│   │   │   │   └── stockLog.java               # Stock audit log
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java      # Custom exception
│   │   │   │   ├── ErrorCode.java              # Error code enum
│   │   │   │   └── GlobalExceptionHandler.java # Exception handler
│   │   │   ├── kafka/
│   │   │   │   └── SalesTransactionConsumer.java  # Kafka listener
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java      # Product CRUD
│   │   │   │   ├── TransactionHistoryRepository.java
│   │   │   │   ├── TransactionDetailRepository.java
│   │   │   │   └── StockLogRepository.java
│   │   │   ├── service/
│   │   │   │   ├── TransactionService.java     # Business logic
│   │   │   │   └── ProductService.java         # Product operations
│   │   │   └── ConsumerApplication.java        # Main application
│   │   └── resources/
│   │       ├── application.properties          # Configuration
│   │       └── logback-spring.xml             # Logging config
│   └── test/
│       └── java/javadev/project/consumer/
│           └── ConsumerApplicationTests.java   # Unit tests
├── logs/                                       # Log files (auto-created)
├── target/                                     # Build output
├── mvnw                                        # Maven wrapper (Unix)
├── mvnw.cmd                                    # Maven wrapper (Windows)
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

## 🧪 Testing

### Test Messages

Create test data in your database first:

```sql
-- Insert test product
INSERT INTO product (sku, product_name, category_id, supplier_id, current_stock, price)
VALUES ('LAP001', 'Laptop ASUS', 1, 1, 100, 8500000);

-- Send test message through Kafka
```

### Verify Transaction

```sql
-- Check transaction history
SELECT * FROM transaction_history ORDER BY created_at DESC LIMIT 1;

-- Check transaction details
SELECT * FROM transaction_detail WHERE transaction_id = <last_id>;

-- Check stock log
SELECT * FROM stock_log ORDER BY created_at DESC LIMIT 5;

-- Check updated product stock
SELECT id, product_name, current_stock FROM product WHERE id = 1;
```

## 🔧 Troubleshooting

### Common Issues

**1. Unable to connect to Kafka**
```
Error: Connection to node -1 (...) could not be established
```
Solution: Verify Kafka broker is running and `bootstrap-servers` configuration is correct.

**2. Database connection refused**
```
Error: Connection refused. Check that the hostname and port are correct
```
Solution: Verify PostgreSQL is running and credentials in `application.properties` are correct.

**3. Product not found**
```
ERROR - ✗ Business Error [PRD001]: Product not found: id=1
```
Solution: Ensure product exists in database before sending transaction.

**4. Insufficient stock**
```
ERROR - ✗ Business Error [PRD002]: Insufficient stock
```
Solution: Check product's `current_stock` is >= requested quantity.

## 📝 Development Notes

### Adding New Features

1. **Add New DTO Fields**: Update `TransactionRequestDTO.java` and related deserializers
2. **Custom Validations**: Extend `TransactionService.validateTransactionItems()`
3. **New Error Codes**: Add to `ErrorCode.java` enum
4. **Additional Logging**: Configure in `logback-spring.xml`

### Performance Tuning

- Adjust HikariCP pool size in `application.properties`
- Configure Kafka consumer threads via `spring.kafka.listener.concurrency`
- Enable batch processing for high throughput scenarios

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

**Made with ❤️ using Spring Boot & Apache Kafka**
