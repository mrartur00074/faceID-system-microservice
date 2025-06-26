#!/bin/sh

HOST=$1
shift

echo "Waiting for Kafka ($HOST:9092)..."

while ! nc -z $HOST 9092; do
  echo "Kafka ($HOST:9092) is unavailable - sleeping"
  sleep 2
done

echo "Kafka is up - executing command"
exec "$@"