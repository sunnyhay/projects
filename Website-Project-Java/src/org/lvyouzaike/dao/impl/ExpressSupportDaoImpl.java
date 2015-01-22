package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ExpressDao;
import org.lvyouzaike.model.ExpressModel;
import org.lvyouzaike.model.ExpressSupport;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("expressSupportDao")
public class ExpressSupportDaoImpl implements ExpressDao {
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
		List<ExpressModel> es = ht.find("from ExpressSupport e where e.name = '" + name + "'");
		if(es != null && es.size() > 0)
			return es.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExpressModel> getAll() {
		return ht.find("from ExpressSupport");
	}

	@Override
	public boolean update(ExpressModel e) {
		ht.update(e);
		return true;
	}

	@Override
	public int getTotal(String name) {
		ExpressSupport e = (ExpressSupport) getByName(name);
		return e.getTotal();
	}

}
