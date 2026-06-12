#!/usr/bin/env bash
set -euo pipefail

IMAGE="bug-app"
CONTAINER="bug-app"
PORT=8551

echo "==> Building image: $IMAGE"
docker build -t "$IMAGE" "$(dirname "$0")"

echo "==> Stopping existing container (if any)"
docker rm -f "$CONTAINER" 2>/dev/null || true

echo "==> Starting container: $CONTAINER"
docker run -d \
  --name "$CONTAINER" \
  --restart unless-stopped \
  --add-host "bug.hillstonenet.com:10.200.7.23" \
  -p "$PORT:$PORT" \
  "$IMAGE"

echo "==> Done. App running at http://localhost:$PORT"
