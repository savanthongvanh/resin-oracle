#!/usr/bin/env bash
set -euo pipefail

sql_dir=/opt/oracle/scripts/app-init
connect_string="${APP_USER}/${APP_USER_PASSWORD}@//localhost:1521/${ORACLE_SERVICE_NAME:-FREEPDB1}"

echo "Initializing application schema for ${APP_USER} on ${ORACLE_SERVICE_NAME:-FREEPDB1}"

sqlplus -s "${connect_string}" @"${sql_dir}/001_create_schema.sql"
sqlplus -s "${connect_string}" @"${sql_dir}/002_seed_states.sql"

echo "Application schema initialization completed"

