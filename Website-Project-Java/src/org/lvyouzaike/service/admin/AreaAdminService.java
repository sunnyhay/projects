package org.lvyouzaike.service.admin;

import java.util.List;
import java.util.Set;

import org.lvyouzaike.model.Area;
import org.lvyouzaike.model.Resort;

public interface AreaAdminService {
	public boolean checkArea(String name);  //check whether an area with this name exists 
	public boolean addArea(String name);  //add a new area
	public List<Area> getAll();     //get all the areas
	public Area getByName(String name);  //get an area with this name
	public Set<Resort> getResorts(String name);  //get all the resorts belonging to the area
}
