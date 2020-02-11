-- *********************************************************************
-- SQL to roll back currently unexecuted changes
-- *********************************************************************
-- Change Log: classpath:/db/changelog-tenant.xml
-- Ran at: 11/02/2020, 12:59
-- Against: razamd@jdbc:postgresql://localhost:5432/tna_dev
-- Liquibase version: 3.6.3
-- *********************************************************************

-- Lock Database
UPDATE public.databasechangeloglock SET LOCKED = TRUE, LOCKEDBY = '192.168.0.8 (192.168.0.8)', LOCKGRANTED = '2020-02-11 12:59:14.104' WHERE ID = 1 AND LOCKED = FALSE;

-- Release Database Lock
UPDATE public.databasechangeloglock SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

