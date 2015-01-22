package org.lvyouzaike.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.DownloadDao;
import org.lvyouzaike.model.Download;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

/*
 * hibernate implementation of DownloadDao using HibernateTemplate util 
 */

@Component("downloadDao")
public class DownloadDaoImpl implements DownloadDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}

	@Override
	public boolean add(Download download) {
		ht.save(download);
		return true;
	}

	@Override
	public boolean delete(int id) {
		Download download = new Download();
		download.setId(id);
		ht.delete(download);
		return true;
	}

	@Override
	public boolean update(Download download) {
		ht.update(download);
		return true;
	}

	@Override
	public List<Download> getAll() {
		return (List<Download>) getAllDownloads();
	}

	/*
	 * this getAllDownloads() refers to page 711 in spring reference book.
	 */
	@SuppressWarnings("unchecked")
	public Collection<Download> getAllDownloads(){
		return this.ht.find("from Download");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Download> getAllByCount(){
		return this.ht.find("from Download d order by d.count");
	}
	
	@Override
	public Download get(int id) {
		return ht.get(Download.class, id);
	}
}
