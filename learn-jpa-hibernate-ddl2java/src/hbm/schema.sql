-- https://stackoverflow.com/questions/12919540/hibernate-tools-4-0-0-detect-many-to-many-tables-not-working-with-mysql-db

-- Schema and Tables need to match hibernate.reveng.xml

create schema codegen;

create table codegen.author (
    id bigint not null,
    name varchar(255),
    primary key (id)
);

create table codegen.book (
    isbn_number varchar(255) not null,
    name varchar(255),
    primary key (isbn_number)
);

create table codegen.book_author (
    isbn_number varchar(255) not null,
    author_id bigint not null,
    primary key (isbn_number, author_id),
    constraint FK_book_author1 foreign key (isbn_number) references codegen.book (isbn_number),
    constraint FK_book_author2 foreign key (author_id) references codegen.author (id)
);

create table codegen.preference (
    author_id bigint not null,
    name varchar(255),
    pref_value varchar(255),
    primary key (author_id, name),
    constraint FK_preference_author foreign key (author_id) references codegen.author (id)
);