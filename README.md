# AMAP Marketplace Project

**Team Members:**

- [Ton Nom]
- [Nom du binôme]

---

## Description

Marketplace pour AMAP avec MongoDB sharding. Chaque producteur a son propre shard pour optimiser les performances.

---

## Prérequis

- Docker & Docker Compose
- Git Bash (Windows)

---

## Lancement du projet

### 1. Démarrer les conteneurs

```bash
cd mongodb
docker-compose up -d
```

Attendre 30 secondes que tout démarre.

### 2. Initialiser le système de sharding

```bash
./init-sharding.sh
```

Ce script fait les **étapes 2 et 5** du TP :

- Initialise le Config Server
- Active le sharding sur la base `amap`
- Crée les index sur `producer_id`
- Configure les collections `products` et `orders`

### 3. Ajouter les producteurs

```bash
./add-producer.sh mushrooms shard1RS
./add-producer.sh bread shard2RS
```

Ce script fait les **étapes 3, 4, 6 et 7** du TP pour chaque producteur :

- Initialise le replica set du shard
- Ajoute le shard au mongos
- Découpe les chunks par `producer_id`
- Déplace les chunks sur le bon shard

### 4. Accéder aux applications

- **Marketplace**: http://localhost:8080
- **Producer Mushrooms**: http://localhost:8081
- **Producer Bread**: http://localhost:8082

---

## Test du système

### Côté Producteur

1. Aller sur http://localhost:8081
2. Cliquer "Add Product"
3. Ajouter un produit (ex: Shiitake, 8.50€, stock: 20)

### Côté Marketplace

1. Aller sur http://localhost:8080
2. S'inscrire (Register)
3. Ajouter des produits au panier
4. Valider la commande (Checkout)
5. Voir l'historique des commandes (My Orders)

### Vérification des commandes

1. Retourner sur http://localhost:8081
2. Cliquer "View Orders"
3. Voir la commande reçue
4. Accepter ou rejeter la commande

---

## Ajouter un nouveau producteur

### Exemple : Producteur de fromage

#### 1. Modifier docker-compose.yml

Ajouter dans `services:` :

```yaml
  # Shard 3
  shard3:
    image: mongo:7.0
    container_name: shard3
    command: mongod --shardsvr --replSet shard3RS --port 27016 --dbpath /data/db
    ports:
      - "27016:27016"
    volumes:
      - shard3-data:/data/db
    networks:
      - mongo-network

  # Producer Cheese
  producer-cheese:
    build:
      context: ../producer
      dockerfile: Dockerfile
    container_name: producer-cheese
    ports:
      - "8083:8083"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongos:27020/amap
      - SERVER_PORT=8083
      - PRODUCER_ID=cheese
      - PRODUCER_NAME=Cheese Maker
    depends_on:
      - mongos
    networks:
      - mongo-network
```

Ajouter `shard3` dans `mongos` depends_on :

```yaml
  mongos:
    depends_on:
      - configsvr
      - shard1
      - shard2
      - shard3
```

Ajouter dans `volumes:` :

```yaml
  shard3-data:
```

#### 2. Lancer le nouveau shard

```bash
docker-compose up -d shard3
sleep 10
```

#### 3. Configurer le producteur

```bash
./add-producer.sh cheese shard3RS
```

#### 4. Lancer l'application

```bash
docker-compose up -d producer-cheese
```

Accès : http://localhost:8083

---

## Nettoyer l'environnement

```bash
docker-compose down
docker volume rm mongodb_config-data mongodb_shard1-data mongodb_shard2-data
```

---

## Vérifier le sharding

```bash
docker exec -it mongos mongosh --port 27020 --eval "sh.status()"
```

---

## Architecture

```
amap-project/
├── producer/           # Application producteur (unifiée)
├── marketplace/        # Application marketplace
├── mongodb/
│   ├── docker-compose.yml
│   ├── init-sharding.sh      # Script 1 (étapes 2 et 5)
│   └── add-producer.sh       # Script 2 (étapes 3,4,6,7)
└── README.md
```

---

## Technologies

- Spring Boot 4.0
- MongoDB 7.0 (sharding)
- Docker & Docker Compose
- Thymeleaf + Tailwind CSS