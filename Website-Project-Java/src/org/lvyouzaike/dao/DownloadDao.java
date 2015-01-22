package org.lvyouzaike.dao;

import java.util.List;

import org.lvyouzaike.model.Download;

public interface DownloadDao {
	public boolean add(Download download);  //add a new download item
	public boolean delete(int id);  //delete a download item
	public boolean update(Download download);  //update a download item
	public List<Download> getAll();  //get all the download items
	public List<Download> getAllByCount();   //get all the download items order by count
	public Download get(int id);  //get a download item with this id
}
