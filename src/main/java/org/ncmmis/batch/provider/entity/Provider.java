package org.ncmmis.batch.provider.entity;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Provider implements Serializable {
	
	private int id;
	private int npi;
	private String lastName;
	private String firstName;
	private String ssn;
	private String email;
	
	@Override
	public String toString() {
		return "PROVIDER: id=" + id + ", NPI=" + npi + ", Last Name=" + lastName + ", First Name=" + firstName + ", SSN=" + ssn + ", Email=" + email;
	}

	public int getId() {
		return id;
	}	
	
	public int getNpi() {
		return npi;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}
	
	public String getSsn() {
		return ssn;
	}

	public String getEmail() {
		return email;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setNpi(int npi) {
		this.npi = npi;
	}

	public void setSsn(String ssn) {
		this.ssn = ssn;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
