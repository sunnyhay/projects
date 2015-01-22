package org.lvyouzaike.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailDao;
import org.lvyouzaike.model.Mail;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("mailDao")
public class MailDaoImpl implements MailDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean check(String address) {
		@SuppressWarnings("unchecked")
		List<Mail> mails = ht.find("from Mail m where m.address = '" + address + "'");
		if(mails != null && mails.size() > 0)
			return true;
		return false;
	}

	@Override
	//if the email address exists, return false; otherwise true and add it. 
	public boolean add(Mail mail) {
		if(!check(mail.getAddress())){
			ht.merge(mail);
			return true;
		}			
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.lvyouzaike.dao.MailDao#update(org.lvyouzaike.model.Mail)
	 * you have to read the mail object with a specific id and then substitute the mail object with necessary data. 
	 */
	@Override
	public boolean update(Mail mail) {
		ht.update(mail);
		return true;
	}
	
	@Override
	public Mail getById(int id){
		return ht.get(Mail.class, id);
	}
	
	@Override
	public Mail getByAddress(String address){
		@SuppressWarnings("unchecked")
		List<Mail> mails = ht.find("from Mail m where m.address = '" + address + "'");
		if(mails != null && mails.size() > 0)
			return mails.get(0);
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mail> getAll() {
		return (List<Mail>) ht.find("from Mail");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mail> getValidAll() {
		return (List<Mail>) ht.find("from Mail m where m.validOrNot = true");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Mail> getBySubTime(Date from, Date to) {
		return (List<Mail>) ht.find("from Mail m where m.subDate > ? and m.subDate < ?", from, to);
	}

	@Override
	public boolean isAllSentAfter(Date current) {
		@SuppressWarnings("unchecked")
		List<Mail> mails = ht.find("from Mail m where m.subDate < ?", current);
		if(mails != null && mails.size() > 0)
			return false;
		return true;
	}

	@Override
	public int numOfCustomContent() {
		@SuppressWarnings("unchecked")
		List<Long> sum = ht.find("select count(*) from Mail m where m.subOrNot = true");
		long t = sum.get(0);
		int p = (int) t;
		return p;
	}

	@Override
	public int numOfValid() {
		@SuppressWarnings("unchecked")
		List<Long> sum = ht.find("select count(*) from Mail m where m.validOrNot = true");
		long t = sum.get(0);
		int p = (int) t;
		return p;
	}

	

}
