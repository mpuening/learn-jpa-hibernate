Learn JPA Hibernate
===================

## TL;DR Swap In EclipseLink HOW-TO

To use EclipseLink instead of Hibernate:

1. Update `pom.xml` to exclude `hibernate-core` and instead include the `eclipselink` 
   dependencies
2. Use the `EclipselinkConfiguration` class to configure EclipseLink
3. Update the `spring.jpa.properties` in `application.yml` to properly set up EclipseLink
4. Copy the `src/main/resources/META-INF/persistence.xml` file into your project
5. Verify via a test case that the configuration works

