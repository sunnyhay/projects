package org.lvyouzaike.service.admin;

import java.util.List;
import java.util.Map;

import org.lvyouzaike.model.Download;

public interface DownloadAdminService {
	public boolean addItem(String name, String version, String description, String link, int count);  //add a new download item
	public boolean deleteItem(int id);  //delete a download item
	public boolean updateItem(Download download);  //update a download item
	public List<Download> getAllItems();  //get all the download items
	public List<Download> getItemsByCount();  //get all the download items order by download count decreasing
	public Map<Integer, Integer> getItemCounts();  //get all the download items' count
	public boolean incrementCount(int id);  //increment the download count of item with this id
	public Download getItemById(int id);  //get a download item with this id
}
