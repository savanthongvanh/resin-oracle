#!/usr/bin/env bash
set -euo pipefail

required_vars=(
  ORACLE_HOST
  ORACLE_PORT
  ORACLE_SERVICE_NAME
  APP_DB_USER
  APP_DB_PASSWORD
  DB_POOL_MIN
  DB_POOL_MAX
  DB_POOL_IDLE_TIME
  DB_POOL_WAIT_TIME
)

for var_name in "${required_vars[@]}"; do
  if [[ -z "${!var_name:-}" ]]; then
    echo "Missing required environment variable: ${var_name}" >&2
    exit 1
  fi
done

cp /opt/resin/conf/resin.xml.template /opt/resin/conf/resin.xml

sed -i "s#@@ORACLE_HOST@@#${ORACLE_HOST}#g" /opt/resin/conf/resin.xml
sed -i "s#@@ORACLE_PORT@@#${ORACLE_PORT}#g" /opt/resin/conf/resin.xml
sed -i "s#@@ORACLE_SERVICE_NAME@@#${ORACLE_SERVICE_NAME}#g" /opt/resin/conf/resin.xml
sed -i "s#@@APP_DB_USER@@#${APP_DB_USER}#g" /opt/resin/conf/resin.xml
sed -i "s#@@APP_DB_PASSWORD@@#${APP_DB_PASSWORD}#g" /opt/resin/conf/resin.xml
sed -i "s#@@DB_POOL_MIN@@#${DB_POOL_MIN}#g" /opt/resin/conf/resin.xml
sed -i "s#@@DB_POOL_MAX@@#${DB_POOL_MAX}#g" /opt/resin/conf/resin.xml
sed -i "s#@@DB_POOL_IDLE_TIME@@#${DB_POOL_IDLE_TIME}#g" /opt/resin/conf/resin.xml
sed -i "s#@@DB_POOL_WAIT_TIME@@#${DB_POOL_WAIT_TIME}#g" /opt/resin/conf/resin.xml

exec /opt/resin/bin/resin.sh console
