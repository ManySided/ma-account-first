#!/bin/bash
psql -v ON_ERROR_STOP=1 --username postgres <<-EOSQL

  CREATE ROLE core WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  PASSWORD 'vgzzqszd';

  CREATE DATABASE coredb
    WITH OWNER = core
         ENCODING = 'UTF8'
         TABLESPACE = pg_default
         LC_COLLATE = 'en_US.utf8'
         LC_CTYPE = 'en_US.utf8'
         CONNECTION LIMIT = -1;
  GRANT CONNECT, TEMPORARY ON DATABASE coredb TO public;

  GRANT ALL ON DATABASE coredb TO core;

  create role core_r with
    nologin
    nosuperuser
    inherit
    nocreatedb
    nocreaterole
    noreplication;

  create role core_rw with
    nologin
    nosuperuser
    inherit
    nocreatedb
    nocreaterole
    noreplication;

EOSQL

psql -v ON_ERROR_STOP=1 --username core coredb <<-EOSQL

  CREATE SCHEMA spatium
      AUTHORIZATION core;

  GRANT ALL ON SCHEMA spatium TO core;

  GRANT USAGE ON SCHEMA spatium TO core_r;

  GRANT USAGE ON SCHEMA spatium TO core_rw;

  alter default privileges in schema spatium
      grant select, insert, update, delete on tables to core_rw;

  alter default privileges in schema spatium
      grant select on tables to core_r;
EOSQL

psql -v ON_ERROR_STOP=1 --username postgres coredb <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL

# keycloak-db
psql -v ON_ERROR_STOP=1 --username postgres <<-EOSQL

  CREATE ROLE keycloak WITH
    LOGIN
    NOSUPERUSER
    INHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION
    PASSWORD 'password';

  CREATE DATABASE keycloak_db
    WITH OWNER = keycloak
    ENCODING = 'UTF8'
    TABLESPACE = pg_default
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    CONNECTION LIMIT = -1;

  GRANT ALL ON DATABASE keycloak_db TO keycloak;

EOSQL

psql -v ON_ERROR_STOP=1 --username postgres keycloak_db <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
EOSQL
