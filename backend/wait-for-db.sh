#!/bin/sh
echo "Waiting for db at $POSTGRES_HOST:$POSTGRES_PORT..."

while ! nc -z $POSTGRES_HOST $POSTGRES_PORT; do
  sleep 1
done

echo "DB is up - launching app"
exec java -jar app.jar