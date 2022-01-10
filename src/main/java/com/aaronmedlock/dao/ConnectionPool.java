package com.aaronmedlock.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.inspection.ClassInspectorLogging;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool{
	
	private static HikariDataSource hds = null;
	private static HikariConfig config= new HikariConfig();
	
	private static Logger log = LoggerFactory.getLogger(ClassInspectorLogging.class);
	protected static int maxPoolSize;
	
	
	
	/**
	 * Configures the Hikari connection pool by using the Vantis configuration file 
	 * at src/main/resources/vantis.properties
	 * 
	 */
	protected static void initializeDataSource() {
		
		// Reading from the Vantis configuration file (vantis.properties)
		Properties prop = new Properties();
		String path = new File("src/main/resources/vantis.properties").getAbsolutePath();
	    
		// Attempt to configure the connection pool using the Vantis configuration file
	    try {
	    	
	    	try {
	    		maxPoolSize = Integer.parseInt( prop.getProperty("max_pool_size") );
	    	    
		    } catch (NumberFormatException e){
		    	log.error("[ERROR]: Maximum Pool Size was not detected as an integer."
		    				+ "Setting initial pool size to default of 10.\n" + e);	  
		    	maxPoolSize = 10;
		    }
	    	

	    	prop.load(new FileReader(path));
	    	config.setJdbcUrl( prop.getProperty("db_url") );
	    	config.setSchema( prop.getProperty("db_schma") );
	    	config.setUsername( prop.getProperty("db_username") );
	    	config.setPassword( prop.getProperty("db_password") );
	    	config.setMaximumPoolSize( maxPoolSize );
	    	
	    	
	    } catch(FileNotFoundException e) {
			log.error("[ERROR]: The Vantis properties file was not found at {}", path);
			e.printStackTrace();
			
	    } catch (IOException e) {
			log.error("[ERROR]: Encountered an IO Exception when reading the properties file at {}", path);
			e.printStackTrace();
		}
	    
	    // Create the connection pool
	    hds = new HikariDataSource(config);
		
	}
	
	
	
	/**
	 * Create a connection pool based on settings specified in the 
	 * project's src/main/resources/vantis.properties
	 * 
	 * @return A connection the data source
	 * 
	 * @throws SQLException If a database access error occurs
	 */
	public static Connection createDbConnectionPool() {
		Connection hdsConnection = null;
		
		if(hds == null) initializeDataSource();
		
		
		try {
			hdsConnection = hds.getConnection();
		} catch (SQLException e) {
			log.error("[ERROR]: Unable to create a connection");
			e.printStackTrace();
		}
		
		log.info("[SUCCESS] A connection pool to the database was successfully established!");
		return hdsConnection;
	}
	
	
	
	/**
	 * This method must be called when the application shuts down or
	 * the connection to the database risks being left open.
	 */
	public void shutdown() {
		hds.close();
	}


}
