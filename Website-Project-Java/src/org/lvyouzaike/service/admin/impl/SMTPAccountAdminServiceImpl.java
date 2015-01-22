package org.lvyouzaike.service.admin.impl;

import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;

import org.lvyouzaike.dao.SMTPAccountDao;
import org.lvyouzaike.model.SMTPAccount;
import org.lvyouzaike.service.admin.SMTPAccountAdminService;
import org.lvyouzaike.util.EncrypUtil;
import org.springframework.stereotype.Component;

@Component("smtpAccountAdminService")
public class SMTPAccountAdminServiceImpl implements SMTPAccountAdminService {
	private SMTPAccountDao dao;
	
	public SMTPAccountDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(SMTPAccountDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean checkPwd(String username, String password) throws Exception {
		return dao.check(username, password);
	}

	@Override
	public boolean addAccount(String username, String password) throws NoSuchAlgorithmException {
		SMTPAccount account = new SMTPAccount();
		account.setUsername(username);
		account.setPassword(EncrypUtil.md5Digest(password));
		return dao.add(account);
	}

	@Override
	public boolean updateAccount(String username, String newUsername, String newPassword) throws NoSuchAlgorithmException {
		SMTPAccount account = dao.get(username);
		account.setUsername(newUsername);
		account.setPassword(EncrypUtil.md5Digest(newPassword));
		return dao.update(account);
	}
}
