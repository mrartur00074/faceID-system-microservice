#!/bin/sh
set -e

host="$1"
shift
cmd="$@"

until nc -z "$host" 9092; do
  echo "Kafka ($host:9092) is unavailable - sleeping"
  sleep 2
done

echo "Kafka ($host:9092) is up - starting app"
exec "$@"