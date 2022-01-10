package com.aaronmedlock.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.inspection.ClassInspectorLogging;
import com.aaronmedlock.model.MetaModel;

public class Configuration {
	
	private static Logger log = LoggerFactory.getLogger(ClassInspectorLogging.class);
	
	private String dbURL, dbUserName, dbPassword;
	private List<MetaModel<Class<?>>> metaModelList;

	
	
	/**
	 * 
	 * @param annotatedClass
	 * @return
	 */
	public Configuration addAnnotatedClass(Class annotatedClass) {
		
		if(metaModelList == null) {
			metaModelList = new LinkedList<>();
		}
		
		metaModelList.add(MetaModel.getTable(annotatedClass));
		
		return this;
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	public List<MetaModel<Class<?>>> getMetaModels(){
		return (metaModelList == null) ? Collections.emptyList() : metaModelList;
	}
	
}
