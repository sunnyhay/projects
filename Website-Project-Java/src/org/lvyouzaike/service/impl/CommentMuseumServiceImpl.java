package org.lvyouzaike.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.lvyouzaike.dao.CommentDao;
import org.lvyouzaike.model.CommentMuseum;
import org.lvyouzaike.service.CommentService;
import org.springframework.stereotype.Component;

@Component("commentMuseumService")
public class CommentMuseumServiceImpl implements CommentService{
	private CommentDao dao;

	public CommentDao getDao() {
		return dao;
	}

	@Resource(name="commentMuseumDao")
	public void setDao(CommentDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addComment(String nickname, String comment) {
		CommentMuseum c = new CommentMuseum();
		c.setComment(comment);
		c.setNickname(nickname);
		c.setCommentDate(new Date());
		dao.add(c);
		return true;
	}
	
}
