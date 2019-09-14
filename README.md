Learn JPA Hibernate
===================

When Hibernate first came out, I heard people say that it is the future and *all* your 
code should use Object Relational Mapping (ORM). Every now and then today, I hear someone 
say that JPA/Hibernate sucks and should *never* be used. The truth is that JPA/Hibernate
is a  nice tool that can be useful in some circumstances and be absolutely dreadful in
others. Developers just need to recognize when and where it can be appropriate and useful.

In order to make an intelligent decision about using JPA though, you need to know to 
know the framework. What is sad though is that I meet too many developers that do not
know it very well, or still think JPA has not progressed beyond version 1.0. So if you
do not know JPA very well, and need to get an application written quickly, do not use 
it... use JDBC instead.

But if you have time to learn and know JPA, and find you have a data model that dove 
tails nicely with an object model, JPA can help you write an application faster with 
less code.

So what is in this project?
===========================

This project contains several modules that demonstrates different aspects of JPA.

## `learn-jpa-hibernate-java2ddl`

Because JPA implements ORM, there *theoretically* exists a way to translate your Java
Entity Beans to DDL, and vice versa, create Java Entity Beans from DDL.

One use case for JPA is quickly implementing an application without regards to how the
database schema looks. But should the application shows value, you may want to look 
at the schema. This can be for several reasons above and beyond the curiosity of reviewing
the schema. For example, your company may have a policy that only DBA's can implement
schema changes. Another example is that you may want to track schema changes via Flyway
or Liquibase. Being able to get a DDL file from your object model is an essential skill
and the `learn-jpa-hibernate-java2ddl` project shows how that is done.

The project contains a simple object model of three entity beans. There is a Spring 
Boot test case that demonstrates that the model works. The project also contains a `DDLExporter` 
class that can be used to export a DDL file from an object model. There is also a test 
case to show that it works.

If you want DDL from your Java Entity Beans, just use the DDLExporter. The code is not 
terribly complex. The code is based on Spring to boot up a `SessionFactory` from which 
a metadata source model is obtained. Once you have the metadata, the `SchemaExport` 
class is used to generate the DDL file.

## `learn-jpa-hibernate-ddl2java`

Going in the opposite direction from starting with Java code is starting with an existing
database schema, or at least "schema first" design.

With Hibernate, this direction is not well maintained, and your mileage will vary depending 
on how your schema is designed. Not all the relationships are detected or even implemented 
and the code that is generated is not formatted very well or even look modern. But, 
you are not paying for this tool, and it can get you a decent percentage down the road 
towards code complete. Also, I don't recommend using this tool to produce `generated` code;
meaning using the code as is, and never modifying it. No, take the code that is produced 
and clean it up and make it your own, and never think about using the tool to create 
subsequent versions.

Here are the steps to create code for yourself:

* Identify where your schema is. This project as is, uses an in-memory `H2` database with 
  the schema located in `src/hbm/schema.sql` file. If you have the schema as a file, and `H2` 
  will suffice as a database to read the schema file, then copy in your schema into 
  that file. If you have an existing database, then ignore `schema.sql` file and instead 
  update the `src/hbm/hibernate.properties` file with your connection information. Also, 
  update `pom.xml` with a database driver that can connect to your database. Also, of 
  big importance, note there is an `ant plugin` step to create a database from the `schema.sql` 
  file. If you have an existing database and are not creating one, then comment out 
  or delete the `sql` task in `pom.xml`.
* Update `src/hbm/hibernate.reveng.xml` to reference the schema and tables that you 
  want to reverse engineer (generate code). Also, checkout the documentation for the 
  file, as it might help you generate code more correctly should the out of the box 
  settings not suffice.
* Update the `packagename` attribute on the `jdbcconfiguration` element to your desired 
  Java package name.
* Update the `JavaCodeGeneratedTest` to test for the classes that you expect to generate. 
  If you don't want to update the test case, make sure to run maven with the `-DskipTests=true`
  flag.
* To generate your code, run `mvn clean package`. When complete (and the test cases can 
  see your files), you will find your Java code in the `target/codegen` folder.

Review the code that gets generated and see if there are improvements to be made. Check 
that all the relationships are identified and implemented properly. If they aren't, you might 
consider checking if another database can be used to generate code from. For example, the 
example in this project has a `many-to-many` relationship between `Book` and `Author`. 
While `H2` can detect the relationship, `Derby` does not. This is why I say your 
mileage may vary. Perhaps you can play around with the schema or the database type to 
move passed the issue. But don't take too long. It isn't terribly difficult to write 
Java code with JPA relationships. There is a reason why the DDL to Java Code conversion
library isn't maintained very well.

## `learn-jpa-hibernate-equals-and-hashcode`

There is an enormous amount of discussion on the internet about whether or not you need 
to implement `equals()` and `hashCode()` on your entity beans. Below are just a few links:

* https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/
* https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
* https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/
* https://stackoverflow.com/questions/5031614/the-jpa-hashcode-equals-dilemma

I have seen many simple projects get away without implementing them, and they satisfied 
the requirements. But I have also seen projects with related entity beans that require having 
proper implementations for updates to work.

Fortunately implementing the code is easy (as is seen in the above links). The Spring 
Data project even provides an `AbstractPersistable` class from which to extend from and
it implements the code you need. However, that `AbstractPersistable` takes a strong
opinion on the `Id` column. Spring's take on it may not be what you want, so that is 
why this project includes a similar `AbstractEntity` class. It has the equivalent
implementations of `equals()` and `hashCode()` but leaves the definition of the `Id` 
to the class that extends from it.

The examples in this project all use that `AbstractEntity` class (except for the bad 
test cases).

This project also includes an `EntityValidator` utility class that can be used to ensure
that your implementations of `equals()` and `hashCode()` methods work properly for entity
beans.

#### Lombok

It should be pointed out that Lombok and its convenient `@Data` annotation can cause 
problems for entity beans. The `@Data` annotation provides not only *getter* and *setter*
methods, but also implementations of `equals()`, `hashCode()` and `toString()`. These
implementations are wrong for entity beans as the EntityValidator can show.
When using Lombok in your project, have the entity beans just use @Getter and @Setter
and let `AbstractPersisable` or `AbstractEntity` provide the `equals()`, `hashCode()`
and `toString()` implementations.


## `learn-jpa-hibernate-relationships`

`Coming soon...`

This project contains a *contrived* data model that demonstrates each of the relationship 
types (e.g. one-to-many uni-directional and bi-directional). Spring Boot is used to 
bootstrap the app, and expose a REST API. Swagger UI is included for easy invocation 
of the APIs.

## `learn-jpa-hibernate-advanced`

`Coming soon...`

There are also examples of some more complex mappings such as mapped super classes,
embedded identifiers, and entity maps.

Advanced features of Spring are also included such as base repositories and projections.


## For more information

Here are some links to other great information:

* https://github.com/AnghelLeonard/Hibernate-SpringBoot
* https://dzone.com/articles/50-best-performance-practices-for-hibernate-5-amp
  