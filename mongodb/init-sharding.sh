#!/bin/bash

echo "=== Step 2: Initializing Config Server ==="
docker exec -it configsvr mongosh --port 27019 --eval '
rs.initiate({
  _id: "configRS",
  configsvr: true,
  members: [{ _id: 0, host: "configsvr:27019" }]
})
'

echo "Waiting for Config Server..."
sleep 5

echo "=== Step 5: Enabling Sharding on Database ==="
docker exec -it mongos mongosh --port 27020 --eval '
sh.enableSharding("amap");
'

echo "Creating indexes on products collection..."
docker exec -it mongos mongosh --port 27020 --eval '
use amap;
db.products.createIndex({ "producer_id": 1 });
'

echo "Sharding products collection..."
docker exec -it mongos mongosh --port 27020 --eval '
use amap;
sh.shardCollection("amap.products", { "producer_id": 1 });
'

echo "Creating indexes on orders collection..."
docker exec -it mongos mongosh --port 27020 --eval '
use amap;
db.orders.createIndex({ "producer_id": 1 });
'

echo "Sharding orders collection..."
docker exec -it mongos mongosh --port 27020 --eval '
use amap;
sh.shardCollection("amap.orders", { "producer_id": 1 });
'

echo ""
echo "✅ Sharding system initialized!"
echo ""
echo "Now run: ./add-producer.sh <producer_id> <shard_name>"
echo "Example: ./add-producer.sh mushrooms shard1RS"