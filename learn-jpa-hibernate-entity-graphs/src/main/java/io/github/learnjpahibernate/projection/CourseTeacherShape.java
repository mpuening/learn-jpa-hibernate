package io.github.learnjpahibernate.projection;

import io.github.learnjpahibernate.model.Teacher;

public interface CourseTeacherShape extends CourseCompactShape {
	Teacher getTeacher();
}
