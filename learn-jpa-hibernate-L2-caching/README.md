Learn JPA Hibernate
===================

## TL;DR Level 2 Caching HOW-TO

To enable Hibernate Level 2 caching:

1. Include `hibernate-ehcache` in your project
2. Create an `ehcache.xml` file in `src/main/resources` that defines each type of cache 
   that you want; you can define both entity and relationship caches
3. Update `spring.jpa.properties.hibernate` in `application.yml` to enable second level 
   caching with ehcache
4. Annotate each cacheable entity bean with `@Cacheable` and`@org.hibernate.annotations.Cache` 
   as shown in the `Course` entity bean
5. Annotate each cacheable relationship with `@org.hibernate.annotations.Cache` again 
   as shown in `Course::students`
6. Queries are cached by annotating finder methods in repository beans with `@QueryHints` 
   as shown in `TeacherRepository`
7. Verify via a test case that the configuration works
