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

## Notes

OpenJPA does not have a dedicated build for Jakarta EE 9. Instead, "Shade" is used to produce
a classified build that is compatible with the new APIs. As such, the `pom.xml` file has quirky
configuration to manage the dependencies. For more information, see this link:

```
https://rmannibucau.metawerx.net/openjpa-jakarta-tips.html
```

Also, an Apache Syncope dependency was previously used for OpenJPA vendor support. That dependency
had code that was once part of Spring (OpenJPADialect and OpenJPAVendorAdapter). Since that code
was not updated to the latest APIs, I copied that code into this project (same license) and made
the appropriate Jakarta API change myself.


