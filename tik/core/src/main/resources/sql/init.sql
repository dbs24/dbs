//====================================================
jdbc:postgresql://dev01.k11dev.tech:5432/dev_tik_core_db?user=dev_tik_core_admin&password=c79e6be027844fb388810512b
r2dbc:postgresql://dev01.k11dev.tech:5432/dev_tik_core_db
====================================================
CREATE SCHEMA dev_tik_core_schema;
ALTER DATABASE dev_tik_core_db SET search_path TO dev_tik_core_schema;
SET search_path TO dev_tik_core_schema;
//====================================================
