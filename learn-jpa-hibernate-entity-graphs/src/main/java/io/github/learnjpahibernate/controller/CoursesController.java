package io.github.learnjpahibernate.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.learnjpahibernate.model.Course;
import io.github.learnjpahibernate.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {

	@Autowired
	protected CourseService courseService;

	/**
	 * This will blow up because we will try to navigate JPA relationships after the
	 * session is closed.
	 */
	@GetMapping("/poorly")
	public List<Course> getCourses() {
		return courseService.getCoursesPoorly();
	}

	/**
	 * This is better because we controller exactly what data is fetched are returned.
	 */
	@GetMapping
	public List<Object> getCourses(@RequestParam(name = "projection", defaultValue = "compact") String projection) {
		return courseService.getCourses(projection);
	}

	@ExceptionHandler(Exception.class)
	public void handleError(HttpServletResponse response, Exception e) {
		e.printStackTrace();
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

}
