insert into Course(id, name) values (1, 'Algebra');
insert into Course(id, name) values (2, 'Geometry');
insert into Course(id, name) values (3, 'Trigonometry');
insert into Course(id, name) values (4, 'Calculus');
insert into Course(id, name) values (5, 'Biology');
insert into Course(id, name) values (6, 'Chemistry');
insert into Course(id, name) values (7, 'Physics');
insert into Course(id, name) values (8, 'English');
insert into Course(id, name) values (9, 'Spanish');
insert into Course(id, name) values (10, 'German');
insert into Course(id, name) values (11, 'Art');
insert into Course(id, name) values (12, 'Theater');
insert into Course(id, name) values (13, 'World History');
insert into Course(id, name) values (14, 'US History');
insert into Course(id, name) values (15, 'Government');

insert into Student(id, name) values (1, 'Alice');
insert into Student(id, name) values (2, 'Beth');
insert into Student(id, name) values (3, 'Charlie');
insert into Student(id, name) values (4, 'Doug');
insert into Student(id, name) values (5, 'Elaine');
insert into Student(id, name) values (6, 'Fred');
insert into Student(id, name) values (7, 'Greta');
insert into Student(id, name) values (8, 'Harold');
insert into Student(id, name) values (9, 'Ingrid');
insert into Student(id, name) values (10, 'Jack');

insert into Student_Course(student_id, course_id) values (1, 4);
insert into Student_Course(student_id, course_id) values (1, 8);
insert into Student_Course(student_id, course_id) values (1, 11);
insert into Student_Course(student_id, course_id) values (1, 14);

insert into Student_Course(student_id, course_id) values (2, 2);
insert into Student_Course(student_id, course_id) values (2, 8);
insert into Student_Course(student_id, course_id) values (2, 10);
insert into Student_Course(student_id, course_id) values (2, 13);