insert into book(isbn_number, name) values ('0-201-63361-2', 'Design Patterns: Elements of Reusable Object-Oriented Software');
insert into book(isbn_number, name) values ('978-0321205759', 'Contributing to Eclipse: Principles, Patterns, and Plug-Ins');

-- assuming 1 through 5 generated ids
insert into author(name) values ('Erich Gamma');
insert into author(name) values ('Richard Helm');
insert into author(name) values ('Ralph Johnson');
insert into author(name) values ('John Vlissides');
insert into author(name) values ('Kent Beck');
	
insert into book_author(isbn_number, author_id) values ('0-201-63361-2', 1);
insert into book_author(isbn_number, author_id) values ('0-201-63361-2', 2);
insert into book_author(isbn_number, author_id) values ('0-201-63361-2', 3);
insert into book_author(isbn_number, author_id) values ('0-201-63361-2', 4);
insert into book_author(isbn_number, author_id) values ('978-0321205759', 1);
insert into book_author(isbn_number, author_id) values ('978-0321205759', 5);

insert into preference(author_id, name, value) values (5, 'Payday', 'Friday');
insert into preference(author_id, name, value) values (5, 'Unit tests', 'Yes');
