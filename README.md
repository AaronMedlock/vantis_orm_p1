<img src="https://aaronmedlock.com/img/2022-vantisorm-logo_COLOR-alt.png" style="width: 75%;"/>
<b>Version 1.0 — "Kit"</b>

## Project Description
Vantis is a Java Object Relational Mapping framework for PostgreSQL. This framework performs the heavy lifting typically fulfilled by the DAO layer by automatically handling the creation of database tables and rows. It is designed to be flexible in allowing you to automate the creation and update of tables, columns, and rows or to support custom implementations with manual methods.

## Technologies Used
* Java 8
* JUnit
* PostgreSQL
* Log4J 1.2.17
* SLF4J 1.7.25
* HikariCP 4.0.3

## Features
### Currently Implemented
- [x] Vantis handles the mapping of Java classes with the flexibility to be done automatically or on-demand as needed by your project.
- [x] Simply annotate your classes, define your constraints, and allow Vantis to perform the rest.
- [x] Create table joins in a single annotation
- [x] Use as little or as much SQL as you want by either passing Vantis your objects to persist, update, or delete in your database or simply pass SQL statements.
- [x] Easy to use API allows you to eliminate writing your basic CRUD methods.
- [x] Perform your database queries efficiently with connection pooling.

### To-Do List

- [ ] Implement improved caching
- [ ] Expand CRUD methods
- [ ] Expand SQL database support
- [ ] Improved aggregate functions
- [ ] Expanded support for join columns and enhanced logic for creating table joins

## Getting Started
Currently the project must be included as a local dependency. However, doing so can be done in three easy steps.

1. Clone the repository
```
  git clone https://github.com/AaronMedlock/vantis_orm_p1.git
  cd vantis_orm_p1
  mvn install
```
2. Place the following inside your project's pom.xml file.
```
<dependency>
  <groupId>com.aaronmedlock</groupId>
	<artifactId>VantisORM</artifactId>
	<version>1.0.0-SNAPSHOT</version>
	<scope>compile</scope>
</dependency>
```
3. Finally create and initialize the _vantis.properties_ file in src/main/resources/ directory of your project.
```
db_url=jdbc:postgresql://localhost:5432/postgres
db_schema=schema
db_username=username
db_password=password
max_pool_size=10
scan_on_startup=true
```
     

## Usage
### Annotating Classes
* <b>@Entity(tableName="table_name")</b>
  - Placed above a class declaration to indicate that the class is associated with table "table_name"
  - Use ```tableName="table_name"``` to specify the name for the table in the database.
  - Use```dropExistingTable=true``` if you wish to recreate the entire table every time that ```Vantis.createTable();``` is called automatically and/or manually. THIS SHOULD ONLY BE USED IN DEVELOPMENT. Default is false.

* <b>@Id(columnName="column_name")</b>
  - Placed above a class member instance variable declaration to indicate that the annotated field is to be the primary key in the database. For example ```@Id(columnName="user_id")```.
  - Use ```columnName="column_name"``` to specify the column name in the database.

* <b>@Column(columnName="column_name")</b>
  - Placed above a class member instance variable declaration to indicate that the annotated field is a column in the table with the name column_name.
  - Use ```columnName="column_name"``` to specify the column name in the database.
  - Use ```allowNullValues=true``` to allow null values in the database. Default is false.
  - Use ```mustBeUnique=true``` to specify that values must be unique in the database. Default is false.
  - Use ```maxStringSize=100``` to specify that strings in the database, varchar, must be some number or less. Default is 50.
  - Use ```numericPrecision=3``` to specify the amount of digit places for whole number. For example, specifying 3 and using a value of 1234 would truncate 4.
  - Use ```numericScale=2``` to specify the amount of decimal places in a double or float. For example, specifying 2 and using a value of 1234.567  would truncate 7.

* <b>@JoinColumn(columnName="column_name", references="table_name.column_name")</b>
  - Placed above a class member instance variable declaration to indicate that the annotated field is to be a foriegn key to another table and column in the database.
  - Use ```columnName="column_name"``` to specify the column name in the database.
  - Use ```references="table_name.column_name"``` to specify the table's annotation name and the column's annotation name that this annotated field will refer to. For example, ```@JoinColumn(columnName="user_id", references="foo_bar.account_id")```

<br/>
<img src="https://aaronmedlock.com/img/2022_vantis-features-img0.png" style="width: 33%;"/>

```toBe || !toBe // CRUD made easier below```

<br/>

### API for CRUD
* ```Vantis.createTable();```
  - Create tables using classes with Entity annotation within the project. Existing tables will be ignored.
  - <b>returns</b> a boolean value for whether tables were created in the database using the project's annotations

* ```Vantis.createRow(Object obj);```
  - Create a new row in the database by passing in the desired object to persist. The OBJECT'S CLASS MUST BE ANNOTATED with the ENTITY and ID annotations in order to be successfully persisted.
  - <b>Parameter</b> object is the object to be inserted into the database. 
  - <b>Returns</b> the integer id of the Primary Key from the inserted row.

* ```Vantis.getRow(Class<?> clazz, int id);```
  - Get a row from the database by passing in the desired class, such as FooBar.class, and the integer ID associated with it. THE OBJECT'S CLASS MUST BE ANNOTATED with entity annotation and THE ID MUST EXIST IN THE DATABASE in order to be retrieved.
  - <b>Parameter 1</b> is the class name of the object desired, such as FooBar.class.
  - <b>Parameter 2</b> is the id of the object desired, such as 1.
  - <b>Returns</b> an object of parameter class instantiated with all data from the database for that row.

* ```Vantis.updateRow(Object obj);```
  - Update an existing row in the database by passing in the desired object to persist. The OBJECT'S CLASS MUST BE ANNOTATED with the entity annotation in order to be successfully persisted.
  - <b>Parameter</b> is the object to be updated in the database.
  - <b>Returns</b> boolean of whether the row was successfully updated or not.

* ```Vantis.deleteRow(Object obj);```
  - Delete an existing row in the database by passing in the desired object to remove. The OBJECT'S CLASS MUST BE ANNOTATED with the entity annotation in order to be successfully persisted.
  - <b>Parameter</b> is the object to be deleted from the database.
  - <b>Returns</b> a boolean of whether the row was successfully deleted or not.

* ```Vantis.executeSqlWithResults(String sqlStatement);```
  - Execute a custom SQL statements and receive the results as a ResultSet object.
  - <b>Paramater</b> is a String query to be executed for the database specified in Vantis.properties.
  - <b>Returns</b> a ResultSet object if successful, SQLException or null if unsuccessful.

* ```Vantis.executeSqlNoResults(String sqlStatement);```
  - <b>Paramater</b> is a String query to be executed for the database specified in Vantis.properties.
  - <b>Returns</b> a boolean true if successful, SQLException or false if unsuccessful.
  
## License
This project uses the following license: [GNU Public License 3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).<br/>
The VantisORM logo and all artwork is &copy;2022 Aaron Medlock