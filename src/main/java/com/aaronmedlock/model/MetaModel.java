package com.aaronmedlock.model;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.annotations.Id;
import com.aaronmedlock.annotations.JoinColumn;
import com.aaronmedlock.exception.ClassHasNoColumnAnnotationsException;
import com.aaronmedlock.exception.ClassNotAnnotatedWithIdException;
import com.aaronmedlock.util.ColumnField;
import com.aaronmedlock.util.JoinColumnField;
import com.aaronmedlock.util.PrimaryKeyField;

public class MetaModel<T> {
	
	private Class<?> clazz;
	private Entity clazzEntity; // Table
	private PrimaryKeyField primaryKeyField; // PK
	private LinkedList<ColumnField> columnFields; // Columns
	private LinkedList<JoinColumnField> foreignKeyFields;

	public final static String blockSeparator = "\n\n*******************************\n",
			sectionSeparator = "\n- - - - - - - - - - - - - - - -\n";
	private static Logger log = LoggerFactory.getLogger(MetaModel.class);
	
	
	public MetaModel(Class<?> clazz) {
		
		// Register constructor param
		this.clazz = clazz;
		
		if(clazz.isAnnotationPresent(Entity.class)) {
			this.clazzEntity = (Entity) clazz.getAnnotation(Entity.class);
			this.columnFields = new LinkedList<>();
			this.foreignKeyFields = new LinkedList<>();		
			Field[] fields = clazz.getDeclaredFields();
			boolean hasIdAnnotation = false,
					hasColumnAnnotation = false;
			
			
			// Check the class for fields
			for(Field field:fields) {
				// Has an ID
				if(field.isAnnotationPresent(Id.class)) {
					Id id = (Id)field.getAnnotation(Id.class);
					this.primaryKeyField = new PrimaryKeyField(field);
					hasIdAnnotation = true;
					log.info("Found '" + clazz.getName() + "' contains @Column value of '"
								+ field.getAnnotation(Id.class).columnName()
								+ "' for '" + field.getName() + "'");
					continue;
				}
				
				// Has columns
				if(field.isAnnotationPresent(Column.class)) {
					Column column = (Column)field.getAnnotation(Column.class);
					columnFields.add( new ColumnField(field) );
					log.info("Found '" + clazz.getName() + "' contains @Column value of '"
								+ field.getAnnotation(Column.class).columnName()
								+ "' for '" + field.getName() + "'");
					hasColumnAnnotation = true;
					continue;
				}
				
				// Has a join column
				if(field.isAnnotationPresent(JoinColumn.class)) {
					JoinColumn joinColumn = (JoinColumn)field.getAnnotation(JoinColumn.class);
					foreignKeyFields.add( new JoinColumnField(field) );
					log.info("Found '" + clazz.getName() + "' contains @JoinColumn value of '" 
								+ field.getAnnotation(JoinColumn.class).columnName()
								+ "' for '" + field.getName() + "'");
					continue;
				}
			}
			
			// Validate that class has an ID and columns
			if(hasIdAnnotation) {
				if(hasColumnAnnotation) {
					log.info("[SUCCESS!] MetaModel for '" + clazz.getName() + "' was successfully created!");
				} else {
					throw new ClassHasNoColumnAnnotationsException("[ERROR] There were no @Column annotations found for '" + clazz.getName() + "'");
				}
			} else {
				throw new ClassNotAnnotatedWithIdException("[ERROR] @Id was not found for '" + clazz.getName() + "'");
			}
		} else {
			throw new ClassHasNoColumnAnnotationsException("[ERROR] Class, " + clazz.getName() + ", is not annotated with @Entity to mark it as a table.");
		}
	}
	
	
	
	public static MetaModel<Class<?>> getTable(Class<?> clazz) {
		
		if(clazz.isAnnotationPresent(Entity.class)) {
			log.info("[INSPECTING] Found {} to be annotated with @Entity.", clazz.getName());
			return new MetaModel<>(clazz);
			
		} else {
			log.info("[INSPECTING] There is NO @Entity annotation for {}", clazz.getName());
			return null;
		}
	}
	
	public Field getAllFields() {
		
		
		
		return null;
	}
	
	
	public LinkedList<ColumnField> getColumns() {
		return columnFields;
	}
	
	
	public PrimaryKeyField getPrimaryKey() {
		return primaryKeyField;
	}

	
	
	public LinkedList<JoinColumnField> getForeignKeys() {
		return foreignKeyFields;
	}
	
	
	public String getSimpleClassName() {
		return clazz.getSimpleName();
	}
	
	public String getClassName() {
		return clazz.getName();
	}
	
	public String getTableName() {
		return this.clazzEntity.tableName();
	}
	
	public boolean dropTableDesired() {
		return this.clazzEntity.dropExistingTable();
	}
	public String getPrimaryKeyName() {
		return this.primaryKeyField.getColumnName();
	}
	
	public String getPrimaryKeyVariable() {
		return this.primaryKeyField.getName();
	}
}
