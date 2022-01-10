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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.inspection.ObjectMapping;
import com.aaronmedlock.model.MetaModel;
import com.aaronmedlock.util.ColumnField;
import com.aaronmedlock.util.JoinColumnField;

public abstract class TableBuilder {
	
	public final static String blockSeparator = "\n\n*******************************\n",
								sectionSeparator = "\n- - - - - - - - - - - - - - - -\n";
	private static Logger log = LoggerFactory.getLogger(TableBuilder.class);
	
	
	
	

	
	
	public static boolean persistTable(LinkedList<MetaModel<Class<?>>> metaModels) {
		
		boolean isTablePersisted = false;
		
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
    	StringBuilder sqlDropTablesStatement = new StringBuilder();
    	StringBuilder sqlPersistTablesStatement = new StringBuilder();
    	StringBuilder sqlAddForeignKeysStatement = new StringBuilder();
    	
    	
    	// Assmble the SQL Statement
       	for(MetaModel<Class<?>> metaModel : ObjectMapping.generateMetaModels()) {
       		StringBuilder sqlCreateTableStatement = new StringBuilder();
       		
       		// Check if the table already exists in DB
       		try {    			
       			DatabaseMetaData md = conn.getMetaData();
    			ResultSet rs = md.getTables(null, null, metaModel.getTableName().toLowerCase(), null);
    			
    			if(rs.next() && (metaModel.dropTableDesired() == false)){
    				log.info("[SKIPPING] The table, " + metaModel.getTableName().toLowerCase() + ", already exists in the database"
    						+ " and is NOT set to be dropped if it already exists.");
    				continue;
    			}
			} catch (SQLException e) {
				e.printStackTrace();
			} 
       		
       		// Drop table if exists
       		if(metaModel.dropTableDesired()) {
       			sqlDropTablesStatement.append("DROP TABLE IF EXISTS " + schema.toLowerCase() + "."
									+ metaModel.getTableName().toLowerCase() + " CASCADE;");
       		}
       		
    		// Create Table
    		sqlCreateTableStatement.append("CREATE TABLE " + schema.toLowerCase() + "."
							+ metaModel.getTableName().toLowerCase() + "( ");
    		
    		
    		// Append ID / Primary Key
    		sqlCreateTableStatement.append(metaModel.getPrimaryKey().getColumnName() + " SERIAL PRIMARY KEY, ");
    		
    		// Append Join Columns
    		LinkedList<JoinColumnField> foreignKeyFields = metaModel.getForeignKeys();    		
    		if(foreignKeyFields.size() > 0) {
    			JoinColumnField lastJCF = foreignKeyFields.getLast();
    			foreignKeyFields.stream().forEach(fk -> {
	    			HashMap<String, String> columnReference = fk.getColumnReference();
	    			
	    			// Assemble SQL Statement
	    			sqlCreateTableStatement.append(fk.getColumnName());
	    			sqlCreateTableStatement.append(getDataTypeSQL( fk.getType().getSimpleName(), 
	    					fk.getMaxStringSize(),
	    					fk.getNumericPrecision(),
	    					fk.getNumericScale() 
						));
	    			
	    			sqlCreateTableStatement.append( fk.getNullableDeclaration() );
	    			sqlCreateTableStatement.append( fk.getUniqueDeclaration() );
	    			
	    			sqlAddForeignKeysStatement.append("ALTER TABLE " + schema.toLowerCase() + "." + metaModel.getTableName().toLowerCase() 
	  					+ " ADD CONSTRAINT " + fk.getColumnName() + "_refs_" + columnReference.get("columnName")
	  					+ "_fk FOREIGN KEY(" + fk.getColumnName() + ") REFERENCES " + schema.toLowerCase() + "."
	  					+ columnReference.get("tableName") + "(" + columnReference.get("columnName") 
	  					+ ")  ON DELETE SET NULL ON UPDATE CASCADE; ");
	    			
	    			if( !fk.equals(lastJCF) ) {
	        			sqlCreateTableStatement.append(", ");
	    			} else { 
	    				sqlCreateTableStatement.append(" "); 
	    			}
	    		});
    			sqlCreateTableStatement.append(", ");
    		}
    		
    		
    		// Append Columns
    		LinkedList<ColumnField> columnFields = metaModel.getColumns();
    		ColumnField lastCF = columnFields.getLast();
    		
    		metaModel.getColumns().stream().forEach(c -> {
    			
    			sqlCreateTableStatement.append(c.getColumnName());
    			sqlCreateTableStatement.append(getDataTypeSQL( c.getType().getSimpleName(), 
					    							c.getMaxStringSize(),
					    							c.getNumericPrecision(),
					    							c.getNumericScale() 
					    						));   
    			
    			sqlCreateTableStatement.append( c.getNullableDeclaration() );
    			sqlCreateTableStatement.append( c.getUniqueDeclaration() );
    			
    			if( !c.equals(lastCF) ) {
        			sqlCreateTableStatement.append(", ");
    			} else { 
    				sqlCreateTableStatement.append(" "); 
    			}
    			
    		});
    		sqlCreateTableStatement.append("); ");
    		sqlPersistTablesStatement.append(sqlCreateTableStatement.toString());
       	}
       	
       	// Persist table to the database
       	if(sqlPersistTablesStatement.length() > 0) {
	       	try( PreparedStatement stmt = conn.prepareStatement(sqlPersistTablesStatement.toString())){
	       		
	       		// Drop tables if desired
	       		if(sqlDropTablesStatement.length() > 0) {
	       			log.info("[EXECUTING] " + sqlDropTablesStatement.toString());
	       			conn.prepareStatement(sqlDropTablesStatement.toString()).executeUpdate();
	       			log.info("[SUCCESS!] Table(s) have been successfully dropped from the database.");
	       		}
	       		
	       		// Insert new Tables
	       		log.info("[EXECUTING] " + sqlPersistTablesStatement.toString());
	       		stmt.executeUpdate();
	       		log.info("[SUCCESS!] Table(s) have been successfully added to the database.");
	       		
	       		// Add foreign keys
	       		if(sqlAddForeignKeysStatement.length() > 0) {
	       			log.info("[EXECUTING] " + sqlAddForeignKeysStatement.toString());
	       			conn.prepareStatement(sqlAddForeignKeysStatement.toString()).executeUpdate();
	       			log.info("[SUCCESS!] Table joins have been successfully added to the tables.");
	       		}
	       		
	       		return true;
	       	} catch (SQLException e) {
	       		log.error("[ERROR] A SQL exception has occured.\n" + e);
				e.printStackTrace();
			}
       	}
		
		return isTablePersisted;
	}
	
	private static String getPackageName() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model = null;
		try {
			model = reader.read(new FileReader("pom.xml"));
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
        return model.getGroupId();
	}
	
	
	public static void inspectClass(Class<?> clazz) {
		
		if(clazz.isAnnotationPresent(Entity.class)) {
			Entity entity = (Entity)clazz.getAnnotation(Entity.class);
			System.out.println( entity.tableName() );

			Field[] fields = clazz.getDeclaredFields();
			for(Field field:fields) {
				if(field.isAnnotationPresent(Column.class)) {
					Column column = (Column)field.getAnnotation(Column.class);
					System.out.println( column.columnName() );
				}
			}
			
			
		}
	}
	
	
	private static List<Class> getAllClasses() {
		String packageName = getPackageName();
	    Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
	    return reflections.getSubTypesOf(Object.class)
	      .stream()
	      .collect(Collectors.toList());
	}
	
	
	
	private static String getDataTypeSQL (String dataType, int maxStringSize, int numericPrecision, int numericScale) {
		
		String returnStatement;
			switch( dataType ) {
			
			case "String":
				returnStatement = " VARCHAR(" + maxStringSize + ")";
				break;
			
			case "int":
				returnStatement = " INTEGER";
				break;
				
			case "double": case "float":
				returnStatement = " NUMERIC(" + numericPrecision
									+ ", " + numericScale + ")";
				break;
				
			case "boolean":
				returnStatement = " BOOLEAN";
				break;
				
			case "long":
				returnStatement = " BIGINT";
				break;
				
			case "char":
				returnStatement = " CHARACTER(1)";
				break;
				
			case "byte":
				returnStatement = " SMALLINT";
				break;
			
			default:
				returnStatement = " VARCHAR(" + maxStringSize + ")";
				break;
				
		}		
		return returnStatement;		
	}
	

}
