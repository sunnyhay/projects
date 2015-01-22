package org.lvyouzaike.service.admin;

import java.util.List;

import org.lvyouzaike.model.ExpressModel;

public interface ExpressAdminService {
	public boolean addExpress(String name);  //add a new ExpressModel object with this name
	public ExpressModel getExpressByName(String name);
	public List<ExpressModel> getAllExpress();
	public int getExpressTotalCount(String name);  //get the express count for this name
}
