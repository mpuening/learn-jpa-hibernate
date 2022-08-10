package io.github.learnjpahibernate.model.money;

import java.io.Serializable;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;
import org.javamoney.moneta.Money;

/*
 * Inspired by https://github.com/hibernate/hibernate-orm/blob/master/documentation/src/test/java/org/hibernate/userguide/mapping/basic/MonetaryAmountUserType.java
 * 
 * Because of https://hibernate.atlassian.net/browse/HHH-9674
 *
 * Updated for Hibernate 6 by studying https://github.com/hibernate/hibernate-orm/blob/main/hibernate-core/src/test/java/org/hibernate/orm/test/mapping/embeddable/strategy/usertype/embedded/NameCompositeUserType.java
 */
public class MonetaryAmountUserType implements CompositeUserType<MonetaryAmount> {

	public static class MonetaryAmountMapper {
		Long price;
		String currency;
	}

	@Override
	public Class<?> embeddable() {
		return MonetaryAmountMapper.class;
	}

	@Override
	public Class<MonetaryAmount> returnedClass() {
		return MonetaryAmount.class;
	}

	@Override
	public MonetaryAmount instantiate(ValueAccess valueAccess, SessionFactoryImplementor sessionFactory) {
		// alphabetical
		String currencyCode = valueAccess.getValue(0, String.class);
		Long numberValue = valueAccess.getValue(1, Long.class);
		CurrencyUnit unit = Monetary.getCurrency(currencyCode);
		return Money.ofMinor(unit, numberValue.longValue());
	}

	@Override
	public Object getPropertyValue(MonetaryAmount ma, int property) throws HibernateException {
		// alphabetical
		switch (property) {
		case 0:
			return ma.getCurrency().getCurrencyCode();
		case 1:
			NumberValue amount = (ma == null) ? null : ma.getNumber();
			CurrencyUnit currencyUnit = (ma == null) ? null : ma.getCurrency();
			if (amount != null && currencyUnit != null && currencyUnit.getDefaultFractionDigits() > 0) {
				// Convert from major amounts to minor amounts (e.g. dollars to cents)
				for (int i = 0; i < currencyUnit.getDefaultFractionDigits(); i++) {
					ma = ma.multiply(Long.valueOf(10L));
				}
				amount = ma.getNumber();
			}
			Number number = null;
			if (amount != null) {
				number = amount.numberValue(Long.class);
			}
			return number.longValue();
		}
		return null;
	}

	@Override
	public boolean equals(MonetaryAmount x, MonetaryAmount y) throws HibernateException {
		if (x == y) {
			return true;
		}
		if (x == null || y == null) {
			return false;
		}
		MonetaryAmount mx = (MonetaryAmount) x;
		MonetaryAmount my = (MonetaryAmount) y;
		return mx.equals(my);
	}

	@Override
	public int hashCode(MonetaryAmount x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public MonetaryAmount deepCopy(MonetaryAmount value) throws HibernateException {
		if (value != null) {
			MonetaryAmount money = (MonetaryAmount) value;
			return Monetary.getDefaultAmountFactory().setCurrency(money.getCurrency()).setNumber(money.getNumber())
					.create();
		}
		return null;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(MonetaryAmount value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public MonetaryAmount assemble(Serializable cached, Object owner) throws HibernateException {
		final Object[] parts = (Object[]) cached;
		String currencyCode = (String) parts[0];
		Long numberValue = (Long) parts[1];
		CurrencyUnit unit = Monetary.getCurrency(currencyCode);
		return Money.ofMinor(unit, numberValue.longValue());
	}

	@Override
	public MonetaryAmount replace(MonetaryAmount detached, MonetaryAmount managed, Object owner)
			throws HibernateException {
		return deepCopy(detached);
	}
}
