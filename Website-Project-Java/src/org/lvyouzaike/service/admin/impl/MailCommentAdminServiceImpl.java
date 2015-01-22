package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.MailCommentDao;
import org.lvyouzaike.model.MailComment;
import org.lvyouzaike.service.admin.MailCommentAdminService;
import org.springframework.stereotype.Component;

@Component("mailCommentAdminService")
public class MailCommentAdminServiceImpl implements MailCommentAdminService {
	private MailCommentDao dao;

	public MailCommentDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(MailCommentDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean delete(int id) {
		dao.delete(id);
		return true;
	}

	@Override
	public List<MailComment> getAll() {
		return dao.getAll();
	}

	@Override
	public List<MailComment> get(Date current) {
		return dao.get(current);
	}

}
