package org.lvyouzaike.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/*
 * the entity corresponds to the smtp email account used for group email sending. 
 * DES digest will be stored in password field.
 * before sending the group emails, the prompt will require the input of password.
 * then it is encrypted and the stored digest will be fetched for comparison.
 */
@Entity
@Table(name="smtp_account")
public class SMTPAccount {
	private int id;                      //id
	private String username;             //username
	private String password;             //digest of original password. DES symmetric algorithm.
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Type(type="text")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
