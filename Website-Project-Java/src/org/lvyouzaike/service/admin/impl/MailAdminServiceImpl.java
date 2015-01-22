package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailDao;
import org.lvyouzaike.model.Mail;
import org.lvyouzaike.service.admin.MailAdminService;
import org.springframework.stereotype.Component;

@Component("mailAdminService")
public class MailAdminServiceImpl implements MailAdminService {
	private MailDao dao;
	
	public MailDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(MailDao dao) {
		this.dao = dao;
	}

	@Override
	public List<Mail> getAll() {
		return dao.getAll();
	}

	@Override
	public List<Mail> getValidAll() {
		return dao.getValidAll();
	}

	@Override
	public List<Mail> getBySubTime(Date from, Date to) {
		return dao.getBySubTime(from, to);
	}

	@Override
	public boolean isAllSentAfter(Date current) {
		return dao.isAllSentAfter(current);
	}

	@Override
	public int numOfCustomContent() {
		return dao.numOfCustomContent();
	}

	@Override
	public int numOfValid() {
		return dao.numOfValid();
	}

	@Override
	public boolean updateMail(Mail mail) {
		return dao.update(mail);
	}

}
