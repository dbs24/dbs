//====================================================
jdbc:postgresql://dbs-dev.k11dev.tech:5432/dev_cm_analyst_db?user=dev_cm_analyst_admin&password=352b987034db4ecabdf478637
r2dbc:postgresql://dbs-dev.k11dev.tech:5432/dev_cm_analyst_db
//====================================================
CREATE SCHEMA dev_cm_analyst_schema;
ALTER DATABASE dev_cm_analyst_db SET search_path TO dev_cm_analyst_schema;
SET search_path TO dev_cm_analyst_schema;
//====================================================
