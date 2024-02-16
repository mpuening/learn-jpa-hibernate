package io.github.learnjpahibernate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.internal.ParameterMetadataImpl;
import org.hibernate.query.internal.QueryParameterBindingsImpl;
import org.hibernate.query.spi.QueryOptions;
import org.hibernate.query.spi.QueryParameterBindings;
import org.hibernate.query.sqm.internal.DomainParameterXref;
import org.hibernate.query.sqm.sql.SqmTranslator;
import org.hibernate.query.sqm.sql.SqmTranslatorFactory;
import org.hibernate.query.sqm.tree.expression.ValueBindJpaCriteriaParameter;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.hibernate.sql.ast.tree.select.SelectStatement;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public class InsertFromSelect {

	/**
	 * Create a native query to insert data into a table from a JPQL select
	 * statement. Callers need to provide the insert table and its SQL columns and
	 * how they map to a JPA query and specification. The JPA query can contain
	 * literal values if they do not come from the query.
	 *
	 * JPA does not yet provide a way to get a native query from a JPQL query. This
	 * class relies on the Hibernate implementation to dig out that value.
	 *
	 * @param insertTable    Table to insert
	 * @param columnMappings Mapping from insert table SQL column names (map keys)
	 *                       to select query JPA property names or literal values
	 *                       (map values)
	 * @param entityManager  Entity manager
	 * @param entityClass    Entity class for select statement
	 * @param specification  Predicate for the select where clause
	 *
	 * @return native query for insert statement
	 */
	public static <T> Query createNativeQuery(String insertTable, Map<String, Object> columnMappings,
			EntityManager entityManager, Class<T> entityClass, Specification<T> specification) {

		List<Object> parameters = new ArrayList<>();

		CriteriaQuery<Tuple> query = createSelectQuery(columnMappings, entityManager, entityClass, specification);
		String selectSql = convertJPAQLtoNativeSQL(entityManager, query, parameters);
		Query insert = createInsertStatement(insertTable, columnMappings, entityManager, selectSql, parameters);
		return insert;
	}

	/**
	 * Create SQL Statement from JPA specification
	 */
	private static <T> CriteriaQuery<Tuple> createSelectQuery(Map<String, Object> columnMappings,
			EntityManager entityManager, Class<T> entityClass, Specification<T> specification) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
		Root<T> root = query.from(entityClass);
		Predicate filterPredicate = specification.toPredicate(root, query, criteriaBuilder);
		query.where(filterPredicate);
		List<Selection<?>> selections = new ArrayList<>();
		columnMappings.forEach((column, property) -> {
			// try property as member of root object, otherwise use literal value
			if (property instanceof String) {
				try {
					selections.add(root.get(property.toString()));
				} catch (IllegalArgumentException e) {
					selections.add(criteriaBuilder.literal(property));
				}
			} else {
				selections.add(criteriaBuilder.literal(property));
			}
		});
		query.multiselect(selections);
		return query;
	}

	/**
	 * Use Hibernate internals to get the Native SQL statement
	 */
	@SuppressWarnings("rawtypes")
	private static String convertJPAQLtoNativeSQL(EntityManager entityManager, CriteriaQuery<Tuple> query,
			List<Object> parameters) {
		final Session session = entityManager.unwrap(Session.class);
		final SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) session.getSessionFactory();
		final Dialect dialect = sessionFactory.getJdbcServices().getDialect();
		final SqmTranslatorFactory ftranslatorFactory = sessionFactory.getQueryEngine().getSqmTranslatorFactory();

		SqmSelectStatement sqm = (SqmSelectStatement) query;
		DomainParameterXref domainParameterXref = DomainParameterXref.from(sqm);
		ParameterMetadataImpl parameterMetadata = new ParameterMetadataImpl(domainParameterXref.getQueryParameters());
		QueryParameterBindings parameterBindings = QueryParameterBindingsImpl.from(parameterMetadata, sessionFactory);
		SqmTranslator<SelectStatement> selectTranslator = ftranslatorFactory.createSelectTranslator(
				sqm,
				QueryOptions.NONE,
				domainParameterXref,
				parameterBindings,
				new LoadQueryInfluencers(sessionFactory),
				sessionFactory,
				true);

		String sql = dialect
				.getSqlAstTranslatorFactory()
				.buildSelectTranslator(sessionFactory, new SelectStatement(selectTranslator.translate().getSqlAst().getQueryPart()))
				.translate(null, QueryOptions.NONE)
				.getSqlString();

		// Save parameter values to be used insert statement
		parameterBindings.visitBindings((parameter, queryParameterBinding) -> {
			ValueBindJpaCriteriaParameter impl = (ValueBindJpaCriteriaParameter)parameter;
			parameters.add(impl.getValue());
		});

		return sql;
	}

	/**
	 * Now produce native insert statement
	 */
	private static Query createInsertStatement(String insertTable, Map<String, Object> columnMappings,
			EntityManager entityManager, String selectSql, List<Object> parameters) {
		StringBuffer insertStatement = new StringBuffer();
		insertStatement.append("insert into ");
		insertStatement.append(insertTable);
		insertStatement.append("(");
		insertStatement.append(columnMappings.keySet().stream().collect(Collectors.joining(",")));
		insertStatement.append(") ");
		insertStatement.append(selectSql);

		Query insert = entityManager.createNativeQuery(insertStatement.toString());
		AtomicInteger index = new AtomicInteger();
		parameters.stream().forEach(parameter -> {
			insert.setParameter(index.incrementAndGet(), parameter);
		});
		return insert;
	}
}
