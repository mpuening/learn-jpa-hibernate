package io.github.learnjpahibernate.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.internal.util.collections.StandardStack;
import org.hibernate.query.criteria.internal.CriteriaQueryImpl;
import org.hibernate.query.criteria.internal.compile.CriteriaInterpretation;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;
import org.hibernate.sql.ast.Clause;
import org.springframework.data.jpa.domain.Specification;

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
		String selectjpaql = createJPAQLstatement(entityManager, query, parameters);
		String selectSql = convertJPAQLtoNativeSQL(entityManager, selectjpaql);
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
	 * Use Hibernate internals to get the JPQL statement
	 */
	@SuppressWarnings("rawtypes")
	private static String createJPAQLstatement(EntityManager entityManager, CriteriaQuery<Tuple> query,
			List<Object> parameters) {
		final Session session = entityManager.unwrap(Session.class);
		final SessionFactory sessionFactory = session.getSessionFactory();
		final Dialect dialect = ((SessionFactoryImplementor) sessionFactory).getJdbcServices().getDialect();

		AtomicInteger index = new AtomicInteger();
		CriteriaInterpretation criteriaInterpretation = ((CriteriaQueryImpl) query).interpret(new RenderingContext() {

			@Override
			public String generateAlias() {
				return "o";
			}

			@Override
			public ExplicitParameterInfo registerExplicitParameter(ParameterExpression<?> criteriaQueryParameter) {
				throw new UnsupportedOperationException("registerExplicitParameter() not supported yet");
			}

			@Override
			public String registerLiteralParameterBinding(Object parameter, Class javaType) {
				parameters.add(parameter);
				return "?" + index.incrementAndGet();
			}

			@Override
			public String getCastType(Class javaType) {
				throw new UnsupportedOperationException("getCastType() not supported yet");
			}

			@Override
			public Dialect getDialect() {
				return dialect;
			}

			private final Stack<Clause> clauseStack = new StandardStack<>();
			private final Stack<FunctionExpression> functionContextStack = new StandardStack<>();

			@Override
			public Stack<Clause> getClauseStack() {
				return clauseStack;
			}

			@Override
			public Stack<FunctionExpression> getFunctionStack() {
				return functionContextStack;
			}
		});
		String selectjpaql = null;
		try {
			selectjpaql = (String) FieldUtils.readField(criteriaInterpretation, "val$jpaqlString", true);
			selectjpaql = selectjpaql.replace(":?", "?");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Unable to calculate JPQL statement", e);
		}
		return selectjpaql;
	}

	/**
	 * Convert jpaql to native sql, also using Hibernate internals
	 */
	private static String convertJPAQLtoNativeSQL(EntityManager entityManager, String selectjpaql) {
		final Session session = entityManager.unwrap(Session.class);
		final SessionFactory sessionFactory = session.getSessionFactory();
		final QueryTranslatorFactory ast = new ASTQueryTranslatorFactory();
		final QueryTranslator queryTranslator = ast.createQueryTranslator("select", selectjpaql, Collections.EMPTY_MAP,
				(SessionFactoryImplementor) sessionFactory, null);
		queryTranslator.compile(null, false);
		String selectSql = queryTranslator.getSQLString();
		return selectSql;
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
