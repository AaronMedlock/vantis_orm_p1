package com.aaronmedlock.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Id;
import com.aaronmedlock.annotations.JoinColumn;

public class JoinColumnField {

	private Field field;
	
	public JoinColumnField(Field field) {
		
		if( field.getAnnotation(JoinColumn.class) == null ) {
			throw new IllegalStateException("Cannot create ColumnField object as the provided field of '"
											+ getName() + "' is not annotated with @JoinTable.");
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
	 
	
	/**
	 * Return the name of the field that's annotated. 
	 * @return name of annotated field
	 */
	public String getName() {
		return field.getName();
	}
	
	
	
	/**
	 * Return the type of the field that's annotated.
	 * @return type of annotated field
	 */
	public Class<?> getType(){
		return field.getType();
	}

	
	/**
	 * Return the data type of the field that's annotated
	 * @return string of annotated field
	 */
	public String getDataType(){
		return field.getType().getSimpleName();
	}
	
	
	/**
	 * Extracts the column name attribute from the column annotation
	 * @return column name of annotated field
	 */
	public String getColumnName() {
		return field.getAnnotation(JoinColumn.class).columnName();
	}
	
	
	/**
	 * Extracts the column being referenced
	 * @return reference table and column
	 */
	public HashMap getColumnReference() {
		
		HashMap<String, String> columnReference = new HashMap<>(); 
		String[] ref = field.getAnnotation(JoinColumn.class).references().split("\\.");
		if(ref.length == 2) {
			columnReference.put("tableName", ref[0]);
			columnReference.put("columnName", ref[1]);
		} else {
			throw new IllegalStateException("Cannot create JoinColumn object as the provided reference of '"
					+ field.getAnnotation(JoinColumn.class).references() + "' is not properly annotated.\n"
					+ "The references annotation should follow the format references=\"table.column\"");
		}
		
		return columnReference;
	}
	
	/**
	 * Extracts the boolean of whether the column allows null values
	 * @return Boolean of whether or not to allow null values
	 */
	public boolean getNullable() {
		return field.getAnnotation(JoinColumn.class).allowNullValues();
	}
	
	/**
	 * Extracts a determination of whether the column would "NULL" or "NOT NULL" in SQL.
	 * @return String SQL statement of whether or not to allow null values
	 */
	public String getNullableDeclaration() {
		return (field.getAnnotation(JoinColumn.class).allowNullValues()) ? 
					" NULL" : " NOT NULL";
	}
	
	/**
	 * Extracts the boolean of whether the column allows null values
	 * @return Boolean of whether or not to allow null values
	 */
	public boolean getUnique() {
		return field.getAnnotation(JoinColumn.class).mustBeUnique();
	}
	
	/**
	 * Extracts a determination of whether the column would "UNIQUE" in SQL.
	 * @return String SQL statement of whether or not the column should be UNIQUE
	 */
	public String getUniqueDeclaration() {
		return (field.getAnnotation(JoinColumn.class).mustBeUnique()) ? 
					" UNIQUE" : "";
	}
	
	/**
	 * Extracts the maximum string size, default is 50.
	 * @return Integer value for use with VARCHAR data type declaration
	 */
	public int getMaxStringSize() {
		return field.getAnnotation(JoinColumn.class).maxStringSize();
	}
	
	/**
	 * Extracts the maximum count of whole number digits for number
	 * @return Integer value for use with doubles and floats
	 */
	public int getNumericPrecision() {
		return field.getAnnotation(JoinColumn.class).numericPrecision();
	}
	
	/**
	 * Extracts the maximum count of decimal digits for number
	 * @return Integer value for use with doubles and floats
	 */
	public int getNumericScale() {
		return field.getAnnotation(JoinColumn.class).numericScale();
	}
	
}
