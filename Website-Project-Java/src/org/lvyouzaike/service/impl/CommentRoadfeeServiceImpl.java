package org.lvyouzaike.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.lvyouzaike.dao.CommentDao;
import org.lvyouzaike.model.CommentRoadfee;
import org.lvyouzaike.service.CommentService;
import org.springframework.stereotype.Component;

@Component("commentRoadfeeService")
public class CommentRoadfeeServiceImpl implements CommentService{
	private CommentDao dao;

	public CommentDao getDao() {
		return dao;
	}

	@Resource(name="commentRoadfeeDao")
	public void setDao(CommentDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addComment(String nickname, String comment) {
		CommentRoadfee c = new CommentRoadfee();
		c.setComment(comment);
		c.setNickname(nickname);
		c.setCommentDate(new Date());
		dao.add(c);
		return true;
	}
	
}
