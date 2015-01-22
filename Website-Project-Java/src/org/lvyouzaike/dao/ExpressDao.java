package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.ExpressModel;

public interface ExpressDao {
	public boolean add(ExpressModel express);  //add a new ExpressModel object
	public ExpressModel getByName(String name);
	public List<ExpressModel> getAll();
	public boolean update(ExpressModel e);  //update an item 
	public int getTotal(String name);  //get the total express count for one item
}
