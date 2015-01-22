package org.lvyouzaike.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/*
 * the entity to represent the individual list 名山宰客榜 with vote open. Corresponding to table 'vote_hill'
 * many-to-many mapping to table 'vote_hill_record'
 * Belong to individual list operations, especially for vote open.
 */
@Entity
@Table(name="vote_hill")
public class VoteHill{
	private int id;          //id
	private String name;     //name of each item in the list
	private String description;     //description of the item
	private Set<VoteHillRecord> voteHillRecord = new HashSet<VoteHillRecord>();  //many-to-many mapping
		
	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@ManyToMany(mappedBy="voteHill",
			cascade=CascadeType.MERGE,
			fetch=FetchType.EAGER)	
	public Set<VoteHillRecord> getVoteHillRecord() {
		return voteHillRecord;
	}
	public void setVoteHillRecord(Set<VoteHillRecord> voteHillRecord) {
		this.voteHillRecord = voteHillRecord;
	}
}
