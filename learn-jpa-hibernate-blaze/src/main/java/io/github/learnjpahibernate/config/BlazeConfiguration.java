package io.github.learnjpahibernate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BlazeConfiguration {
	@Bean
	public CriteriaBuilderFactory criteriaBuilderFactory(EntityManagerFactory entityManagerFactory) {
		return Criteria.getDefault().createCriteriaBuilderFactory(entityManagerFactory);
	}
}
