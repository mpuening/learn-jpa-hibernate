Learn JPA Hibernate
===================

## TL;DR Extended Repositories HOW-TO

To support an extended repository:

1. Create a `@NoRepositoryBean` interface in your project with your desired method(s)
   similar to `ExtendedJpaRepository`
2. Implement the above interface similarly to as shown in `ExtendedJpaRepositoryImpl`
3. Use the `ExtendedRepositoryConfiguration` class to configure Spring Boot to use your
   above implementation as the base class
4. Create repository beans for your entity beans, but extend the interface from step 1
