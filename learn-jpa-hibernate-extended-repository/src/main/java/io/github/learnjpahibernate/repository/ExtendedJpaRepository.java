package io.github.learnjpahibernate.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExtendedJpaRepository<T, ID extends Serializable> 
  extends JpaRepository<T, ID> {
  
    public List<T> findByPropertyContainsValue(String property, String value);
}