package org.lvyouzaike.service.admin;

import java.util.List;

import org.lvyouzaike.model.VoteHill;

public interface VoteHillAdminService {
	public boolean checkHill(String name);  //check if a Hill list item exists with this name
	public boolean addHill(String name, String description);  //add a new hill
	public boolean updateHillDesc(String name, String description);  //update a hill's description
	public VoteHill getById(int id);  //get by id
	public VoteHill getByName(String name);  //get by name
	public List<VoteHill> getHillsOrderByVotes();  //get all the hills order by votes on them decreasing
	public List<VoteHill> getAllHills();  //get all the hills
}
