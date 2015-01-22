package org.lvyouzaike.service.admin.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ExpressDao;
import org.lvyouzaike.model.ExpressModel;
import org.lvyouzaike.model.ExpressSupport;
import org.lvyouzaike.service.admin.ExpressAdminService;
import org.springframework.stereotype.Component;

@Component("expressSupportAdminService")
public class ExpressSupportAdminServiceImpl implements ExpressAdminService {
	private ExpressDao dao;

	public ExpressDao getDao() {
		return dao;
	}

	@Resource(name="expressSupportDao")
	public void setDao(ExpressDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addExpress(String name) {
		ExpressSupport e = new ExpressSupport();
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
