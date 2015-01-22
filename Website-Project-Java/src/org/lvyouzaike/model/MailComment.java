package org.lvyouzaike.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/*
 * The entity corresponding to email_unsub table. To get the mail-user comment on why unsubscribe the email subscription.
 * Belong to email subscription operation.
 */
@Entity
@Table(name="email_unsub")
public class MailComment {
	private String address;         //email address
	private String customreason;    //the user's own reason
	private String formatreason;    //some pre-defined reasons, a string with user-chosen reasons delimited by commas, e.g. "没意思，垃圾邮件嫌疑"
	private int id;                 //id
	private Date unsubDate;         //unsubscription date
	
	public String getAddress() {
		return address;
	}
	@Type(type="text")
	public String getCustomreason() {
		return customreason;
	}
	public String getFormatreason() {
		return formatreason;
	}
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public Date getUnsubDate() {
		return unsubDate;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setCustomreason(String customreason) {
		this.customreason = customreason;
	}
	public void setFormatreason(String formatreason) {
		this.formatreason = formatreason;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setUnsubDate(Date unsubDate) {
		this.unsubDate = unsubDate;
	}
	
	
}
