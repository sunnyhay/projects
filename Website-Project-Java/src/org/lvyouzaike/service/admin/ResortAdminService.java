package org.lvyouzaike.service.admin;

import java.util.List;

import org.lvyouzaike.model.Resort;

public interface ResortAdminService {
	public boolean checkResort(String name);  //check if a resort with this name exists
	//加入一个新的旅游目的地（景点或者城市），其中identity是这四个字符串中的一个：intercity,interresort,domesticresort,domesticcity 用于确定其四个属性之一。
	public boolean addResort(String name, String description, byte[] img, String identity, String area);  
	//update description 
	public boolean updateDesc(String name, String description);
	//update img
	public boolean updateImg(String name, byte[] img);
		
	public List<Resort> getAll();  //get all resorts
	public Resort getResortById(int id);  //get a resort by id
	public Resort getResortByName(String name);  //get a resort by name
	
	//order by count of mail subscriptions decreasing
	public List<Resort> getInterCitiesByMailSub();  //get all international cities
	public List<Resort> getInterResortsByMailSub();  //get all international resorts
	public List<Resort> getCitiesByMailSub();  //get all domestic cities
	public List<Resort> getResortsByMailSub();  //get all domestic resorts
	
	//order by count of votes decreasing
	public List<Resort> getInterCitiesByVote();  //get all international cities
	public List<Resort> getInterResortsByVote();  //get all international resorts
	public List<Resort> getCitiesByVote();  //get all domestic cities
	public List<Resort> getResortsByVote();  //get all domestic resorts
	
	public int numOfInterCities();  //get the number of international cities;
	public int numOfInterResorts();  //get the number of international resorts;
	public int numOfCities();  //get the number of domestic cities;
	public int numOfResorts();  //get the number of domestic resorts;
	
	public long getMailSubCount(String name);  //get the mail subscription count for a resort with this name
	public long getVoteCount(String name);  //get the vote count for a resort with this name
	public String getArea(String name);  //get the name of area to which the resort with this name belongs
}
