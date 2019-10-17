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
example in this project has a `many to many` relationship between `Book` and `Author`. 
While `H2` can detect the relationship, `Derby` does not. This is why I say your 
mileage may vary. Perhaps you can play around with the schema or the database type to 
move passed the issue. But don't take too long. It isn't terribly difficult to write 
Java code with JPA relationships. There is a reason why the DDL to Java Code conversion
library isn't maintained very well.

## `learn-jpa-hibernate-equals-and-hashcode`

There is an enormous amount of discussion on the internet about whether or not you need 
to implement `equals()` and `hashCode()` on your entity beans. Below are just a few links:

* `https://vladmihalcea.com/the-best-way-to-implement-equals-hashcode-and-tostring-with-jpa-and-hibernate/`
* `https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/`
* `https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/`
* `https://stackoverflow.com/questions/5031614/the-jpa-hashcode-equals-dilemma`

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
When using Lombok in your project, have the entity beans just use `@Getter` and `@Setter`
and let `AbstractPersisable` or `AbstractEntity` provide the `equals()`, `hashCode()`
and `toString()` implementations.


## `learn-jpa-hibernate-relationships`

This project contains a *contrived* data model that demonstrates each of the relationship 
types (e.g. one to many uni-directional and bi-directional). Spring Boot is used to 
bootstrap the app, and expose a REST API. The data model is inspired from the O'Reilly's
Enterprise JavaBeans book (5th Edition) and is a Star Trek themed reservation system. Please
do not evaluate this model as being the best ever, because it exists to show how to implement 
each of the JPA relationship types and play with different configuration settings as opposed
to being the best data model. Some parts  of model may seem a bit awkward, but at least there
is a working relationship type to test.

Consider the following when building your own model:

####  Multiplicity

Multiplicity describes how many entities appear in a relationship. In JPA, the multiplicity 
is either *one* or *many*. 

#### Direction

The direction describes how one can navigate a relationship. In JPA, there are uni-directional
and bi-directional relationship.

#### Aggregation versus Composition

Aggregation describes a relationship where the child entity may exist independently of the parent 
entity. For example, a university has both students and courses, and they are related 
when a student enrolls in a course. Deleting a student does not mean the  course is deleted, 
it just means the student dropped out. Likewise, deleting a course does not mean the 
students get deleted. It just means the course isn't offered anymore.

Composition describes a relationship where the child entity cannot exist independently of the parent 
entity. For example, an apartment building consists on apartments. If one were to delete 
the building, then the apartments would not exist either.

In JPA, relationships can have an `orphanRemoval` setting that can be used to determine 
what might happen to orphans. The flag should be used judiciously.

#### Relationship Fetch Type

Below are the JPA defaults for FetchType:

* `OneToMany` and `ManyToMany` relationships are `FetchType.LAZY` relationships by default.
* `OneToOne` and `ManyToOne` relationships are `FetchType.EAGER` relationships by default.

Changing the default settings change what queries are done. For example, a one to one 
relationship will typically generate inner joins or left outer joins to pull in extra 
data from child entities in order to minimize the number of queries. But this costs more
data to queried than might be necessary for a use case. If one we change a relationship 
to lazy, less data is queried initially, but additional queries may be executed to 
traverse the relationship which might impact performance in another way, for example 
the N+1 query problem. FetchType is a static configuration that one must choose wisely.
Dynamic configuration is preferred  which is why one should know and understand entity
graphs and projections.

#### Land Mines and Pitfalls

Consider the following:

* Prefer `Set` rather than `List` for relationships.
* Avoid `OneToMany` *uni-directional* relationships. Set up bi-directional or element collection 
  relationships instead.
* Avoid `CascadeType.REMOVE` (and `CascadeType.ALL`) settings on `OnetoMany` and `ManytoMany `
  relationships. Implement a bulk delete on child entity table.
* Learn to recognize the N+1 problem

#### Example Data Model

Below is the class diagram our model. The arrows between classes are labeled as being 
*many* or *one* and show whether they are *uni-directional* or *bi-directional*.

![alt text](docs/images/model.png "Class Diagram")

Below are sections to help you identify where in the model an example is of each of the 
seven JPA relationship types:

#### 1) `One-to-One Uni-directional` Relationship

`Person` to `Address` represents a one to one relationship. This means a person has
one and only one address and an address has only one person. Furthermore, the relationship
can only be navigated from the person side. The code below also indicated that the address
is required and all operations are cascaded (such as delete).

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "PERSON")
public class Person {
    // Join column exists in this entity
    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ADDRESS_ID", nullable = false)
    private Address address;
}

@Entity
@Table(name = "ADDRESS")
public class Address {
    // No code required
}
```

#### 2) `One-to-One Bi-directional` Relationship

`Ship` to `Customer` represents a one to one bidirectional relationship. This relationship
is similar in regards to the multiplicity of the previous relationship, but expands on it by 
allowing navigation from both sides.

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "PERSON")
public class Person {
    // Bi-directional relationship has 'mappedBy' attribute in other entity
    @OneToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "SHIP_ID", nullable = true)
    private Ship ship;
}

@Entity
@Table(name = "SHIP")
public class Ship {
    @OneToOne(mappedBy = "ship", optional = false)
    private Person person;
}
```

#### 3) `One-to-Many Uni-directional` Relationship

```
Warning!!!
```
One to many uni-directional relationship should be *avoided*. More information can 
be found here:

`https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/`

`Customer` to `HailingFrequency` can be considered to be a one to many relationship. 
But because of the problems that Hibernate has with dealing with this relationship, it does *NOT* appear
in the code of this project. It has been switched to a bidirectional relationship with 
the child `HailingFrequency` entity not having a generated Id. It also has a nice example 
with using a `@PrePersist` method to copy the the generated Id into the composite key.
```
Warning!!!
```
Even though the relationship was improved upon, it does have an N+1 issue on the cascade 
delete operation. Make sure to implement a bulk delete of the child entities when implementing
such a relationship.

Despite the discussion above, here is some sample code of such a relationship
(with unrelated properties removed for clarity):

```
@Entity
@Table(name = "PERSON")
public class Person {
    // Join column exists in the other entity (or join table)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "ID")
    private Set<HailingFrequency> hailingFrequencies;
}

@Entity
@Table(name = "HAILING_FREQUENCY")
public class HailingFrequency {
    // No code required
}
```

#### 4) `Many-to-One Uni-directional` Relationship

`Address` to `Planet` represents a many to one relationship. This means that there are many 
addresses for each planet, however navigation is only from the address side.

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "ADDRESS")
public class Address {
    // Join column exists in this entity
    @ManyToOne(optional = false)
    @JoinColumn(name = "PLANET_ID", nullable = false)
    private Planet planet;
}

@Entity
@Table(name = "PLANET")
public class Planet {
    // No code required
}
```

#### 5) `One-to-Many and Many-to-One Bi-directional` Relationship

`Voyage` to `Reservation` represents both the one to many and many to one relationship, 
because both relationship are the same, just from opposite perspectives.

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "RESERVATION")
public class Reservation {
    // Bi-directional relationship has 'mappedBy' attribute in other entity 
    @ManyToOne
    @JoinColumn(name = "VOYAGE_ID", nullable = false)
    private Voyage voyage;
}

@Entity
@Table(name = "VOYAGE")
public class Voyage {
    @OneToMany(mappedBy="voyage")
    private Set<Reservation> reservations;
}
```

#### 6) `Many-to-Many Uni-directional` Relationship

`Reservation` to `Cabin` represents a many to many relationship. The many to many relationship 
requires a join table. Being unidirectional means that the relationship is only navigated 
from one side. In other words, a cabin cannot navigate to its reservation.

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "RESERVATION")
public class Reservation {
    // This relationship requires a join table
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "RESERVATION_CABIN", joinColumns = @JoinColumn(name = "RESERVATION_ID"), inverseJoinColumns = @JoinColumn(name = "CABIN_ID"))
    private Set<Cabin> cabins;
}

@Entity
@Table(name = "CABIN")
public class Cabin {
    // No code required
}
```

#### 7) `Many-to-Many Bi-directional` Relationship

`Reservation` to `Person` represents a many to many bidirectional relationship. This relationship
is similar in regards to the multiplicity of the previous relationship, but expands on it by 
allowing navigation from both sides.

Code (with unrelated properties removed for clarity):

```java
@Entity
@Table(name = "RESERVATION")
public class Reservation {
    // Bi-directional relationship has 'mappedBy' attribute in other entity
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "RESERVATION_PERSON", joinColumns = @JoinColumn(name = "RESERVATION_ID"), inverseJoinColumns = @JoinColumn(name = "PERSON_ID"))
    private Set<Person> passengers;
}

@Entity
@Table(name = "PERSON")
public class Person {
    @ManyToMany(mappedBy = "passengers")
    private Set<Reservation> reservations;
}
```

#### Other Interesting Features

* `@DiscriminatorColumn` Column

    The `PERSON` table contains a discriminator column for both passengers and captains of ships.

* `@PrePersist`

    An example of using `@PrePersist` is in the `HailingFrequency` entity class. The method
    is responsible for making sure the `Id` key property is properly set from the foreign
    key relationship.

* Currency (`MonetaryAmount`)

    Hibernate has features that have not been exposed through the JPA API.One of those
    features is `Type` mapping. On the `Cabin` entity class is a property called `price`.
    Even though it is implemented as one property, it is stored as two columns in the database.
    One column for the monetary amount and another for cerrency type. It even stores the numeric
    value in the minor amount (e.g. pennies) so that the database doesn't lose fractions of pennies.
    (Remember Superman III?). You can find the logic to support the type in `MonetaryAmountUserType`.

    In addition, the `MonetaryAmount` does not render to JSON very well. This is easily fixed with
    Jackson (de)serializers. You can how money values are rendered to and from JSON in the
    `MonetaryAmountSerializer` and `MonetaryAmountDeserializer` classes. They are engaged by
    annotating a property (e.g. `price`) with the `MonetaryAmountJsonSerialize` annotation.

* `DatasourceProxyBeanPostProcessor`

    The test cases in this project assert the database statements for various situations. Being
    able to assert what statements are executed is key to making sure your model does what it
    is designed to do. It as allows you to find where the model can be improved where otherwise
    it wouldn't be as obvious. Here are as example of what is possible with the proxy:

```java
Assert.assertThat(proxyDataSource, executionCount(10));
Assert.assertThat(proxyDataSource, insertCount(4));
Assert.assertThat(proxyDataSource, selectCount(1));
Assert.assertThat(proxyDataSource, updateCount(1));
Assert.assertThat(proxyDataSource, deleteCount(4));
```

* HAL Browser

    This project includes the HAL Browser to navigate the Hateoas API. I didn't spent too
    much testing the UI. though. Springfox 2.9.2 does not support Spring Boot 2.1 Hateoas,
    and Springfox 3.0 is not released at the time this is being written.

    There is a test case `ShipRestRepositoryTests` that invokes the API to add a `Cabin` to a `Ship`.

    TODO: Add validation annotations to the entity beans.

* Hibernate Statistics

    The Spring Actuator can expose Hibernate statistics.

    TODO: Add information to fetch stats.

## `learn-jpa-hibernate-entity-graphs`

#### How to Recognize and Resolve the N+1 Problem

#### How to Better Query Data (Criteria API)

Want to know how to recognize the N+1 problem? You just need to be able to easily count 
how many queries take place for a given use case and have an expectation for how many
should take place. For example, if you expect one query to take place and find that
thousands are actually taking place, then you should probably investigate your design
to see if are suffering from the N+1 problem. 

This project contains a *contrived* model to accentuate the N+1 problem. The model
consists of students, teachers and courses. Obviously, students enroll in courses and 
the courses are taught by teachers. To really cause an N+1 mess, the relationship between 
the entities are all `LAZY`.

The use case is to create a report (CSV file) of all the students and their courses 
along with which teacher is teaching the course. Just ignore that fact that we are producing 
a report of the entire database. But we will produce the report in a variety of ways 
to investigate how many queries get executed:

* The N+1 Problematic Way
* Custom Queries
* Entity Graphs

A test case will show how many queries get executed. The take away from this exercise 
is that one should be skilled in asserting how many queries get executed for each test 
case, and then knowing the tools to resolve problems.

Knowing that you have options on how data is retrieved, you can resolve and prevent 
the N+1 problem in your application.

More information can be found here:

https://vladmihalcea.com/how-to-detect-the-n-plus-one-query-problem-during-testing/

#### How to Better Control Data with Projections

Another way the N+1 problem manifests itself is by relationships being traversed when
serializing an entity into JSON. In order to not traverse unintended relationships,
one can define projections. A projection is implemented via an interface that specifies
exactly what data should be returned to a client.

This project defines several projections and compares the data returned to a client 
to not using a projection.

## `learn-jpa-hibernate-multiple-datasources`

Having multiple data sources also means having multiple entity manager factories and
deciding if one should have multiple transaction managers or a single one with XA support.

This project shows how to set up multiple data sources, one H2 database, one Derby database 
with a JTA transaction manager using Bitronix.

There are abstract classes, `AbstractDataSourceConfiguration` and `AbstractJpaConfiguration`
with re-usable code that one can use to create data sources and entity manager factories.

The `TransactionManagerConfiguration` class contains instructions to change from the Bitronix
transaction manager to dual JPA data source transaction managers.

## `learn-jpa-hibernate-extended-repository`

Spring's ability to dynamically implement `@Repository` interfaces is super powerful. 
But sometimes there is a requirement where the out of box features do not support what 
is needed. It is nice to extend what Spring implements. And that is what this
project shows. The key to enabling a new base class for the interface is the 
`@EnableJpaRepositories` annotation. In the extension, one can implement new features, or
change and/or override what Spring provides.

This project implements a new method to query by a property passed into the repository
and perform a `like` query against of value.

## `learn-jpa-hibernate-mapped-super-class`

Mapped super classes provide a convenient location for modeling common
columns/properties across two or more similar entity beans.

In this silly example, a star schema is used as the model. But as I have
seen many times, developers who want to use JPA where JPA might not be
appropriate, have a database view created to make using JPA easier. So
included in this model is a view that brings together the dimension and
fact tables so that the developer doesn't need to model any dimension tables.
Since views cannot be updated, an updatable version of the sales entity bean
is created. The two versions of the sales entity share a mapped super class.

## `learn-jpa-hibernate-bulk-operations`

Bulk operations are those that are performed by the database and not in the application.
At the time of this writing, JPA supports only delete and update statements. JPA does not
support insert statements except for native queries.

This project includes an easy example of performing both the update and delete statements,
but my challenge was to support the insert from select statement where the select statement
is a JPQL statement. The `InsertFromSelect` class contains a method that accepts a 
specification to produce a query and column mappings to produce the insert statement.
The class uses Hibernate internals to produce the native query statement that is still
required to execute the query.

## `learn-jpa-hibernate-batch-operations`

JPA batch operations align with JDBC batch operations and try to optimize a long series
of SQL statements that need to be executed by the application.

I have another Github project called `learn-apache-poi-xssf` that implements JPA
batch statements. Feel free to review that project as an example for JPA batch operations.

## `learn-jpa-hibernate-active-record-aspects`
## `learn-jpa-hibernate-active-record-example`

Martin Fowler defines an active record as "an object that wraps a row in a database table or view,
encapsulates the database access, and adds domain logic on that data." For Spring Data 
JPA, the key aspect here is that the database access is encapsulated in the entity bean, 
and not using a repository bean.

Some people call the *active record pattern* an *anti-pattern*. That won't be debated 
here. These projects, inspired from Spring Roo, merely serve as a curiosity for implementing
the active record in Java using AspectJ.

Consider these two code examples for getting and saving a `Person`, first as an active record,

```java
Person person = Person.find(Person.class, Long.valueOf(1L));

Person newPerson = new Person("Name");
newPerson.persist();
```

and then using Spring repositories:

```java
Person person = personRepository.find(Person.class, Long.valueOf(1L));

Person newPerson = new Person("Name");
personRepository.save(newPerson);
```

The big difference here is the lack of using Spring repositories. It is not saving 
that much code, and you lose the ability to mock the repositories. In addition, the
IDE tooling takes some time to get right, so take that into consideration when deciding
to use the active record on your applications. If you ask me, I won't be using it.

Tooling wise, for Eclipse, install the AspectJ Development Tools.

```
Warning!!!
```
`Lombok` is not compatible with `AspectJ` at the time of this writing.

## `learn-jpa-hibernate-swap-in-openjpa`
## `learn-jpa-hibernate-swap-in-eclipselink`

Many times when people say they are using JPA, they are using Hibernate. Even Spring's 
own `spring-boot-starter-data-jpa` includes Hibernate. But people need to know that 
when Spring Data JPA is mentioned, there is the JPA Specification, of which Hibernate 
is but one implementation, and Spring is there to make using them so much easier.

But in order for one to say they really know them, one needs to know where the dividing lines
are. And one way to investigate that is by playing with other implementations of JPA. 
Two other JPA implementations besides Hibernate are OpenJPA and EclipseLink.

There are two sub-projects in this project that are similar in nature. Each swaps out the
default Hibernate implementation in favor of OpenJPA and EclipseLink respectively.

Notable differences between these and the Hibernate version is the required configuration
class (`OpenJpaConfiguration`, `EclipseLinkConfiguration`) to implement a `TransactionManager`
and `EntityManagerFactory`. They each have their own properties which one can see in 
the `application.yml` file.

Playing around with the different implementations, one notices small differences. For 
example, Hibernate has no problem understanding this query: `From Course`. However, 
EclipseLink wants the query to have an alias like so: `From Course c`. Finally OpenJPA 
wants a statement like this: `Select c from Course c`. 

Hibernate still remains my favorite implementation. OpenJPA is my least favorite because
of the weaving step. (I haven't found a dynamic configuration that works for me.)

## For more information

Here are some links to other great information:

* https://github.com/AnghelLeonard/Hibernate-SpringBoot
* https://dzone.com/articles/50-best-performance-practices-for-hibernate-5-amp

## TODO

Code review.. too much repeating code, remove updatable = true
Identity columns... start at 100?
README.md files in sub modules. Just show me the code links...
java2ddl examples for eclipselink and openjpa (https://www.eclipse.org/eclipselink/documentation/2.5/jpa/extensions/p_ddl_generation.htm)
excessive config present? Like hibernate metrics: false?
