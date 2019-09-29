package io.github.learnjpahibernate.model.money;

import java.io.IOException;
import java.math.BigDecimal;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class MonetaryAmountDeserializer extends JsonDeserializer<MonetaryAmount> {
	@Override
	public MonetaryAmount deserialize(JsonParser parser, DeserializationContext sortorder)
			throws IOException, JsonProcessingException {
		String moneyValue = parser.getCodec().readValue(parser, String.class);
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