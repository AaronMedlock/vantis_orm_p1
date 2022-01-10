package com.aaronmedlock.inspection;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.annotations.Id;
import com.aaronmedlock.annotations.JoinColumn;
import com.aaronmedlock.model.MetaModel;

public abstract class ObjectMapping {
	

	/**
	 * Generates the meta models of annotated classes
	 * @return 
	 */
	public static LinkedList<MetaModel<Class<?>>> generateMetaModels() {
		
		Set<Class> classList = getAllClasses();
		LinkedList<MetaModel<Class<?>>> resultList = new LinkedList<>();
		
		
		for(Class<?> clazz : classList) {
			if(clazz.isAnnotationPresent(Entity.class)) {
				resultList.add( new MetaModel<Class<?>>(clazz) );
			}
		}

		return resultList;
	}
	

	public static void inspectClasses(Class<?> clazz) {
		
		if(clazz.isAnnotationPresent(Entity.class)) {
			Entity entity = (Entity)clazz.getAnnotation(Entity.class);
			System.out.println( "\n" + entity.tableName() );
			System.out.println("Drop table? " + entity.dropExistingTable());

			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields) {
				if(field.isAnnotationPresent(Id.class)) {
					Id id = (Id)field.getAnnotation(Id.class);
					System.out.println( "  - " + id.columnName() + " (@Id)");
				}
				if(field.isAnnotationPresent(Column.class)) {
					Column column = (Column)field.getAnnotation(Column.class);
					System.out.println( "  - " + column.columnName() + " (@Column)");
				}
				if(field.isAnnotationPresent(JoinColumn.class)) {
					JoinColumn joinColumn = (JoinColumn)field.getAnnotation(JoinColumn.class);
					System.out.println( "  - " + joinColumn.columnName() +" (@JoinColumn)" 
										+ " references " + joinColumn.references());
				}
				if( !field.isAnnotationPresent(Id.class) && 
					!field.isAnnotationPresent(Column.class) &&
					!field.isAnnotationPresent(JoinColumn.class) ) {
					System.out.println( "  - " + field.getName() + " (NOT ANNOTATED)");
				}
			}
			
			
		}
	}
	
	
	public static Set<Class> getAllClasses() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
		try {
			model = reader.read(new FileReader("pom.xml"));
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
        String packageName = model.getGroupId();
        
	    Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
	    return reflections.getSubTypesOf(Object.class)
	      .stream()
	      .collect(Collectors.toSet());
	}
	
}
