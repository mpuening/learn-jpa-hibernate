package org.hibernate.dialect;

/**
 * When updating to Spring Boot 3.1.0, the version of Hibernate now no
 * longer has DerbyTenSevenDialect. However, it it referenced in the
 * Spring Framework. Remove this class once the discrepancy is resolved.
 */
public class DerbyTenSevenDialect extends DerbyDialect {

}
