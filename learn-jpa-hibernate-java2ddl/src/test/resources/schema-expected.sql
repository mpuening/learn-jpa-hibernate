
    drop table if exists author CASCADE ;

    drop table if exists book CASCADE ;

    drop table if exists book_author CASCADE ;

    drop table if exists preference CASCADE ;

    create table author (
       id bigint generated by default as identity,
        name varchar(255),
        primary key (id)
    );

    create table book (
       isbn_number varchar(255) not null,
        name varchar(255),
        primary key (isbn_number)
    );

    create table book_author (
       isbn_number varchar(255) not null,
        author_id bigint not null,
        primary key (isbn_number, author_id)
    );

    create table preference (
       author_id bigint not null,
        name varchar(255) not null,
        value varchar(255),
        primary key (author_id, name)
    );

    alter table book_author 
       add constraint FKbjqhp85wjv8vpr0beygh6jsgo 
       foreign key (author_id) 
       references author;

    alter table book_author 
       add constraint FKaeq6spgg3dxdqnorfkwsasics 
       foreign key (isbn_number) 
       references book;

    alter table preference 
       add constraint FKgmc9qdylvg5l0vfw94f2mn1lv 
       foreign key (author_id) 
       references author;
