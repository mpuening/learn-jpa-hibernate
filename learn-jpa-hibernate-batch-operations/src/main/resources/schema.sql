
drop table event if exists;

drop sequence if exists seq_event;

create sequence seq_event start with 1 increment by 50;

create table event (
    id bigint not null,
    description VARCHAR(100) not null,
    created_by VARCHAR(100),
    created_date TIMESTAMP,
    last_modified_by VARCHAR(100),
    last_modified_date TIMESTAMP,
    primary key (id)
);
