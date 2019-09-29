package io.github.learnjpahibernate.model.money;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = MonetaryAmountSerializer.class)
@JsonDeserialize(using = MonetaryAmountDeserializer.class)
public @interface MonetaryAmountJsonSerialize {
	String lang() default "en";

	String country() default "US";

	// USD 1.29
	String pattern() default "¤ 0.00";
}
