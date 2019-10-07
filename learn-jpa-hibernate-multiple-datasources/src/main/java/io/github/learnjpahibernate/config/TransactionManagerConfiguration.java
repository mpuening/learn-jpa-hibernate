package io.github.learnjpahibernate.config;

import org.springframework.context.annotation.Configuration;

/**
 * Should one not want JTA Transactions, do the following:
 * 
 * 1) Use non xa database driver in application.yml
 * 
 * 2) Use the beans in this class and switch the switch the names of the
 * transaction managers
 * 
 * 3) Remove "bitronixConfiguration" from the @DependsOn() dependency in the JPA
 * configuration classes
 */
@Configuration
public class TransactionManagerConfiguration {

	public static final String PRIMARY_TRANSACTION_MANAGER_NAME = "transactionManager";
	public static final String SECONDARY_TRANSACTION_MANAGER_NAME = "transactionManager";

//	public static final String PRIMARY_TRANSACTION_MANAGER_NAME = "primaryTransactionManager";
//	public static final String SECONDARY_TRANSACTION_MANAGER_NAME = "secondaryTransactionManager";
//	
//	@Autowired
//	@Qualifier("primaryEntityManagerFactory")
//	private EntityManagerFactory primaryEntityManagerFactory;
//	
//	@Autowired
//	@Qualifier("secondaryEntityManagerFactory")
//	private EntityManagerFactory secondaryEntityManagerFactory;
//	
//	@Primary
//	@Bean(name = { "transactionManager", "primaryTransactionManager" })
//	public PlatformTransactionManager primaryTransactionManager() throws IllegalArgumentException, NamingException {
//		return new JpaTransactionManager(primaryEntityManagerFactory);
//	}
//	
//	@Bean("secondaryTransactionManager")
//	public PlatformTransactionManager secondaryTransactionManager() throws IllegalArgumentException, NamingException {
//		return new JpaTransactionManager(secondaryEntityManagerFactory);
//	}
}
