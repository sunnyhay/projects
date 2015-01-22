package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.VoteHill;

public interface VoteHillDao {
	public boolean check(String name);  //check if a Hill list item exists with this name
	public boolean add(VoteHill hill);  //add a new hill
	public boolean update(VoteHill hill);  //update a hill
	public VoteHill getById(int id);  //get by id
	public VoteHill getByName(String name);  //get by name
	public List<VoteHill> getAll();  //get all
}
