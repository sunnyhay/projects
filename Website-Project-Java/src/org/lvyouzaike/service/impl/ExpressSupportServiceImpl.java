package org.lvyouzaike.service.impl;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ExpressDao;
import org.lvyouzaike.model.ExpressSupport;
import org.lvyouzaike.service.ExpressService;
import org.springframework.stereotype.Component;

@Component("expressSupportService")
public class ExpressSupportServiceImpl implements ExpressService {
	private ExpressDao dao;

	public ExpressDao getDao() {
		return dao;
	}

	@Resource(name="expressSupportDao")
	public void setDao(ExpressDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean increExpressItem(String name, int item) {
		ExpressSupport e = (ExpressSupport) dao.getByName(name);
		int count = 0;
		
		switch(item){
		case 1: count = e.getItem1(); count++; e.setItem1(count); break;
		case 2: count = e.getItem2(); count++; e.setItem2(count); break;
		case 3: count = e.getItem3(); count++; e.setItem3(count); break;
		case 4: count = e.getItem4(); count++; e.setItem4(count); break;
		case 5: count = e.getItem5(); count++; e.setItem5(count); break;
		default: System.out.println("wrong!");
		}
		count = e.getTotal();
		e.setTotal(++count);
				
		return dao.update(e);
	}

}
