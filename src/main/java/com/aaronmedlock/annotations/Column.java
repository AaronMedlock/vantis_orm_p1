package com.aaronmedlock.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	
	String columnName();
	boolean allowNullValues() default false;
	boolean mustBeUnique() default false;
	int maxStringSize() default 50;
	int numericPrecision() default 30;
	int numericScale() default 8;

}
