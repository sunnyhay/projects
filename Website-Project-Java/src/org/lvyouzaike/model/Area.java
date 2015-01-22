package org.lvyouzaike.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*
 * the entity of administrative area for resorts. Corresponding to table 'area'
 * Belong to common list operations.
 * one-to-Many relation to table (object) resort
 */
@Entity
@Table(name="area")
public class Area {
	private int id;          //id
	private String name;     //name
	private Set<Resort> resorts = new HashSet<Resort>();
	
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
	@OneToMany(mappedBy="area",
			cascade=CascadeType.MERGE,
			fetch=FetchType.EAGER)
	public Set<Resort> getResorts() {
		return resorts;
	}
	public void setResorts(Set<Resort> resorts) {
		this.resorts = resorts;
	}
	
}
