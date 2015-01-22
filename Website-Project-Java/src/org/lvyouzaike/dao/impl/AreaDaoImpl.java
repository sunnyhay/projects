package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.AreaDao;
import org.lvyouzaike.model.Area;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("areaDao")
public class AreaDaoImpl implements AreaDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean check(String name) {
		@SuppressWarnings("unchecked")
		List<Area> areas = ht.find("from Area a where a.name = '" + name + "'");
		if(areas != null && areas.size() > 0)
			return true;
		return false;
	}

	@Override
	public boolean add(Area area) {
		if(!check(area.getName())){
			ht.merge(area);
			return true;
		}			
		return false;
	}

	@Override
	public boolean update(Area area) {
		ht.update(area);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Area> getAll() {
		return (List<Area>) ht.find("from Area");
	}

	@Override
	public Area get(String name) {
		@SuppressWarnings("unchecked")
		List<Area> areas = ht.find("from Area a where a.name = '" + name + "'");
		if(areas != null && areas.size() > 0)
			return areas.get(0);
		return null;
	}

}
