package com.aaronmedlock.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.model.MetaModel;
import com.aaronmedlock.util.ColumnField;
import com.aaronmedlock.util.JoinColumnField;
import com.aaronmedlock.util.PrimaryKeyField;

public class BasicCrud {
	
	private static Logger log = LoggerFactory.getLogger(TableBuilder.class);
	
	
	
	/**
	 * Insert object into the database
	 * @param obj The object to be persisted. Must contain the entity annotation
	 * @return Integer id as primary key of row in database.
	 */
	public static int insert(Object obj) {

		// Connect to the database
		Connection conn = ConnectionPool.createDbConnectionPool();
		
		// Reading from the Vantis configuration file (vantis.properties)
		Properties prop = new Properties();
		String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
		
    	try {
			prop.load(new FileReader(path));
	    } catch(FileNotFoundException e) {
			log.error("[ERROR]: The Vantis properties file was not found at {}", path);
			e.printStackTrace();
			
	    } catch (IOException e) {
			log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
			e.printStackTrace();
		}
    	
    	// Collect Info
    	String schema = prop.getProperty("db_schema");
		Class<?> clazz = obj.getClass();
		StringBuilder insertStatementVariables = new StringBuilder();
		StringBuilder insertStatementValues = new StringBuilder();
		StringBuilder insertSqlStatement = new StringBuilder();
		
		// Check if object is annotated with @Entity
		if(clazz.isAnnotationPresent(Entity.class)) {
			// Construct a meta model of the object
			MetaModel<Class<?>> metaModel = new MetaModel<Class<?>>(clazz);
			
			
			// Check if the table exists in DB
       		try {    			
       			DatabaseMetaData md = conn.getMetaData();
    			ResultSet rs = md.getTables(null, null, metaModel.getTableName().toLowerCase(), null);
    			
    			if( rs.next() == false ){
    				log.error("[ERROR] A valid table for '" + metaModel.getClassName() + " was not found in the database!");
    				return -1;
    			}
			} catch (SQLException e) {
				e.printStackTrace();
			} 

			
			// Insert foreign key fields
			LinkedList<JoinColumnField> foreignKeyFields = metaModel.getForeignKeys();
			if(foreignKeyFields.size() > 0) {
				JoinColumnField lastJCF = foreignKeyFields.getLast();

				foreignKeyFields.stream().forEach(fk -> {
					insertStatementVariables.append(fk.getColumnName());
					insertStatementValues.append(fk.get(obj));
					
	    			if( !fk.equals(lastJCF) ) {
	    				insertStatementVariables.append(", ");
	    				insertStatementValues.append(", ");
	    			}
				});
				
			}

			
			// Insert column fields
			LinkedList<ColumnField> columnFields = metaModel.getColumns();
			
			if(foreignKeyFields.size() > 0 &&
					columnFields.size() > 0) {
				insertStatementVariables.append(", ");
				insertStatementValues.append(", ");
			}
			
			if(columnFields.size() > 0) {
				ColumnField lastCF = columnFields.getLast();
					
				columnFields.stream().forEach(c -> {
					insertStatementVariables.append(c.getColumnName());	

					if(c.getDataType().equals("String")){
						insertStatementValues.append("'" + c.get(obj) + "'");
					} else {
						insertStatementValues.append(c.get(obj));
					}

	    			if( !c.equals(lastCF) ) {
	    				insertStatementVariables.append(", ");
	    				insertStatementValues.append(", ");
	    			}
				});
			}
			
			
			// Prepare and execute the insert statement
			insertSqlStatement.append("INSERT INTO " + schema + "." + metaModel.getTableName().toLowerCase() + " (" 
					+ insertStatementVariables.toString() + ") VALUES (" + insertStatementValues + ") RETURNING "
					+ metaModel.getTableName().toLowerCase() + "." + metaModel.getPrimaryKey().getColumnName() + ";");
			
			
			log.info("[EXECUTING SQL] " + insertSqlStatement.toString());
			try( PreparedStatement stmt = conn.prepareStatement(insertSqlStatement.toString())){
				ResultSet rs;
				
				if((rs = stmt.executeQuery()) != null) {
					rs.next();
					
					int id = rs.getInt(1);
					log.info("[SUCCESS] User inserted into the database with id of " + id + ".");
					
					return id;
				}
			} catch (SQLException e) {
				log.error("[ERROR] A SQL Exception was thrown. Unable was unable to insert the user into the database.");
				e.printStackTrace();
			}
		}
		
		log.error("[ERROR] The @Entity annotation was not found in the object for the '" + obj.getClass().getSimpleName()
				  + " class. Unable was unable to insert the user into the database.");
		return -1;
	}
	
	
	
	
	/**
	 * Gets the object using the id from the database.
	 * @param clazz Class to instantiate an object of
	 * @param id The id of the object's row in db.
	 * @return object
	 */
	public static Object getObject(Class<?> clazz, int id) {
		Object obj = new Object();
		
		// Check if object is annotated
		if(clazz.isAnnotationPresent(Entity.class)) {
			// Construct a meta model of the object
			MetaModel<Class<?>> metaModel = new MetaModel<Class<?>>(clazz);
			
			try(Connection conn = ConnectionPool.createDbConnectionPool()){
		
				// Check if the table exists in DB  			
       			DatabaseMetaData md = conn.getMetaData();
    			ResultSet rs = md.getTables(null, null, metaModel.getTableName().toLowerCase(), null);
    			
    			if( rs.next() == false ){
    				log.error("[ERROR] A valid table for '" + metaModel.getClassName() + " was not found in the database!");
    				return null;
    			}
    			
    			// Reading from the Vantis configuration file (vantis.properties)
    			Properties prop = new Properties();
    			String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
    			
    	    	try {
    				prop.load(new FileReader(path));
    		    } catch(FileNotFoundException e) {
    				log.error("[ERROR]: The Vantis properties file was not found at {}", path);
    				e.printStackTrace();
    				
    		    } catch (IOException e) {
    				log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
    				e.printStackTrace();
    			}
    	    	
    	    	
    	    	// Collect Info and build SQL Statement
    	    	String schema = prop.getProperty("db_schema"); 
    	    	String sqlStatement = "SELECT * FROM " + schema + "." + metaModel.getTableName() + " WHERE " 
						+ metaModel.getPrimaryKeyName() + "=" + id;
    	    	
    	    	log.info("[EXECUTING SQL] " + sqlStatement);
    	    	PreparedStatement stmt = conn.prepareStatement(sqlStatement);
    	    	
    	    	// Query the DB and build the user object
    	    	rs = stmt.executeQuery();
    			if(rs.next()) {
    				ResultSetMetaData rsMetaData =rs.getMetaData();
    				
    				// Initialize object of parameter class
    				try {
						obj = Class.forName(clazz.getName()).newInstance();
					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
						e.printStackTrace();
					}
    				
    				// Build object using data retrieved from database
					try {
						// Set primary key and verify that retrieved user is correct
						Field fieldId = clazz.getDeclaredField(metaModel.getPrimaryKeyVariable());
						int idFromDb = rs.getInt(metaModel.getPrimaryKeyName());
						fieldId.setAccessible(true);
						if(idFromDb == id) {
							fieldId.set(obj, idFromDb);
						} else {
							log.error("[ERROR] The retrieved row with the id of " + rs.getInt(metaModel.getPrimaryKeyName()) + " does not match parameter of " + id + ".");
							return null;
						}
						
						
						// Get all column names
						ArrayList<String> databaseColumns = new ArrayList<>();
						for(int i = 1; i <= rsMetaData.getColumnCount(); i++){
							databaseColumns.add(rsMetaData.getColumnName(i));
						}
						
						
						// Get foreign keys
						LinkedList<JoinColumnField> foreignKeyFields = metaModel.getForeignKeys();
						if(foreignKeyFields.size() > 0) {						
							for(JoinColumnField fk : foreignKeyFields) {
								try {
									Field field = clazz.getDeclaredField(fk.getName());
									field.setAccessible(true);
									field.set(obj, getColumnFromRS(fk.getColumnName(), rs, databaseColumns));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						
						// Get columns
						LinkedList<ColumnField> columnFields = metaModel.getColumns();
						if(columnFields.size() > 0) {
							for(ColumnField c : columnFields) {
								try {
									Field field = clazz.getDeclaredField(c.getName());
									field.setAccessible(true);
									field.set(obj, getColumnFromRS(c.getColumnName(), rs, databaseColumns));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}		
					} catch (NoSuchFieldException | SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					} catch (Exception e) {
						log.error("[ERROR]: " + e);
						e.printStackTrace();
					}
    				return obj;
    				
    				
    			} else {
    				log.warn("[WARNING] A user with the id of " + metaModel.getPrimaryKeyName() + "=" + id + " was not found in the database.");
    				return null;
    			}    			
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		}	
		return null;
	}
	
	
	
	/**
	 * Update the object using the value of its ID annotation.
	 * @param obj The object that will be updated in the database
	 * @return Whether or not the update completed
	 */
	public static boolean updateObject(Object obj) {
		
		Class<?> clazz = obj.getClass();
		
		// Check if object is annotated
		if(clazz.isAnnotationPresent(Entity.class)) {
			// Construct a meta model of the object
			MetaModel<Class<?>> metaModel = new MetaModel<Class<?>>(clazz);
			
			try(Connection conn = ConnectionPool.createDbConnectionPool()){
		
				// Check if the table exists in DB  			
       			DatabaseMetaData md = conn.getMetaData();
    			ResultSet rs = md.getTables(null, null, metaModel.getTableName().toLowerCase(), null);
    			
    			if( rs.next() == false ){
    				log.error("[ERROR] A valid table for '" + metaModel.getClassName() + " was not found in the database!");
    				return false;
    			}
    			
    			// Reading from the Vantis configuration file (vantis.properties)
    			Properties prop = new Properties();
    			String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
    			
    	    	try {
    				prop.load(new FileReader(path));
    		    } catch(FileNotFoundException e) {
    				log.error("[ERROR]: The Vantis properties file was not found at {}", path);
    				e.printStackTrace();
    				
    		    } catch (IOException e) {
    				log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
    				e.printStackTrace();
    			}
    	    	
    	    	
    	    	// Collect Info and build SQL Statement
    	    	String schema = prop.getProperty("db_schema");
    	    	
    	    	StringBuilder sqlStatement = new StringBuilder();
    	    	sqlStatement.append("UPDATE " + schema + "." + metaModel.getTableName()
    	    						+ " SET ");
    	    	
    	    	// Insert foreign key fields
    			LinkedList<JoinColumnField> foreignKeyFields = metaModel.getForeignKeys();
    			if(foreignKeyFields.size() > 0) {
    				JoinColumnField lastJCF = foreignKeyFields.getLast();

    				foreignKeyFields.stream().forEach(fk -> {
    					sqlStatement.append(fk.getColumnName() + "=" + fk.get(obj));
    					
    	    			if( !fk.equals(lastJCF) ) {
    	    				sqlStatement.append(", ");
    	    			}
    				});
    				
    			}

    			
    			// Insert column fields
    			LinkedList<ColumnField> columnFields = metaModel.getColumns();
    			
    			if(foreignKeyFields.size() > 0 &&
    					columnFields.size() > 0) {
    				sqlStatement.append(", ");
    			}
    			
    			if(columnFields.size() > 0) {
    				ColumnField lastCF = columnFields.getLast();
    					
    				columnFields.stream().forEach(c -> {
    					sqlStatement.append(c.getColumnName() + "=");	

    					if(c.getDataType().equals("String")){
    						sqlStatement.append("'" + c.get(obj) + "'");
    					} else {
    						sqlStatement.append(c.get(obj));
    					}

    	    			if( !c.equals(lastCF) ) {
    	    				sqlStatement.append(", ");
    	    			}
    				});
    			}
    			
    			// Set row to be set
    			Field primaryKeyFieldId = clazz.getDeclaredField(metaModel.getPrimaryKeyVariable());
    			primaryKeyFieldId.setAccessible(true);
    			int id = primaryKeyFieldId.getInt(obj);
    			sqlStatement.append(" WHERE " + metaModel.getPrimaryKeyName() + "=" + id +";");
    	    	
    			log.info("[EXECUTING SQL] " + sqlStatement);
    	    	PreparedStatement stmt = conn.prepareStatement(sqlStatement.toString());
    	    	
    	    	// Query the DB and build the user object
    	    	int count = stmt.executeUpdate();
    			if(count > 0) {
    				return true;
    			}
    	    	
			} catch (SQLException e1) {
				log.error("[ERROR]: Encountered a SQL Exception when accessing the database");
				e1.printStackTrace();
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
		
	}

	
	/**
	 * Update the object using the value of its ID annotation.
	 * @param obj The object that will be updated in the database
	 * @return Whether or not the update completed
	 */
	public static boolean deleteObject(Object obj) {
		Class<?> clazz = obj.getClass();
		
		// Check if object is annotated
		if(clazz.isAnnotationPresent(Entity.class)) {
			// Construct a meta model of the object
			MetaModel<Class<?>> metaModel = new MetaModel<Class<?>>(clazz);
			
			try(Connection conn = ConnectionPool.createDbConnectionPool()){
		
				// Check if the table exists in DB  			
       			DatabaseMetaData md = conn.getMetaData();
    			ResultSet rs = md.getTables(null, null, metaModel.getTableName().toLowerCase(), null);
    			
    			if( rs.next() == false ){
    				log.error("[ERROR] A valid table for '" + metaModel.getClassName() + " was not found in the database!");
    				return false;
    			}
    			
    			// Reading from the Vantis configuration file (vantis.properties)
    			Properties prop = new Properties();
    			String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
    			
    	    	try {
    				prop.load(new FileReader(path));
    		    } catch(FileNotFoundException e) {
    				log.error("[ERROR]: The Vantis properties file was not found at {}", path);
    				e.printStackTrace();
    				
    		    } catch (IOException e) {
    				log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
    				e.printStackTrace();
    			}
    	    	
    	    	
    	    	// Collect Info to build the SQL Statement
    	    	String schema = prop.getProperty("db_schema");
    	    	Field primaryKeyFieldId = clazz.getDeclaredField(metaModel.getPrimaryKeyVariable());
    	    	primaryKeyFieldId.setAccessible(true);
    			int id = primaryKeyFieldId.getInt(obj);
    			
    			// Prepare the sql statement
    			String sqlStatement = "DELETE FROM " + schema + "." + metaModel.getTableName()
							+ " CASCADE WHERE " + metaModel.getPrimaryKeyName() + "=" + id +";";
    			log.info("[EXECUTING SQL] " + sqlStatement);
    			PreparedStatement stmt = conn.prepareStatement(sqlStatement);
    	    	
    	    	// Query the DB and determine if delete was successful
    	    	int updateCount = stmt.executeUpdate();
    			if(updateCount > 0) {
    				log.info("[INFO] Object with ID #" + id + " was successfully deleted from the database");
    				return true;
    			}	
			} catch (SQLException e1) {
				log.error("[ERROR] Encountered a SQL Exception when accessing the database");
				e1.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		log.warn("[WARNING] The object was NOT successfully deleted from the database");
		return false;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private static <T> T getColumnFromRS(String columnName, ResultSet rs, ArrayList<String> databaseColumns) throws Exception{
		// Collect into
		ResultSetMetaData rsMetaData = rs.getMetaData();
		int index = databaseColumns.indexOf(columnName) + 1;// Compensate for Java's 0 - last and SQL's 1 - last
		
		if(index == -1) {
			log.error("[ERROR] Annotated column was not found in database.");
			return null;
		} else {
			switch( rsMetaData.getColumnClassName(index).toUpperCase() ) {
			
			case "JAVA.LANG.STRING":
				return (T) rs.getString(columnName);
			
			case "JAVA.LANG.INTEGER":
				return (T) new Integer(rs.getInt(columnName));
				
			case "JAVA.LANG.DOUBLE":
				return (T) new Double(rs.getDouble(columnName));
			
			case "JAVA.LANG.FLOAT":
				return (T) new Float(rs.getFloat(columnName));
				
			case "JAVA.LANG.BOOLEAN":
				return (T) new Boolean(rs.getBoolean(columnName));
				
			case "JAVA.LANG.LONG":
				return (T) new Long(rs.getLong(columnName));
				
			case "JAVA.LANG.CHARACTER":
				return (T) new Character(rs.getString(columnName).charAt(0));
				
			case "JAVA.LANG.BYTE":
				return (T) new Integer(rs.getByte(columnName));
				
			}
		}
		return null;
	}
}
