package org.lvyouzaike.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/*
 * the entity for requiring voters' advice on good vote options. Corresponding to table 'vote_advice'
 * Belong to common list operations.
 */
@Entity
@Table(name="vote_advice")
public class VoteAdvice {
	private int id;          //id
	private String nickname; //nickname to submit the comment
	private String advice;   //advice for adding vote items in common list
	private Date adviceDate; //recent advice date
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	@Type(type="text")
	public String getAdvice() {
		return advice;
	}
	public void setAdvice(String advice) {
		this.advice = advice;
	}
	public Date getAdviceDate() {
		return adviceDate;
	}
	public void setAdviceDate(Date adviceDate) {
		this.adviceDate = adviceDate;
	}
	
	
}
