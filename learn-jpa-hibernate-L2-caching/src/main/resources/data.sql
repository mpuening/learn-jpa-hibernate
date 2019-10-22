insert into Teacher(id, name) values (1, 'Ms. Crabapple');
insert into Teacher(id, name) values (2, 'Ms. Boyd');
insert into Teacher(id, name) values (3, 'Mr. Cooper');
insert into Teacher(id, name) values (4, 'Mr. Schwartz');
insert into Teacher(id, name) values (5, 'Mrs. Lopez');
insert into Teacher(id, name) values (6, 'Mrs. Star');
insert into Teacher(id, name) values (7, 'Mr. Huber');

insert into Course(id, name, teacher_id) values (1, 'Algebra', 2);
insert into Course(id, name, teacher_id) values (2, 'Geometry', 2);
insert into Course(id, name, teacher_id) values (3, 'Trigonometry', 2);
insert into Course(id, name, teacher_id) values (4, 'Calculus', 2);
insert into Course(id, name, teacher_id) values (5, 'Biology', 3);
insert into Course(id, name, teacher_id) values (6, 'Chemistry', 3);
insert into Course(id, name, teacher_id) values (7, 'Physics', 3);
insert into Course(id, name, teacher_id) values (8, 'English', 1);
insert into Course(id, name, teacher_id) values (9, 'Spanish', 5);
insert into Course(id, name, teacher_id) values (10, 'German', 4);
insert into Course(id, name, teacher_id) values (11, 'Art', 6);
insert into Course(id, name, teacher_id) values (12, 'Theater', 1);
insert into Course(id, name, teacher_id) values (13, 'World History', 7);
insert into Course(id, name, teacher_id) values (14, 'US History', 7);
insert into Course(id, name, teacher_id) values (15, 'Government', 7);

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

insert into Student_Course(student_id, course_id) values (2, 1);
insert into Student_Course(student_id, course_id) values (2, 7);
insert into Student_Course(student_id, course_id) values (2, 10);
insert into Student_Course(student_id, course_id) values (2, 15);


insert into Student_Course(student_id, course_id) values (3, 4);
insert into Student_Course(student_id, course_id) values (3, 8);
insert into Student_Course(student_id, course_id) values (3, 11);
insert into Student_Course(student_id, course_id) values (3, 13);


insert into Student_Course(student_id, course_id) values (4, 4);
insert into Student_Course(student_id, course_id) values (4, 8);
insert into Student_Course(student_id, course_id) values (4, 11);
insert into Student_Course(student_id, course_id) values (4, 12);


insert into Student_Course(student_id, course_id) values (5, 2);
insert into Student_Course(student_id, course_id) values (5, 9);
insert into Student_Course(student_id, course_id) values (5, 12);
insert into Student_Course(student_id, course_id) values (5, 14);


insert into Student_Course(student_id, course_id) values (6, 3);
insert into Student_Course(student_id, course_id) values (6, 7);
insert into Student_Course(student_id, course_id) values (6, 11);
insert into Student_Course(student_id, course_id) values (6, 15);


insert into Student_Course(student_id, course_id) values (7, 2);
insert into Student_Course(student_id, course_id) values (7, 8);
insert into Student_Course(student_id, course_id) values (7, 10);
insert into Student_Course(student_id, course_id) values (7, 13);


insert into Student_Course(student_id, course_id) values (8, 3);
insert into Student_Course(student_id, course_id) values (8, 6);
insert into Student_Course(student_id, course_id) values (8, 9);
insert into Student_Course(student_id, course_id) values (8, 12);


insert into Student_Course(student_id, course_id) values (9, 4);
insert into Student_Course(student_id, course_id) values (9, 5);
insert into Student_Course(student_id, course_id) values (9, 12);
insert into Student_Course(student_id, course_id) values (9, 13);
