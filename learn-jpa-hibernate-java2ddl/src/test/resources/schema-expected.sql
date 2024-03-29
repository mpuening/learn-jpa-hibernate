
    drop table if exists AUTHOR cascade ;

    drop table if exists BOOK cascade ;

    drop table if exists BOOK_AUTHOR cascade ;

    drop table if exists PREFERENCE cascade ;

    create table AUTHOR (
        ID bigint generated by default as identity,
        NAME varchar(255),
        primary key (ID)
    );

    create table BOOK (
        ISBN_NUMBER varchar(255) not null,
        NAME varchar(255),
        primary key (ISBN_NUMBER)
    );

    create table BOOK_AUTHOR (
        ISBN_NUMBER varchar(255) not null,
        AUTHOR_ID bigint not null,
        primary key (ISBN_NUMBER, AUTHOR_ID)
    );

    create table PREFERENCE (
        AUTHOR_ID bigint not null,
        NAME varchar(255) not null,
        PREF_VALUE varchar(255),
        primary key (AUTHOR_ID, NAME)
    );

    alter table if exists BOOK_AUTHOR 
       add constraint FK2saicemm9a0wsvdycwmtjs20s 
       foreign key (AUTHOR_ID) 
       references AUTHOR;

    alter table if exists BOOK_AUTHOR 
       add constraint FK3hap5lwqqoa1h26ay67mgl16n 
       foreign key (ISBN_NUMBER) 
       references BOOK;

    alter table if exists PREFERENCE 
       add constraint FKcjfcgv2ythu2axq9s9o07du2r 
       foreign key (AUTHOR_ID) 
       references AUTHOR;
