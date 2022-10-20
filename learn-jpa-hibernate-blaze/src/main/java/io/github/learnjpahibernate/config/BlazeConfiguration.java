package io.github.learnjpahibernate.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;

import jakarta.persistence.EntityManagerFactory;

@Configuration
public class BlazeConfiguration {

	@Autowired
	protected EntityManagerFactory entityManagerFactory;

	@Bean
	public CriteriaBuilderFactory criteriaBuilderFactory() {
		CriteriaBuilderConfiguration config = Criteria.getDefault();
		CriteriaBuilderFactory criteriaBuilderFactory = config.createCriteriaBuilderFactory(this.entityManagerFactory);
		return criteriaBuilderFactory;
	}
}
