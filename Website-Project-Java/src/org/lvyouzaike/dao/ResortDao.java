package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.Resort;

public interface ResortDao {
	public boolean check(String name);  //check if a resort with this name exists
	public boolean add(Resort resort);  //add a new resort
	public boolean update(Resort resort);  //update a resort
	public List<Resort> getAll();  //get all resorts
	public Resort getById(int id);  //get a resort by id
	public Resort getByName(String name);  //get a resort by name
	public List<Resort> getInterCities();  //get all international cities
	public List<Resort> getInterResorts();  //get all international resorts
	public List<Resort> getCities();  //get all domestic cities
	public List<Resort> getResorts();  //get all domestic resorts
	public int numOfInterCities();  //get the number of international cities;
	public int numOfInterResorts();  //get the number of international resorts;
	public int numOfCities();  //get the number of domestic cities;
	public int numOfResorts();  //get the number of domestic resorts;
	public long getMailSubCount(String name);  //get the mail subscription count for a resort with this name
	public long getVoteCount(String name);  //get the vote count for a resort with this name
	public String getArea(String name);  //get the name of area to which the resort with this name belongs  
}
