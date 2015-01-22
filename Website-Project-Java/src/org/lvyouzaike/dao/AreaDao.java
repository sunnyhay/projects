package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.Area;

public interface AreaDao {
	public boolean check(String name);  //check whether an area with this name exists 
	public boolean add(Area area);  //add a new area
	public boolean update(Area area);  //update an area's content, usually the set of resorts
	public List<Area> getAll();     //get all the areas
	public Area get(String name);  //get an area with this name
}
