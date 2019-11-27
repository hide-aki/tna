-- *********************************************************************
-- SQL to roll back currently unexecuted changes
-- *********************************************************************
-- Change Log: classpath:/db/changelog/db.changelog-tenant.xml
-- Ran at: 5/10/18 8:24 PM
-- Against: mdzahidraza@jdbc:postgresql://localhost:5432/mtdbapp_jazasoft
-- Liquibase version: 3.5.4
-- *********************************************************************

-- Lock Database
UPDATE public.databasechangeloglock SET LOCKED = TRUE, LOCKEDBY = 'fdf0:c850:c5c6:5700:8409:d13c:4e9f:1f3c (fdf0:c850:c5c6:5700:8409:d13c:4e9f:1f3c)', LOCKGRANTED = '2018-10-05 20:24:25.062' WHERE ID = 1 AND LOCKED = FALSE;

-- Release Database Lock
UPDATE public.databasechangeloglock SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

