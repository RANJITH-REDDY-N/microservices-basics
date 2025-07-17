#!/bin/bash

SERVICE=$1
PORT=$2

if [ -z "$SERVICE" ] || [ -z "$PORT" ]; then
  echo "Usage: $0 <service-folder> <port>"
  exit 1
fi

PID=$(lsof -ti tcp:$PORT)
if [ -n "$PID" ]; then
  echo "Killing process on port $PORT (PID $PID)..."
  kill -9 $PID
fi

cd $SERVICE
mvn spring-boot:run &
cd ..

echo "$SERVICE restarted on port $PORT." 