package io.github.learnjpahibernate.model.money;

import java.util.Locale;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class MonetaryAmountSerializer extends ValueSerializer<MonetaryAmount> {
	private final Locale locale;

	private final String pattern;

	public MonetaryAmountSerializer() {
		this(Locale.US, "¤ 0.00");
	}

	public MonetaryAmountSerializer(Locale locale, String pattern) {
		this.locale = locale;
		this.pattern = pattern;
	}

	@Override
	public void serialize(MonetaryAmount monetaryAmount, JsonGenerator generator, SerializationContext ctxt) throws JacksonException {
		if (monetaryAmount != null) {
			MonetaryAmountFormat customFormat = MonetaryFormats
					.getAmountFormat(AmountFormatQueryBuilder.of(this.locale).set("pattern", this.pattern).build());
			generator.writeString(customFormat.format(monetaryAmount));
		}
	}

	@Override
    public ValueSerializer<MonetaryAmount> createContextual(SerializationContext ctxt, BeanProperty property) {
		ValueSerializer<MonetaryAmount> result = new MonetaryAmountSerializer();
		MonetaryAmountJsonSerialize annotation = null;
		if (property != null) {
			annotation = property.getAnnotation(MonetaryAmountJsonSerialize.class);
		}
		if (annotation != null) {
			Locale locale = Locale.of(annotation.lang(), annotation.country());
			String pattern = annotation.pattern();
			result = new MonetaryAmountSerializer(locale, pattern);
		}
		return result;
	}
}
