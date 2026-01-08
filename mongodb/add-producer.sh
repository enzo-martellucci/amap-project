#!/bin/bash

# Vérifier les paramètres
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: ./add-producer.sh <producer_id> <shard_name>"
    echo ""
    echo "Examples:"
    echo "  ./add-producer.sh mushrooms shard1RS"
    echo "  ./add-producer.sh bread shard2RS"
    exit 1
fi

PRODUCER_ID=$1
SHARD_NAME=$2

# Extraire le numéro de shard (shard1RS -> shard1, port 27018)
SHARD_NUM=${SHARD_NAME//[!0-9]/}
SHARD_CONTAINER="shard${SHARD_NUM}"
SHARD_PORT=$((27019 - SHARD_NUM))

echo "=== Adding producer: $PRODUCER_ID on $SHARD_NAME ==="
echo ""

echo "Step 3: Initializing Shard ($SHARD_CONTAINER)"
docker exec -it $SHARD_CONTAINER mongosh --port $SHARD_PORT --eval "
rs.initiate({
  _id: '${SHARD_NAME}',
  members: [{ _id: 0, host: '${SHARD_CONTAINER}:${SHARD_PORT}' }]
})
"

echo "Waiting for Shard..."
sleep 5

echo ""
echo "Step 4: Adding Shard to Mongos"
docker exec -it mongos mongosh --port 27020 --eval "
sh.addShard('${SHARD_NAME}/${SHARD_CONTAINER}:${SHARD_PORT}');
"

echo ""
echo "Step 6: Splitting chunks for producer: $PRODUCER_ID"
docker exec -it mongos mongosh --port 27020 --eval "
use amap;
sh.splitAt('amap.products', { 'producer_id': '${PRODUCER_ID}' });
sh.splitAt('amap.orders', { 'producer_id': '${PRODUCER_ID}' });
"

echo ""
echo "Step 7: Moving chunks to $SHARD_NAME"
docker exec -it mongos mongosh --port 27020 --eval "
use amap;
sh.moveChunk('amap.products', { 'producer_id': '${PRODUCER_ID}' }, '${SHARD_NAME}');
sh.moveChunk('amap.orders', { 'producer_id': '${PRODUCER_ID}' }, '${SHARD_NAME}');
"

echo ""
echo "✅ Producer $PRODUCER_ID configured on $SHARD_NAME"