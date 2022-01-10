package com.aaronmedlock.util;

import java.lang.reflect.Field;

import com.aaronmedlock.annotations.JoinColumn;

public class ForeignKeyField {

	private Field field;
	
	
	/**
	 * 
	 * @param field
	 */
	public ForeignKeyField(Field field) {
		if(field.getAnnotation(JoinColumn.class) == null) {
			throw new IllegalStateException("Cannot create a ColumnField object as the provided field of '"
											+ getName() + "' is not annotated with @JoinColumn");
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
	 * 
	 * @return
	 */
	public String getName() {
		return field.getName();
	}
	
	
	/**
	 * 
	 * @return a class object identifying the declared type of field represented by object
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
	 * 
	 * @return
	 */
	public String getColumnName() {
		return field.getAnnotation(JoinColumn.class).columnName();
	}
}
