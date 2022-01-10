package com.aaronmedlock.util;

import java.lang.reflect.Field;
import com.aaronmedlock.annotations.Column;

public class ColumnField {

	private Field field;
	
	public ColumnField(Field field) {
		
		if(field.getAnnotation(Column.class) == null) {
			throw new IllegalStateException("Cannot create ColumnField object as the provided field, '"
											+ getName() + "' is not annotated with @Column");
		}
		
		this.field = field;	
	}
	
	
	/**
	 * Return the value of the field that's annotated
	 * @param obj object to get the value of
	 * @return the value of the field represented in obj object.
	 */
	 public Object get(Object obj) {
		 try {
			 field.setAccessible(true);
			return field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		 return null;
	 }
	 
	
	public String getName() {
		return field.getName();
	}
	
	
	public Class<?> getType(){
		return field.getType();
	}
	
	public String getDataType(){
		return field.getType().getSimpleName();
	}
	
	/**
	 * Extracts the column name attribute from the column annotation
	 * @return The element's annotation
	 */
	public String getColumnName() {
		return field.getAnnotation(Column.class).columnName();
	}
	
	/**
	 * Extracts the boolean of whether the column allows null values
	 * @return Boolean of whether or not to allow null values
	 */
	public boolean getNullable() {
		return field.getAnnotation(Column.class).allowNullValues();
	}
	
	/**
	 * Extracts a determination of whether the column would "NULL" or "NOT NULL" in SQL.
	 * @return String SQL statement of whether or not to allow null values
	 */
	public String getNullableDeclaration() {
		return (field.getAnnotation(Column.class).allowNullValues()) ? 
					" NULL" : " NOT NULL";
	}
	
	/**
	 * Extracts the boolean of whether the column allows null values
	 * @return Boolean of whether or not to allow null values
	 */
	public boolean getUnique() {
		return field.getAnnotation(Column.class).mustBeUnique();
	}
	
	/**
	 * Extracts a determination of whether the column would "UNIQUE" in SQL.
	 * @return String SQL statement of whether or not the column should be UNIQUE
	 */
	public String getUniqueDeclaration() {
		return (field.getAnnotation(Column.class).mustBeUnique()) ? 
					" UNIQUE" : "";
	}
	
	/**
	 * Extracts the maximum string size, default is 50.
	 * @return Integer value for use with VARCHAR data type declaration
	 */
	public int getMaxStringSize() {
		return field.getAnnotation(Column.class).maxStringSize();
	}
	
	/**
	 * Extracts the maximum count of whole number digits for number
	 * @return Integer value for use with doubles and floats
	 */
	public int getNumericPrecision() {
		return field.getAnnotation(Column.class).numericPrecision();
	}
	
	/**
	 * Extracts the maximum count of decimal digits for number
	 * @return Integer value for use with doubles and floats
	 */
	public int getNumericScale() {
		return field.getAnnotation(Column.class).numericScale();
	}

}
