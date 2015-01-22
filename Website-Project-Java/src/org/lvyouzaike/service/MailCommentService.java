package org.lvyouzaike.service;

public interface MailCommentService {
	//用户提交退订时，会同步提交该用户推定调查内容
	public boolean addMailComment(String address, String formatreason, String customreason);
}
