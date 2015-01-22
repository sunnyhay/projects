package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ExpressDao;
import org.lvyouzaike.model.ExpressAttitude;
import org.lvyouzaike.model.ExpressModel;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("expressAttitudeDao")
public class ExpressAttitudeDaoImpl implements ExpressDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}

	@Override
	public boolean add(ExpressModel express) {
		ht.save(express);
		return true;
	}

	@Override
	public ExpressModel getByName(String name) {
		@SuppressWarnings("unchecked")
		List<ExpressModel> es = ht.find("from ExpressAttitude a where a.name = '" + name + "'");
		if(es != null && es.size() > 0)
			return es.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpressModel> getAll() {
		return ht.find("from ExpressAttitude");
	}

	@Override
	public boolean update(ExpressModel e) {
		ht.update(e);
		return true;
	}
	
	@Override
	public int getTotal(String name) {
		ExpressAttitude e = (ExpressAttitude) getByName(name);
		return e.getTotal();
	}	
}
