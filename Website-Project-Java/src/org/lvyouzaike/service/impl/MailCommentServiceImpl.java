package org.lvyouzaike.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailCommentDao;
import org.lvyouzaike.model.MailComment;
import org.lvyouzaike.service.MailCommentService;
import org.springframework.stereotype.Component;

@Component("mailCommentService")
public class MailCommentServiceImpl implements MailCommentService {
	private MailCommentDao dao;	

	public MailCommentDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(MailCommentDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addMailComment(String address, String formatreason,
			String customreason) {
		MailComment c = new MailComment();
		
		c.setAddress(address);
		c.setCustomreason(customreason);
		c.setFormatreason(formatreason);
		c.setUnsubDate(new Date());
		dao.add(c);
		
		return true;
	}

}
