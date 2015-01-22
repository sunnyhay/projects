package org.lvyouzaike.dao;

import org.lvyouzaike.model.SMTPAccount;

public interface SMTPAccountDao {
	public boolean check(String username, String password) throws Exception;             //check if the password matches under the same username
	public boolean add(SMTPAccount account);                            //add a new SMTP account
	public boolean update(SMTPAccount account);                         //update this account with new digested password
	public SMTPAccount get(String username);                            //get an account with this username
}
