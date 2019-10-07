package io.github.learnjpahibernate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.github.learnjpahibernate.repository.ExtendedJpaRepositoryImpl;

@Configuration
@EnableJpaRepositories(basePackages = "io.github.learnjpahibernate", repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
public class ExtendedRepositoryConfiguration {

}
