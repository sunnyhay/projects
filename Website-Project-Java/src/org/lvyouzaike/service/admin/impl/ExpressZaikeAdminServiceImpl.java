package org.lvyouzaike.service.admin.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ExpressDao;
import org.lvyouzaike.model.ExpressModel;
import org.lvyouzaike.model.ExpressZaike;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.springframework.stereotype.Component;

@Component("expressZaikeAdminService")
public class ExpressZaikeAdminServiceImpl implements ExpressAdminService {
	private ExpressDao dao;

	public ExpressDao getDao() {
		return dao;
	}

	@Resource(name="expressZaikeDao")
	public void setDao(ExpressDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addExpress(String name) {
		ExpressZaike e = new ExpressZaike();
		e.setName(name);
		
		return dao.add(e);
	}

	@Override
	public ExpressModel getExpressByName(String name) {
		return dao.getByName(name);
	}

	@Override
	public List<ExpressModel> getAllExpress() {
		return dao.getAll();
	}

	@Override
	public int getExpressTotalCount(String name) {
		return dao.getTotal(name);
	}

}
