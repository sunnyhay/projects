package org.lvyouzaike.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/*
 * the entity to comments on the individual list of road fees. Corresponding to table 'roadfee_comment'
 * Belong to individual list operations.
 * One of five individual list comment tables (4.26 so far) 
 */
@Entity
@Table(name="roadfee_comment")
public class CommentRoadfee  extends CommentModel{
	private int id;           //id
	private String nickname;  //nickname
	private String comment;   //comment
	private Date commentDate; //comment date
	
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	
	
}
