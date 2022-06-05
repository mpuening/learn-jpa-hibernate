package io.github.learnjpahibernate.repository;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class ExtendedJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements ExtendedJpaRepository<T, ID> {

	private EntityManager entityManager;

	public ExtendedJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Transactional
	public List<T> findByPropertyContainsValue(String property, String value) {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = builder.createQuery(getDomainClass());
		Root<T> root = criteriaQuery.from(getDomainClass());
		criteriaQuery.select(root).where(builder.like(root.<String>get(property), "%" + value + "%"));
		TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
		return query.getResultList();
	}
}
