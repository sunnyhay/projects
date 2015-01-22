package org.lvyouzaike.service.admin;

import java.util.List;

import org.lvyouzaike.model.VoteTemple;

public interface VoteTempleAdminService {
	public boolean checkTemple(String name);  //check if a Temple list item exists with this name
	public boolean addTemple(String name, String description);  //add a new temple
	public boolean updateTempleDesc(String name, String description);  //update a temple's description
	public VoteTemple getById(int id);  //get by id
	public VoteTemple getByName(String name);  //get by name
	public List<VoteTemple> getTemplesOrderByVotes();  //get all the temples order by votes on them decreasing
	public List<VoteTemple> getAllTemples();  //get all the temples
}
