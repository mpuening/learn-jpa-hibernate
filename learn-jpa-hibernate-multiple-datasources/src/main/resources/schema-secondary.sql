
--drop table late_invoice;

create table late_invoice (
    id bigint generated by default as identity(start with 100),
    name VARCHAR(100) not null,
    primary key (id)
);
