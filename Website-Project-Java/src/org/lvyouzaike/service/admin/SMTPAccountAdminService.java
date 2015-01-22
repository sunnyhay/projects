package org.lvyouzaike.service.admin;

import java.security.NoSuchAlgorithmException;

public interface SMTPAccountAdminService {
	public boolean checkPwd(String username, String password) throws Exception;        //check if MD5 digest of password matches
	public boolean addAccount(String username, String password) throws NoSuchAlgorithmException;      //add a new account
	public boolean updateAccount(String username, String newUsername, String newPassword) throws NoSuchAlgorithmException;                    //update the account with this username
}
