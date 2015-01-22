package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.Voter;

public interface VoterDao {
	public boolean add(Voter voter);  //add a new voter
	public boolean update(Voter voter);  //update a vote record
	public boolean delete(Voter voter);  //delete a vote record
	public Voter getByIp(String ip);  //get a voter by IP address
	public List<Voter> getAll();  //get all the voter records
	public List<Voter> getAfter(Date from, Date to);  //get all the voter records submitted within an interval
}
