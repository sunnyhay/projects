package org.lvyouzaike.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailDao;
import org.lvyouzaike.dao.ResortDao;
import org.lvyouzaike.model.Mail;
import org.lvyouzaike.model.Resort;
import org.lvyouzaike.service.MailService;
import org.springframework.stereotype.Component;

@Component("mailService")
public class MailServiceImpl implements MailService {
	private MailDao dao;
	private ResortDao resortDao;
		
	public MailDao getDao() {
		return dao;
	}
	
	@Resource
	public void setDao(MailDao dao) {
		this.dao = dao;
	}
	
	public ResortDao getResortDao() {
		return resortDao;
	}

	@Resource(name="resortDao")
	public void setResortDao(ResortDao resortDao) {
		this.resortDao = resortDao;
	}

	@Override
	public boolean addMailSub(String address, String resorts) {
		Mail m = dao.getByAddress(address);
		
		if(m == null){//a new mail subscription
			m = new Mail();
			Set<Resort> rSet = new HashSet<Resort>();
			
			m.setAddress(address);
			m.setSubDate(new Date());
			m.setValidOrNot(true);
			
			if(resorts == null || resorts.equals("")){
				m.setSubOrNot(false);  //no customized resort
				//and do not need to set customized resorts
			}else{
				m.setSubOrNot(true);
				String r[] = resorts.split(",");
				for(String resort: r){
					Resort rt = resortDao.getByName(resort);
					rSet.add(rt);
					rt.getMails().add(m);
				}
				m.setResorts(rSet);
			}		
			return dao.add(m);
		}else{//update the user's subscription with new resorts
			Set<Resort> rSet = m.getResorts();
			if(!rSet.isEmpty()){//clear previous non-empty subscription resorts
				for(Resort r: rSet){
					r.getMails().remove(m);  //delete each resort's this mail subscription
				}
				m.getResorts().clear();  //clear this mail subscription's customized resorts
			}
			if(resorts == null){
				m.setSubOrNot(false);
			}else{
				m.setValidOrNot(true);
				m.setSubOrNot(true);
				Set<Resort> reSet = new HashSet<Resort>();
				String r[] = resorts.split(",");
				for(String resort: r){
					Resort rt = resortDao.getByName(resort);
					reSet.add(rt);
					rt.getMails().add(m);
				}
				m.setResorts(reSet);
			}
		}	return dao.update(m);
		
	}

	@Override
	public Mail getByAddress(String address) {
		return dao.getByAddress(address);
	}

	@Override
	public boolean unsubMail(String address) {
		if(dao.check(address)){
			Mail m = dao.getByAddress(address);
			
			if(m.isSubOrNot()){//customized resorts should be dealt with
				Set<Resort> resorts = m.getResorts();
				for(Resort r: resorts){
					r.getMails().remove(m);
				}
				m.getResorts().clear();
				m.setSubOrNot(false);
			}
			m.setValidOrNot(false);		
			return dao.update(m);
		}
		return false;		
	}

}
