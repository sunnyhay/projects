package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.VoteAdviceDao;
import org.lvyouzaike.model.VoteAdvice;
import org.lvyouzaike.service.admin.VoteAdviceAdminService;
import org.springframework.stereotype.Component;

@Component("voteAdviceAdminService")
public class VoteAdviceAdminServiceImpl implements VoteAdviceAdminService {
	private VoteAdviceDao dao;

	public VoteAdviceDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(VoteAdviceDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean deleteVoteAdvice(int id) {
		return dao.delete(id);
	}

	@Override
	public List<VoteAdvice> getAll() {
		return dao.getAll();
	}

	@Override
	public List<VoteAdvice> getAfter(Date current) {
		return dao.getAfter(current);
	}

}
