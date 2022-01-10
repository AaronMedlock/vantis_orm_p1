package com.aaronmedlock.inspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassInspectorLogging {
	
	public final static String blockSeparator = "\n\n*******************************\n",
						sectionSeparator = "\n- - - - - - - - - - - - - - - -\n";
	private static Logger log = LoggerFactory.getLogger(ClassInspectorLogging.class);
	
	public static void inspectClass(Class<?> clazz) {
		log.info( "\n\n"
				+ "*******************************************\n"
				+ "C L A S S  I N S P E C T I O N  R E P O R T\n"
				+ "*******************************************\n");
		listPublicConstructors(clazz);
		listNonPublicConstructors(clazz);
		listPublicFields(clazz);
		listNonPublicFields(clazz);
		listPublicMethods(clazz);
		listDeclaredMethods(clazz);
	}
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listPublicConstructors(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "PUBLIC CONSTRUCTORS FOR: " + clazz.getName().toUpperCase());
		
		// Print each public constructor
		Constructor<?>[] constructors = clazz.getConstructors();
		for (Constructor<?> constructor : constructors) {
			log.info("\tConstructor name: " + constructor.getName());
			log.info("\tConstructor param types: " + Arrays.toString(constructor.getParameterTypes()) 
								+ sectionSeparator);
		}
	}
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listNonPublicConstructors(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "NON-VISIBLE CONSTRUCTORS FOR {}", clazz.getName());
		
		// Print each non-visible constructor
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			// Parse modifier out
			if ((constructor.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
				continue;
			}
			log.info("\tConstructor name: {}", constructor.getName());
			log.info("\tConstructor param types: {}", Arrays.toString(constructor.getParameterTypes()) 
								+ sectionSeparator);
		}
	}
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listPublicFields(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "PUBLIC FIELDS OF " + clazz.getName().toUpperCase());
		
		// Print each public field
		Field[] fields = clazz.getFields();
		if (fields.length == 0) {
			log.info("There are no public fields in " + clazz.getName());
		}
		for (Field field : fields) {
			log.info("\tField name: " + field.getName());
			log.info("\tField type: " + field.getType());
			log.info("\tIs field primitive? :: " + field.getType().isPrimitive());
			log.info("\tModifiers bit value: " + Integer.toBinaryString(field.getModifiers())
								+ sectionSeparator);
		}
	}
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listNonPublicFields(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "NON-PUBLIC FIELDS OF " + clazz.getName().toUpperCase());
		
		// Print each non-public field
		Field[] fields = clazz.getDeclaredFields();
		if (fields.length == 0) {
			log.info("\nThere aren't any non-public fields in " + clazz.getName());
		}
		for (Field field : fields) {
			if ((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC)
				continue;
			log.info("\tField name: " + field.getName());
			log.info("\tField type: " + field.getType());
			log.info("\tIs field primitive?: " + field.getType().isPrimitive());
			log.info("\tModifiers bit value: " + Integer.toBinaryString(field.getModifiers())
								+ sectionSeparator);
		}
	}
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listPublicMethods(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "PUBLIC FIELDS OF " + clazz.getName().toUpperCase());
		
		// Print each public method
		Method[] methods = clazz.getMethods();
		if (methods.length == 0) {
			log.info("There are no public methods in " + clazz.getName());
		}
		for (Method method : methods) {
			if (method.getDeclaringClass() == Object.class) {
				continue;
			}
			
			log.info("\nMethod name: " + method.getName());
			log.info("\tMethod param count: " + method.getParameterCount());
			log.info("\tMethod declared class: " + method.getDeclaringClass());
			log.info("\tMethod declared annotations: " + Arrays.toString(method.getDeclaredAnnotations()));

			Parameter[] params = method.getParameters();
			for (Parameter param : params) {
				log.info("\t\tParameter name: " + param.getName());
				log.info("\t\tParameter type: " + param.getType());
				log.info("\t\tParameter annotations: " + Arrays.toString(param.getDeclaredAnnotations()));
			}
			log.info(sectionSeparator);
		}
	}
	
	
	
	/**
	 * 
	 * @param clazz
	 */
	public static void listDeclaredMethods(Class<?> clazz) {
		// Print block title
		log.info(blockSeparator 
							+ "DECLARED METHODS FOR " + clazz.getName().toUpperCase());

		// Print each declared method
		Method[] methods = clazz.getDeclaredMethods();

		if (methods.length == 0) {
			log.info("\tThere are no non-public methods in the class: " + clazz.getName());
		}

		for (Method method : methods) {

			if ((method.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
				continue;
			}

			log.info("\tName: " + method.getName());
			Class<?>[] parameterTypes = method.getParameterTypes();
			log.info("\tModifiers bit value: " + Integer.toBinaryString(method.getModifiers()));
			log.info("\tDeclaring class: " + method.getDeclaringClass().getName());
			log.info("\tDeclared annotations: " + Arrays.toString(method.getDeclaredAnnotations()));

			log.info("\tParameter count: " + parameterTypes.length);		
			Parameter[] params = method.getParameters();
			for (Parameter param : params) {
				log.info("\t\tParameter name: " + param.getName());
				log.info("\t\tParameter type: " + param.getType());
				log.info("\t\tParameter annotations: " + Arrays.toString(param.getAnnotations()));
			}
			log.info(sectionSeparator);
		}
	}
}
