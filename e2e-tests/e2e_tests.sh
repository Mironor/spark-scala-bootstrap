#!/usr/bin/env bash
set -euo pipefail

cd ..
sbt assembly

cd e2e-tests
docker-compose down --remove-orphans -v && docker-compose up