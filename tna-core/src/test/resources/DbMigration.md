### Running Uint and Integration Test without pysical database (i.e InMemory H2 database)
1. Copy content of `application-h2.yml` to `application-test.yml`. Here H2 related configurations are specified
2. Data Initialization:

    a. Master database: Schema is initialized using Hibernate auto schema creation feature 
       and data is initialized from data-h2.sql file.
       
    b. Tenant database: Liquibase is used to initialize schema and data. liquibase entry file is 
        `changelog-tenant-h2.xml` which contains change sets of both schema and data.
        test schema change set and  main schema change set are same except for createIndex change set
        
        
### Running Uint and Integration Test with pysical database (i.e Postgre database)
1. Copy content of `application-h2.yml` to `application-db.yml`. Here Postgre related configurations are specified
2. Data Initialization:

    a. Master database: Liquibase is used to initialize schema. 
        liquibase change set file located in framework (parent project: jazasoft-mtdb)
    
    b. Tenant database: Liquibase is used to initialize schema and data. liquibase entry file is 
        `changelog-tenant-db.xml` which contains change sets of data only. 
        Schema is initialized using main schema change set. Therefore main app must be run at 
        least once before running unit test.
        
        
