    
drop table course if exists;

drop table student if exists;

create table course (
    id bigint generated by default as identity,
    name VARCHAR(50) not null,
    primary key (id)
);

alter table course 
   add constraint UK_COURSE_1 unique (name);

create table student (
    id bigint generated by default as identity,
    name VARCHAR(100) not null,
    primary key (id)
);


create table student_course (
    student_id bigint not null,
    course_id bigint not null,
    primary key (student_id, course_id)
);

alter table student_course 
   add constraint FK_STUDENT_SOURCE_1
   foreign key (course_id) 
   references course;

alter table student_course 
   add constraint FK_STUDENT_COURSE_2
   foreign key (student_id) 
   references student;