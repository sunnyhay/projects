package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.SMTPAccountDao;
import org.lvyouzaike.model.SMTPAccount;
import org.lvyouzaike.util.EncrypUtil;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("smtpAccountDao")
public class SMTPAccountDaoImpl implements SMTPAccountDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}
	
	@Override
	public boolean check(String username, String password) throws Exception {
		@SuppressWarnings("unchecked")
		List<SMTPAccount> accounts = ht.find("from SMTPAccount a where a.username = '" + username + "'");
		if(accounts.size() > 0){
			SMTPAccount account = accounts.get(0);
			//System.out.println("account username: " + account.getUsername() + " with password: " + account.getPassword());
			//System.out.println("new digest: " + EncrypUtil.md5Digest(password));
			if(EncrypUtil.md5Digest(password).equals(account.getPassword()))
				return true;
		}				
		return false;
	}
	
	@Override
	public SMTPAccount get(String username){
		@SuppressWarnings("unchecked")
		List<SMTPAccount> accounts = ht.find("from SMTPAccount a where a.username = '" + username + "'");
		if(accounts.size() > 0)
			return accounts.get(0);
		return null;
	}

	@Override
	public boolean add(SMTPAccount account) {
		ht.save(account);
		return true;
	}

	@Override
	public boolean update(SMTPAccount account) {
		ht.update(account);
		return true;
	}

}
