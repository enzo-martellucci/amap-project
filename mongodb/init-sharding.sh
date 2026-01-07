#!/bin/bash

echo "=== Initializing Config Server ==="
docker exec -it configsvr mongosh --port 27019 --eval '
rs.initiate({
  _id: "configRS",
  configsvr: true,
  members: [{ _id: 0, host: "configsvr:27019" }]
})
'

echo "Waiting for Config Server to be ready..."
sleep 5

echo "=== Initializing Shard 1 (Mushrooms) ==="
docker exec -it shard1 mongosh --port 27018 --eval '
rs.initiate({
  _id: "shard1RS",
  members: [{ _id: 0, host: "shard1:27018" }]
})
'

echo "Waiting for Shard 1 to be ready..."
sleep 5

echo "=== Initializing Shard 2 (Bread) ==="
docker exec -it shard2 mongosh --port 27017 --eval '
rs.initiate({
  _id: "shard2RS",
  members: [{ _id: 0, host: "shard2:27017" }]
})
'

echo "Waiting for Shards to be ready..."
sleep 5

echo "=== Adding Shards to Mongos ==="
docker exec -it mongos mongosh --port 27020 --eval '
sh.addShard("shard1RS/shard1:27018");
sh.addShard("shard2RS/shard2:27017");
'

echo "=== Enabling Sharding on Database ==="
docker exec -it mongos mongosh --port 27020 --eval '
use amap;
sh.enableSharding("amap");
'

echo "=== Sharding initialization complete! ==="