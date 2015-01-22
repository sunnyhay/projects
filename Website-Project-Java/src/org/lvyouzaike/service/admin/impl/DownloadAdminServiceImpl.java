package org.lvyouzaike.service.admin.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.lvyouzaike.dao.DownloadDao;
import org.lvyouzaike.model.Download;
import org.lvyouzaike.service.admin.DownloadAdminService;
import org.springframework.stereotype.Component;

@Component("downloadAdminService")
public class DownloadAdminServiceImpl implements DownloadAdminService {
	private DownloadDao dao;

	public DownloadDao getDao() {
		return dao;
	}

	@Resource
	public void setDao(DownloadDao dao) {
		this.dao = dao;
	}

	@Override
	public boolean addItem(String name, String version, String description,
			String link, int count) {
		Download d = new Download();
		d.setCount(count);
		d.setDescription(description);
		d.setLink(link);
		d.setName(name);
		d.setVersion(version);
		d.setUpdateTime(new Date());
		
		return dao.add(d);
	}

	@Override
	public boolean deleteItem(int id) {
		return dao.delete(id);
	}

	@Override
	public boolean updateItem(Download download) {
		return dao.update(download);
	}

	@Override
	public List<Download> getAllItems() {
		return dao.getAll();
	}

	@Override
	public List<Download> getItemsByCount() {
		return dao.getAllByCount();
	}

	@Override
	public Download getItemById(int id) {
		return dao.get(id);
	}

	@Override
	public Map<Integer, Integer> getItemCounts() {
		Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
		List<Download> items = getAllItems();
		for(Download d: items){
			counts.put(d.getId(), d.getCount());
		}
		return counts;
	}

	@Override
	public boolean incrementCount(int id) {
		Download d = getItemById(id);
		d.setCount(d.getCount()+1);
		d.setUpdateTime(new Date());  //meanwhile update the most recent download date.
		return updateItem(d);
	}

}
