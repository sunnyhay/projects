package org.lvyouzaike.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/*
 * The entity of email subscription. Corresponding table in database 'travelzaike' is 'email_sub'.
 * Belong to email subscription operation.
 * Many-to-Many relation to table (object) resort
 */
@Entity
@Table(name="email_sub")
public class Mail {
	private String address;  //email address submited by the user
	private int id;  //key
	private Date lastSendDate;  //the recent email sending date for the user
	private Date subDate;  //the time when the email subscription is done. 
	private boolean subOrNot;  //whether any customized stuff (cities or resorts) is selected by the user
	private boolean validOrNot;  //is this email subscription valid or not?
	private Set<Resort> resorts = new HashSet<Resort>();  //the Resort list of cities or resorts the user customizes, a set
	
	public String getAddress() {
		return address;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public Date getLastSendDate() {
		return lastSendDate;
	}
	@ManyToMany(cascade={CascadeType.MERGE},
			fetch=FetchType.EAGER)
	@JoinTable(name = "email_resort", 
			joinColumns = { @JoinColumn(name = "e_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "r_id") })
	public Set<Resort> getResorts() {
		return resorts;
	}
	public Date getSubDate() {
		return subDate;
	}
	public boolean isSubOrNot() {
		return subOrNot;
	}
	public boolean isValidOrNot() {
		return validOrNot;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setLastSendDate(Date lastSendDate) {
		this.lastSendDate = lastSendDate;
	}
	public void setResorts(Set<Resort> resorts) {
		this.resorts = resorts;
	}
	public void setSubDate(Date subDate) {
		this.subDate = subDate;
	}
	public void setSubOrNot(boolean subOrNot) {
		this.subOrNot = subOrNot;
	}
	public void setValidOrNot(boolean validOrNot) {
		this.validOrNot = validOrNot;
	}

}
