package com.aaronmedlock.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aaronmedlock.Vantis;
import com.aaronmedlock.inspection.ClassInspectorLogging;

/**
 * A simple sample project to demonstrate how basic CRUD actions could be performed.
 */
public class DemoApp{
	private static Logger log = LoggerFactory.getLogger(ClassInspectorLogging.class);
	public static void main (String[] args) {	
//		DemoModel demoModel = new DemoModel("Jane", "Doe");
//		
//		//CREATE		
//		int userId = Vantis.createRow(demoModel);
//		System.out.println("The id returned is " + userId);
//		demoModel.setId(userId);
//		
//		//READ
//		DemoModelToo demoModelToo = (DemoModelToo) Vantis.getRow(DemoModelToo.class, 1);
//		System.out.println("Returned ID is: " + demoModelToo.getJobTitle());
//		
//		//Prepare for Update and Delete
//		demoModel.setLastName("Doo");
//		
//		//UPDATE
//		System.out.println("Updated Object? " + Vantis.updateRow(demoModel));
//		
//		//DELETE
//		System.out.println("Deleted object? " + Vantis.deleteRow(demoModel));
		log.info("Demo App!");
	}

}
