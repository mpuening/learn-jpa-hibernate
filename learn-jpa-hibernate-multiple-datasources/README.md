Learn JPA Hibernate
===================

## TL;DR Multiple Data Sources HOW-TO

To support multiple data sources and have the same Spring Boot features as having
a single data source (e.g. loading SQL files, etc), do the following:

1. Disable both Data Source and its Initialization and JPA Auto-configuration in your `application.yml` file exclude property
2. Include both `AbstractDataSource` and `AbstractJpaConfiguration` classes in your 
   project
3. Create a `DataSourceConfiguration` and `JpaConfiguration` for each data source you have
4. Choose your transaction model: JTA or not. This project uses JTA, and `spring-boot-starter-jta-atomikos`
   is included in the project; should you not need JTA transactions, include `TransactionManagerConfiguation` in 
   your project and follow the instructions in that class
5. Make sure you separate your domain classes and repositories so that each configuration 
   isolates what classes they control (see the package name references in the configuration
   classes)
6. Place your data source and JPA configuration in `application.yml` under different 
   prefixes (e.g. `application.primary` and `application.secondary`)
7. Create multiple `schema.sql` and `data.sql` files to initialize an in-memory database
8. Verify via a test case that the configuration works
