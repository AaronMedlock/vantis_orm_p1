package com.aaronmedlock.demo;

import java.util.Objects;

import com.aaronmedlock.annotations.Column;
import com.aaronmedlock.annotations.Entity;
import com.aaronmedlock.annotations.Id;
import com.aaronmedlock.annotations.JoinColumn;

@Entity(tableName="company", dropExistingTable=false)
public class DemoModelToo {
	
	@Id(columnName="employee_id")
	public int id;
	
	@JoinColumn(columnName="personal_id", references="person.person_id", mustBeUnique=true)
	private int personId;
	
	@Column(columnName="job_title")
	private String jobTitle;
	
	@Column(columnName="department_code")
	private int department;
	
	@Column(columnName="marked_for_layoff")
	private boolean markedForLayoff;
	
	public DemoModelToo() {}
	
	public DemoModelToo(int personId, String jobTitle, int department, boolean markedForLayoff) {
		super();
		this.personId = personId;
		this.jobTitle = jobTitle;
		this.department = department;
		this.markedForLayoff = markedForLayoff;
	}
	
	
	public DemoModelToo(int id, int personId, String jobTitle, int department, boolean markedForLayoff) {
		this(personId, jobTitle, department, markedForLayoff);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public int getDepartment() {
		return department;
	}

	public void setDepartment(int department) {
		this.department = department;
	}

	public boolean isMarkedForLayoff() {
		return markedForLayoff;
	}

	public void setMarkedForLayoff(boolean markedForLayoff) {
		this.markedForLayoff = markedForLayoff;
	}

	@Override
	public int hashCode() {
		return Objects.hash(department, id, jobTitle, markedForLayoff, personId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DemoModelToo other = (DemoModelToo) obj;
		return department == other.department && id == other.id && Objects.equals(jobTitle, other.jobTitle)
				&& markedForLayoff == other.markedForLayoff && personId == other.personId;
	}

	@Override
	public String toString() {
		return "DemoModelToo [id=" + id + ", personId=" + personId + ", jobTitle=" + jobTitle + ", department="
				+ department + ", markedForLayoff=" + markedForLayoff + "]";
	}
	
	
	
}
