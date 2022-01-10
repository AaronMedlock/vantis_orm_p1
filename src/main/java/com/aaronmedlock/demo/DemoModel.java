package com.aaronmedlock.demo;

import java.util.Objects;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.annotations.Id;

@Entity(tableName="person", dropExistingTable=false)
public class DemoModel {
	
	@Id(columnName="person_id")
	private int id;
	
	@Column(columnName="first_name")
	private String firstName;
	
	@Column(columnName="last_name")
	private String lastName;
	
	private String nonAnnotatedField;
	
	public DemoModel() {}
	
	public DemoModel(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	
	public DemoModel(int id, String firstName, String lastName) {
		this(firstName, lastName);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getNonAnnotatedField() {
		return nonAnnotatedField;
	}

	public void setNonAnnotatedField(String nonAnnotatedField) {
		this.nonAnnotatedField = nonAnnotatedField;
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, id, lastName, nonAnnotatedField);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DemoModel other = (DemoModel) obj;
		return Objects.equals(firstName, other.firstName) && id == other.id && Objects.equals(lastName, other.lastName)
				&& Objects.equals(nonAnnotatedField, other.nonAnnotatedField);
	}

	@Override
	public String toString() {
		return "DemoModel [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", nonAnnotatedField="
				+ nonAnnotatedField + "]";
	}
	
	

}
