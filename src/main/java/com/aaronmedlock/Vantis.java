package com.aaronmedlock;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.dao.BasicCrud;
import com.aaronmedlock.dao.ConnectionPool;
import com.aaronmedlock.dao.TableBuilder;
import com.aaronmedlock.inspection.ObjectMapping;

public final class Vantis {
	private static Logger log = LoggerFactory.getLogger(Vantis.class);
	
	
	static {
		Properties prop = new Properties();
		String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
		Boolean tableScannedOnStartup = false;
		
		
	    try {
			prop.load(new FileReader(path));
			tableScannedOnStartup = Boolean.parseBoolean( prop.getProperty("scan_on_startup") );
			
	    } catch(FileNotFoundException e) {
			log.error("[ERROR]: The Vantis properties file was not found at {}", path);
			e.printStackTrace();
			
	    } catch (IOException e) {
			log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
			e.printStackTrace();
		}
	    
	    if(tableScannedOnStartup) {
	    	createTable();
	    }		
	}
	
	public Vantis() {

	}
	
	
	/**
	 * Create tables using classes with Entity annotation within the project. Existing tables will be ignored.
	 * 
	 * @return boolean value of whether the table was successfully created in the database.
	 */
	public static boolean createTable() {
		return TableBuilder.persistTable(ObjectMapping.generateMetaModels());
	}
	
	
	/**
	 * Create a new row in the database by passing in the desired object to
	 * persist. The OBJECT'S CLASS MUST BE ANNOTATED with the entity annotation
	 * in order to be successfully persisted.
	 * 
	 * @param obj The object to be inserted into the database.
	 * @return Integer id of the Primary Key from the inserted row
	 */
	public static int createRow(Object obj) {
		return BasicCrud.insert(obj);
	}
	
	
	/**
	 * Get a row from the database by passing in the desired class, such as FooBar.class,
	 * and the integer ID associated with it. THE OBJECT'S CLASS MUST BE ANNOTATED with
	 * entity annotation and THE ID MUST EXIST IN THE DATABASE in order to be retrieved.
	 * 
	 * @param clazz The class of the object desired, such as FooBar.class.
	 * @param id The integer ID of the desired row, such as 1.
	 * @return An object of parameter class instantiated with all data from the database 
	 * for that row.
	 */
	public static Object getRow(Class<?> clazz, int id) {
		return BasicCrud.getObject(clazz, id);
	}
	
	
	/**
	 * Update an existing row in the database by passing in the desired object to
	 * persist. The OBJECT'S CLASS MUST BE ANNOTATED with the entity annotation
	 * in order to be successfully persisted.
	 * 
	 * @param obj The object to be updated into the database.
	 * @return boolean of whether the row was successfully updated or not.
	 */
	public static boolean updateRow(Object obj) {
		return BasicCrud.updateObject(obj);
	}
	
	/**
	 * Delete an existing row in the database by passing in the desired object to
	 * remove. The OBJECT'S CLASS MUST BE ANNOTATED with the entity annotation
	 * in order to be successfully persisted.
	 * 
	 * @param obj The object to be deleted from the database.
	 * @return boolean of whether the row was successfully deleted or not.
	 */
	public static boolean deleteRow(Object obj) {
		return BasicCrud.deleteObject(obj);
	}
	
	
	/**
	 * Execute a custom SQL statements and receive the results as a ResultSet object.
	 * @param sqlStatement String query to be executed for the specified database.
	 * @return ResultSet object if successful, SQLException or null if unsuccessful.
	 */
	public static ResultSet executeSqlWithResults(String sqlStatement) {
		Connection conn = ConnectionPool.createDbConnectionPool();
		
		// Try the SQL command
		try( PreparedStatement stmt = conn.prepareStatement(sqlStatement)){
			return stmt.executeQuery();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Execute a custom SQL statement and receive boolean if successful.
	 * @param sqlStatement String query to be executed for the specified database.
	 * @return boolean true if successful, SQLException or false if unsuccessful.
	 */
	public static boolean executeSqlNoResults(String sqlStatement) {
		Connection conn = ConnectionPool.createDbConnectionPool();
		
		try( PreparedStatement stmt = conn.prepareStatement(sqlStatement)){
			stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;	
	}
	
	
	
	
}
