package io.github.learnjpahibernate.model.money;

import java.math.BigDecimal;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.StringNode;

public class MonetaryAmountDeserializer extends ValueDeserializer<MonetaryAmount> {
	@Override
	public MonetaryAmount deserialize(JsonParser parser, DeserializationContext sortorder)
			throws JacksonException {
		StringNode moneyValueNode = parser.readValueAsTree();
		String moneyValue = moneyValueNode.asString();
		MonetaryAmount result = null;
		if (moneyValue != null && !moneyValue.isEmpty()) {
			String[] tokens = moneyValue.split(" ");
			String currencyCode = tokens[0];
			Number amount = new BigDecimal(tokens[1]);
			result = Monetary.getDefaultAmountFactory().setCurrency(currencyCode).setNumber(amount).create();
		}
		return result;
	}
}