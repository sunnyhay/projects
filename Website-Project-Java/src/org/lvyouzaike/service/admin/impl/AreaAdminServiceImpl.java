package org.lvyouzaike.service.admin.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.lvyouzaike.dao.AreaDao;
import org.lvyouzaike.model.Area;
import org.lvyouzaike.model.Resort;
import org.lvyouzaike.service.admin.AreaAdminService;
import org.springframework.stereotype.Component;

@Component("areaAdminService")
public class AreaAdminServiceImpl implements AreaAdminService {
	private AreaDao dao;

	public AreaDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(AreaDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkArea(String name) {
		return dao.check(name);
	}

	@Override
	public boolean addArea(String name) {
		Area a = new Area();
		a.setName(name);
		return dao.add(a);
	}	

	@Override
	public List<Area> getAll() {
		return dao.getAll();
	}

	@Override
	public Area getByName(String name) {
		return dao.get(name);
	}

	@Override
	public Set<Resort> getResorts(String name) {
		return dao.get(name).getResorts();
	}

}
