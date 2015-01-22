package org.lvyouzaike.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/*
 * the entity for resort record. Corresponding to table 'resort'.
 * Belong to common list operations.
 * Many-to-many relation to tables (objects) voter and mail(email_sub)
 * Many-to-One relation to table (object) area
 */
@Entity
@Table(name="resort")
public class Resort {
	private int id;                    //id
	private String name;               //name
	private String description;               //description
	private byte[] img;                //image
	private Boolean isInterCity;       //is an international city?
	private Boolean isInterResort;     //is an international resort?
	private Boolean isDomesticCity;    //is a domestic city?
	private Boolean isDomesticResort;  //is a domestic resort?
	private Set<Mail> mails = new HashSet<Mail>();  //many-to-many mapping
	private Set<Voter> voters = new HashSet<Voter>();  //many-to-many mapping
	private Area area;  //many-to-one mapping
	
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
	public byte[] getImg() {
		return img;
	}
	public void setImg(byte[] img) {
		this.img = img;
	}
	public Boolean getIsInterCity() {
		return isInterCity;
	}
	public void setIsInterCity(Boolean isInterCity) {
		this.isInterCity = isInterCity;
	}
	public Boolean getIsInterResort() {
		return isInterResort;
	}
	public void setIsInterResort(Boolean isInterResort) {
		this.isInterResort = isInterResort;
	}
	public Boolean getIsDomesticCity() {
		return isDomesticCity;
	}
	public void setIsDomesticCity(Boolean isDomesticCity) {
		this.isDomesticCity = isDomesticCity;
	}
	public Boolean getIsDomesticResort() {
		return isDomesticResort;
	}
	public void setIsDomesticResort(Boolean isDomesticResort) {
		this.isDomesticResort = isDomesticResort;
	}
	@ManyToMany(mappedBy="resorts",
			cascade=CascadeType.MERGE,
			fetch=FetchType.EAGER)
	public Set<Mail> getMails() {
		return mails;
	}
	public void setMails(Set<Mail> mails) {
		this.mails = mails;
	}
	@ManyToMany(mappedBy="resorts",
			cascade=CascadeType.MERGE,
			fetch=FetchType.EAGER)
	public Set<Voter> getVoters() {
		return voters;
	}
	public void setVoters(Set<Voter> voters) {
		this.voters = voters;
	}
	@ManyToOne(cascade=CascadeType.MERGE)
	@JoinColumn(name="areaId")
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
}
