# AMAP Marketplace Project

**Team Members:**

- [Ton Nom]
- [Nom du binôme si applicable]

## Project Description

Marketplace application for an AMAP (Association for the Maintenance of Family Farming) allowing customers to order products from multiple local producers through a centralized platform.

The system consists of:

- **Marketplace** (central platform): customers can browse products, add to cart, and place orders
- **Producer applications** (2): producers can manage their products and view received orders
- **MongoDB sharded cluster**: data is sharded by producer for optimized performance

## Architecture

- **MongoDB**: 1 config server, 2 shards (mushrooms & bread), 1 mongos router
- **Applications** (Dockerized):
    - Marketplace: Spring Boot (port 8080)
    - Producer Mushrooms: Spring Boot (port 8081)
    - Producer Bread: Spring Boot (port 8082)

## Prerequisites

- Docker & Docker Compose
- Git Bash (for Windows users to run .sh scripts)

## 🚀 Quick Start (Recommended - Full Docker Setup)

### 1. Launch Everything with Docker

```bash
cd mongodb

# Build and start all containers (MongoDB + Applications)
docker-compose up -d --build
```

**Wait 1-2 minutes** for all containers to start and applications to initialize.

### 2. Initialize MongoDB Sharding

```bash
# Initialize sharding configuration
./init-sharding.sh

# Configure shards for each producer
./add-producer-shard.sh mushrooms shard1RS
./add-producer-shard.sh bread shard2RS
```

### 3. Access Applications

- **Marketplace**: http://localhost:8080
- **Producer Mushrooms**: http://localhost:8081
- **Producer Bread**: http://localhost:8082

**That's it! Everything is running in Docker!** 🐳

---

## Alternative: Local Development Setup

If you prefer to run applications locally (outside Docker) for development:

### Prerequisites

- Java 25 (or compatible JDK)
- Maven

### 1. Launch Only MongoDB

```bash
cd mongodb

# Start only MongoDB containers
docker-compose up -d configsvr shard1 shard2 mongos

# Initialize sharding
./init-sharding.sh
./add-producer-shard.sh mushrooms shard1RS
./add-producer-shard.sh bread shard2RS
```

### 2. Launch Applications Locally

**Using IntelliJ IDEA:**

- Open each application and click the green play button

**Using Maven (command line):**

Terminal 1:

```bash
cd producer-mushrooms
mvn spring-boot:run
```

Terminal 2:

```bash
cd producer-bread
mvn spring-boot:run
```

Terminal 3:

```bash
cd marketplace
mvn spring-boot:run
```

---

## Usage Guide

### 1. Add Products (Producers)

**Mushroom Producer:**

- Go to http://localhost:8081
- Click "Add Product"
- Fill in product details (e.g., name: "Shiitake", price: 8.50, stock: 20)
- Submit

**Bread Producer:**

- Go to http://localhost:8082
- Click "Add Product"
- Fill in product details (e.g., name: "Sourdough Bread", price: 4.50, stock: 15)
- Submit

### 2. Place Orders (Marketplace)

**Register/Login:**

- Go to http://localhost:8080
- Click "Register" and create an account
    - Example: John Doe, john@example.com, password123
- Or login if you already have an account

**Shop:**

- Browse available products from both producers
- Add products to your cart (you can add from both producers)
- Click "Cart" in the navigation
- Review your cart
- Click "Checkout" to place your order

**View Orders:**

- Click "My Orders" to see your order history
- Notice: orders are automatically split by producer

### 3. View Orders (Producers)

**Each producer can:**

- Click "View Orders" on their application
- See all orders received for their products
- View order details (customer, products, quantities, total)

---

## Verify MongoDB Sharding

### Check sharding status

```bash
docker exec -it mongos mongosh --port 27020 --eval "sh.status()"
```

### View data distribution across shards

```bash
docker exec -it mongos mongosh --port 27020 --eval "
use amap;
db.products.getShardDistribution();
db.orders.getShardDistribution();
"
```

### Verify chunks are on correct shards

```bash
docker exec -it mongos mongosh --port 27020 --eval "
use config;
db.chunks.find({}, {ns:1, min:1, max:1, shard:1}).pretty();
"
```

---

## Useful Docker Commands

### View logs

```bash
cd mongodb

# All services
docker-compose logs -f

# Specific service
docker-compose logs -f marketplace
docker-compose logs -f producer-mushrooms
```

### Restart a service

```bash
docker-compose restart marketplace
docker-compose restart producer-mushrooms
docker-compose restart producer-bread
```

### Stop everything

```bash
docker-compose down
```

### Rebuild applications after code changes

```bash
docker-compose up -d --build producer-mushrooms producer-bread marketplace
```

---

## Clean Database & Reset

To completely reset the system:

```bash
cd mongodb

# Stop and remove all containers
docker-compose down

# Remove all data volumes
docker volume rm mongodb_config-data mongodb_shard1-data mongodb_shard2-data

# Rebuild and restart everything
docker-compose up -d --build

# Wait 1-2 minutes, then reinitialize sharding
./init-sharding.sh
./add-producer-shard.sh mushrooms shard1RS
./add-producer-shard.sh bread shard2RS
```

---

## Technologies Used

- **Backend**: Spring Boot 4.0, Spring Data MongoDB
- **Frontend**: Thymeleaf, Tailwind CSS (via CDN)
- **Database**: MongoDB 7.0 (sharded cluster)
- **Containerization**: Docker, Docker Compose
- **Build Tool**: Maven

---

## Project Structure

```
amap-project/
├── marketplace/                  # Central marketplace application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── producer-mushrooms/           # Mushroom producer application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── producer-bread/               # Bread producer application
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── mongodb/                      # MongoDB & Docker configuration
│   ├── docker-compose.yml        # Orchestrates all services
│   ├── init-sharding.sh          # Initialize sharding
│   └── add-producer-shard.sh     # Add producer shard (parameterizable)
└── README.md
```

---

## Scripts Explanation

### `init-sharding.sh`

Initializes the MongoDB sharding infrastructure:

- Initializes Config Server replica set
- Initializes Shard replica sets (shard1, shard2)
- Adds shards to the mongos router
- Enables sharding on the `amap` database

### `add-producer-shard.sh`

Configures sharding for a specific producer (parameterizable):

```bash
./add-producer-shard.sh <producer_id> <shard_name>
```

- Creates indexes on `producer_id` field
- Enables sharding on `products` and `orders` collections
- Splits and moves chunks to the appropriate shard

**Example:**

```bash
./add-producer-shard.sh mushrooms shard1RS
./add-producer-shard.sh bread shard2RS
```

---

## Notes

- Authentication is basic (no password encryption) as per project requirements
- Sessions are stored in memory (lost on application restart)
- Cart data is stored in HTTP session
- Applications are configured to work both locally and in Docker (via environment variables)
- For production use, implement:
    - Password hashing (BCrypt)
    - Persistent sessions (Redis/Database)
    - HTTPS
    - Input validation
    - CSRF protection

---

## Troubleshooting

### Applications don't start

- Check logs: `docker-compose logs marketplace`
- Ensure MongoDB is fully initialized before apps start (wait 30-60 seconds)

### Can't connect to MongoDB

- Verify mongos is running: `docker ps`
- Check MongoDB logs: `docker-compose logs mongos`

### Sharding not working

- Verify sharding status: `docker exec -it mongos mongosh --port 27020 --eval "sh.status()"`
- Re-run initialization scripts

### Port already in use

- Stop other applications using ports 8080, 8081, 8082
- Or modify ports in `docker-compose.yml`

---

## License

Educational project for Database course - INSA