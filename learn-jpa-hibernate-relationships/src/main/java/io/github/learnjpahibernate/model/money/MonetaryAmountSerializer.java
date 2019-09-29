package io.github.learnjpahibernate.model.money;

import java.io.IOException;
import java.util.Locale;

import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public class MonetaryAmountSerializer extends JsonSerializer<MonetaryAmount> implements ContextualSerializer {
	private final Locale locale;

	private final String pattern;

	public MonetaryAmountSerializer() {
		this(Locale.US, "Â¤ 0.00");
	}

	public MonetaryAmountSerializer(Locale locale, String pattern) {
		this.locale = locale;
		this.pattern = pattern;
	}

	@Override
	public void serialize(MonetaryAmount monetaryAmount, JsonGenerator generator, SerializerProvider serializers)
			throws IOException {
		if (monetaryAmount != null) {
			MonetaryAmountFormat customFormat = MonetaryFormats
					.getAmountFormat(AmountFormatQueryBuilder.of(this.locale).set("pattern", this.pattern).build());
			generator.writeString(customFormat.format(monetaryAmount));
		}
	}

	@Override
	public JsonSerializer<MonetaryAmount> createContextual(SerializerProvider provider, BeanProperty property)
			throws JsonMappingException {
		JsonSerializer<MonetaryAmount> result = new MonetaryAmountSerializer();
		MonetaryAmountJsonSerialize annotation = null;
		if (property != null) {
			annotation = property.getAnnotation(MonetaryAmountJsonSerialize.class);
		}
		if (annotation != null) {
			Locale locale = new Locale(annotation.lang(), annotation.country());
			String pattern = annotation.pattern();
			result = new MonetaryAmountSerializer(locale, pattern);
		}
		return result;
	}
}
