Learn JPA Hibernate
===================

## TL;DR Entity Graphs HOW-TO

To configure and use an entity graph:

1. Add `@NamedEntityGraphs` to the entity bean in question
2. Reference the name of the entity graph in the `@EntityGraph` annotation on the query 
   in  the JPA repository
3. (Or create a entity graph and load in a graph and set a hint in a criteria query)
3. Verify from a test that the number of queries expected are executed

To configure and use a projection:

1. Use `ProjectionConfiguration` to configure a `ProjectionFactory`
2. Create interfaces that define what properties are in a projection
3. Use the factory to create the projection (e.g. `projectionFactory.createProjection(clazz, object)`)
4. Verify that a test case returns only the those fields from the projection
