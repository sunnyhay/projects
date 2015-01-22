package org.lvyouzaike.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteAdviceDao;
import org.lvyouzaike.model.VoteAdvice;
import org.lvyouzaike.service.VoteAdviceService;
import org.springframework.stereotype.Component;

@Component("voteAdviceService")
public class VoteAdviceServiceImpl implements VoteAdviceService {
	private VoteAdviceDao dao;

	public VoteAdviceDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteAdviceDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addVoteAdvice(String nickname, String advice) {
		VoteAdvice a = new VoteAdvice();
		
		a.setNickname(nickname);
		a.setAdvice(advice);
		a.setAdviceDate(new Date());
		
		return dao.add(a);
	}

}
