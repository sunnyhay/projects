package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.VoteTemple;

public interface VoteTempleDao {
	public boolean check(String name);  //check if a Hill list item exists with this name
	public boolean add(VoteTemple item);  //add an item
	public boolean update(VoteTemple item);  //update an item
	public VoteTemple getById(int id);  //get by id
	public VoteTemple getByName(String name);  //get by name
	public List<VoteTemple> getAll();  //get all
}
