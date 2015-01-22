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
 * the entity of voter for common list. Corresponding to table 'voter'.
 * Belong to common list operations.
 * Many-to-Many relation to resort table (object) 
 */
@Entity
@Table(name="voter")
public class Voter {
	private int id;        //id
	private String ip;     //ip address of the voter
	private Date voteDate; //recent vote date
	private Set<Resort> resorts = new HashSet<Resort>(); //voter's record list of resorts
	
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	@ManyToMany(cascade=CascadeType.MERGE,
			fetch=FetchType.EAGER)
	@JoinTable(name = "voter_resort", 
			joinColumns = { @JoinColumn(name = "v_id") }, 
			inverseJoinColumns = { @JoinColumn(name = "r_id") })
	public Set<Resort> getResorts() {
		return resorts;
	}
	public void setResorts(Set<Resort> resorts) {
		this.resorts = resorts;
	}
	public Date getVoteDate() {
		return voteDate;
	}
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}
	
	
}
