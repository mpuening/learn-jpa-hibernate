Learn JPA Hibernate
===================

## TL;DR Bulk Operations HOW-TO

Bulk operations are those that insert, update, or delete multiple rows in the database. 
The operations do not interact directly with an entity bean. Example operations are shown 
in `InvoiceService` class.

Furthermore, the `insert from select` operation is not supported by JPA. However, you
can make this operation happen with Hibernate. Include the `InsertFromSelect` class 
in your project.
