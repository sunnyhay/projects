package org.lvyouzaike.service;

import org.lvyouzaike.model.Mail;

public interface MailService {
	//输入：用户提交的邮件地址和自定义定制列表（逗号隔开的字符串）。
	//需要检查该用户的邮件地址是否存在。如果存在，那么更新其自定义定制列表；否则就是一次新的用户记录。
	//需要判断自定义定制列表为空。
	public boolean addMailSub(String address, String resorts);
	//在邮件定制查询页面，用于返回根据用户提交邮件地址查询到的相关记录。
	public Mail getByAddress(String address);  
	//取消邮件定制
	public boolean unsubMail(String address);
}
