package org.lvyouzaike.service.admin.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.AreaDao;
import org.lvyouzaike.dao.ResortDao;
import org.lvyouzaike.model.Area;
import org.lvyouzaike.model.Resort;
import org.lvyouzaike.service.admin.ResortAdminService;
import org.lvyouzaike.util.ResortMailSubComparator;
import org.lvyouzaike.util.ResortVoteComparator;
import org.springframework.stereotype.Component;

@Component("resortAdminService")
public class ResortAdminServiceImpl implements ResortAdminService {
	private ResortDao dao;
	private AreaDao areaDao;
	
	public ResortDao getDao() {
		return dao;
	}

	@Resource(name="resortDao")
	public void setDao(ResortDao dao) {
		this.dao = dao;
	}

	public AreaDao getAreaDao() {
		return areaDao;
	}

	@Resource(name="areaDao")
	public void setAreaDao(AreaDao areaDao) {
		this.areaDao = areaDao;
	}

	@Override
	public boolean checkResort(String name) {
		return dao.check(name);
	}

	@Override
	public boolean addResort(String name, String description, byte[] img,
			String identity, String area) {
		Resort r = new Resort();
		Area a = areaDao.get(area);
		
		r.setName(name);
		r.setDescription(description);
		r.setImg(img);
		r.setArea(a);
		
		if(identity.equals("intercity"))
			r.setIsInterCity(true);
		else if(identity.equals("interresort"))
			r.setIsInterResort(true);
		else if(identity.equals("domesticcity"))
			r.setIsDomesticCity(true);
		else
			r.setIsDomesticResort(true);
		
		return dao.add(r);
	}

	@Override
	public boolean updateDesc(String name, String description) {
		Resort r = dao.getByName(name);
		r.setDescription(description);
		return dao.update(r);
	}

	@Override
	public boolean updateImg(String name, byte[] img) {
		Resort r = dao.getByName(name);
		r.setImg(img);
		return dao.update(r);
	}
	
	@Override
	public List<Resort> getAll() {
		return dao.getAll();
	}

	@Override
	public Resort getResortById(int id) {
		return dao.getById(id);
	}

	@Override
	public Resort getResortByName(String name) {
		return dao.getByName(name);
	}

	@Override
	public List<Resort> getInterCitiesByMailSub() {
		List<Resort> resorts = dao.getInterCities();
		Collections.sort(resorts, new ResortMailSubComparator());
		return resorts;
	}

	@Override
	public List<Resort> getInterResortsByMailSub() {
		List<Resort> resorts = dao.getInterResorts();
		Collections.sort(resorts, new ResortMailSubComparator());
		return resorts;
	}

	@Override
	public List<Resort> getCitiesByMailSub() {
		List<Resort> resorts = dao.getCities();
		Collections.sort(resorts, new ResortMailSubComparator());
		return resorts;
	}

	@Override
	public List<Resort> getResortsByMailSub() {
		List<Resort> resorts = dao.getResorts();
		Collections.sort(resorts, new ResortMailSubComparator());
		return resorts;
	}

	@Override
	public List<Resort> getInterCitiesByVote() {
		List<Resort> resorts = dao.getInterCities();
		Collections.sort(resorts, new ResortVoteComparator());
		return resorts;
	}

	@Override
	public List<Resort> getInterResortsByVote() {
		List<Resort> resorts = dao.getInterResorts();
		Collections.sort(resorts, new ResortVoteComparator());
		return resorts;
	}

	@Override
	public List<Resort> getCitiesByVote() {
		List<Resort> resorts = dao.getCities();
		Collections.sort(resorts, new ResortVoteComparator());
		return resorts;
	}

	@Override
	public List<Resort> getResortsByVote() {
		List<Resort> resorts = dao.getResorts();
		Collections.sort(resorts, new ResortVoteComparator());
		return resorts;
	}

	@Override
	public int numOfInterCities() {
		return dao.numOfInterCities();
	}

	@Override
	public int numOfInterResorts() {
		return dao.numOfInterResorts();
	}

	@Override
	public int numOfCities() {
		return dao.numOfCities();
	}

	@Override
	public int numOfResorts() {
		return dao.numOfResorts();
	}

	@Override
	public long getMailSubCount(String name) {
		return dao.getMailSubCount(name);
	}

	@Override
	public long getVoteCount(String name) {
		return dao.getVoteCount(name);
	}

	@Override
	public String getArea(String name) {
		return dao.getArea(name);
	}

}
