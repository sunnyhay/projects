package org.lvyouzaike.action;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.lvyouzaike.service.admin.DownloadAdminService;
import org.lvyouzaike.service.admin.impl.DownloadAdminServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Component("downloadAction")
public class DownloadAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String[] FILES = new String[]{"旅游风险红宝书08022012.doc", "旅游风险红宝书08022012.pdf", 
													"旅游风险红宝书-大话西游版08022012.doc", "旅游风险红宝书-大话西游版08022012.pdf",
													"旅游安全红宝书08022012.doc", "旅游安全红宝书08022012.pdf",
													"旅游安全红宝书-大话西游版08022012.doc", "旅游安全红宝书-大话西游版08022012.pdf",
													"旅游维权指南09172012.doc", "旅游维权指南09172012.pdf"};
	private int num;
	private String filename;
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public InputStream getDownloadFile() throws UnsupportedEncodingException{
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
		DownloadAdminService downloadAdminService = (DownloadAdminServiceImpl) applicationContext.getBean("downloadAdminService");
		
		downloadAdminService.incrementCount(num);  //increment the corresponding download item's count
		
		//update the application's download counts info
		String download = "download";
		ActionContext context = ActionContext.getContext();
		Map<String, Object> map = context.getApplication();
		
		map.put(download + num , (Integer) map.get(download + num) + 1);
		
		if(num > 0){
			filename = FILES[num-1];
			filename = new String(filename.getBytes("utf-8"),"8859_1");
			return ServletActionContext.getServletContext().getResourceAsStream("files/" + FILES[num-1]);
		}
		else{
			return null;
		}
		
	}
	
	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}
