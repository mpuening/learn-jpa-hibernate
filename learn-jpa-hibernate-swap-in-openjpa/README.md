Learn JPA Hibernate
===================

## TL;DR Swap In OpenJPA HOW-TO

To use OpenJPA instead of Hibernate:

1. Update `pom.xml` to exclude `hibernate-core` and instead include the `openjpa` 
   dependencies and `openjpa-maven-plugin` configuration
2. Use the `OpenJpaConfiguration` class to configure OpenJPA
3. Copy in the `OpenJPADialect` and `OpenJPAVendorAdapter` classes into your project
4. Update the `spring.jpa.properties` in `application.yml` to properly set up OpenJPA
5. Copy the `src/main/resources/META-INF/persistence.xml` file into your project
6. Verify via a test case that the configuration works

