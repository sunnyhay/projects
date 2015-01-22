package org.lvyouzaike.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

/*
 * the entity to comments on the common list of international resorts. Corresponding to table 'interresort_comment'
 * Belong to common list operations.
 * One of four common list comment tables 
 */
@Entity
@Table(name="interresort_comment")
public class CommentInterResort  extends CommentModel{
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
