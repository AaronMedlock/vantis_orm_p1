package com.aaronmedlock.util;

import java.lang.reflect.Field;

import com.aaronmedlock.annotations.Id;

public class PrimaryKeyField {
	
	private Field field;
	
	public PrimaryKeyField(Field field) {
		
		if( field.getAnnotation(Id.class) == null ) {
			throw new IllegalStateException("Cannot create ColumnField object as the provided field of '"
											+ getName() + "' is not annotated with @Id.");
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
		return field.getAnnotation(Id.class).columnName();
	}
}
