Learn JPA Hibernate
===================

## TL;DR 3rd Party Classes HOW-TO

1. Disable JPA Auto-configuration in your `Application` class
2. Use `JPAConfiguration` to configure JPA to use `persistence.xml` and make sure to 
   update the package names used in the annotations
3. You might need to specify the database type in `application.yml` (database: H2)
4. Create a version of `persistence.xml` similar to the one in `src/main/resources/META-INF`
5. Create a version of `third-party-orm.xml` similar to the one in `src/main/resources/META-INF`
6. Verify via a test case that the configuration works