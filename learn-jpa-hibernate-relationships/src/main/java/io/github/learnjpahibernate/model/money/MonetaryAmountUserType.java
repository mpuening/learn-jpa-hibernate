package io.github.learnjpahibernate.model.money;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.NumberValue;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.javamoney.moneta.Money;

/*
 * Inspired by https://github.com/hibernate/hibernate-orm/blob/master/documentation/src/test/java/org/hibernate/userguide/mapping/basic/MonetaryAmountUserType.java
 * 
 * Because of https://hibernate.atlassian.net/browse/HHH-9674
 */
public class MonetaryAmountUserType implements UserType {
	@Override
	public int[] sqlTypes() {
		return new int[] {

				Types.BIGINT, Types.VARCHAR

		};
	}

	@Override
	public Class<?> returnedClass() {
		return MonetaryAmount.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
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
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		BigInteger amount = StandardBasicTypes.BIG_INTEGER.nullSafeGet(rs, names[0], session);
		if (amount == null) {
			return null;
		}
		String currencyCode = StandardBasicTypes.STRING.nullSafeGet(rs, names[1], session);
		CurrencyUnit unit = Monetary.getCurrency(currencyCode);
		return Money.ofMinor(unit, amount.longValue());
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		MonetaryAmount ma = (MonetaryAmount) value;
		NumberValue amount = (ma == null) ? null : ma.getNumber();
		CurrencyUnit currencyUnit = (ma == null) ? null : ma.getCurrency();
		if (amount != null && currencyUnit != null && currencyUnit.getDefaultFractionDigits() > 0) {
			// Convert from major amounts to minor amounts (e.g. dollars to cents)
			for (int i = 0; i < currencyUnit.getDefaultFractionDigits(); i++) {
				ma = ma.multiply(BigInteger.valueOf(10L));
			}
			amount = ma.getNumber();
		}
		Number number = null;
		if (amount != null) {
			number = amount.numberValue(BigInteger.class);
		}
		String currency = null;
		if (currencyUnit != null) {
			currency = currencyUnit.getCurrencyCode();
		}
		StandardBasicTypes.BIG_INTEGER.nullSafeSet(st, number, index, session);
		StandardBasicTypes.STRING.nullSafeSet(st, currency, index + 1, session);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
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
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}
}
