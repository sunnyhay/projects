package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.Voter;

public interface VoterAdminService {
	public boolean delete(String ip);  //delete a voter by ip
	public Voter getByIp(String ip);  //get a voter by IP address
	public List<Voter> getAll();  //get all the voter records
	public List<Voter> getAfter(Date from, Date to);  //get all the vote records submitted within an interval
}
