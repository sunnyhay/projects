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
 * the entity to represent an individual list record 寺庙宰客榜 with vote open. Corresponding to table 'vote_temple_record'
 * Many-to-many mapping to table 'vote_temple'
 * Belong to individual list operations, especially for vote open.
 */
@Entity
@Table(name="vote_temple_record")
public class VoteTempleRecord{
	private int id;        //id
	private String ip;     //ip address of the voter
	private Date voteDate; //recent vote time
	private Set<VoteTemple> voteTemple = new HashSet<VoteTemple>();  //many-to-many mapping
	
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
	public Date getVoteDate() {
		return voteDate;
	}
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}
	@ManyToMany(cascade={CascadeType.MERGE},
			fetch=FetchType.EAGER)
	@JoinTable(name = "vote_temple_record_mapping", 
	joinColumns = { @JoinColumn(name = "record_id") }, 
	inverseJoinColumns = { @JoinColumn(name = "temple_id") })
	public Set<VoteTemple> getVoteTemple() {
		return voteTemple;
	}
	public void setVoteTemple(Set<VoteTemple> voteTemple) {
		this.voteTemple = voteTemple;
	}	
}
