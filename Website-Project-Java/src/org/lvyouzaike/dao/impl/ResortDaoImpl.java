package org.lvyouzaike.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.lvyouzaike.dao.ResortDao;
import org.lvyouzaike.model.Resort;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component("resortDao")
public class ResortDaoImpl implements ResortDao {
	private HibernateTemplate ht;

	public HibernateTemplate getHt() {
		return ht;
	}

	@Resource
	public void setHt(HibernateTemplate ht) {
		this.ht = ht;
	}

	@Override
	public boolean check(String name){
		@SuppressWarnings("unchecked")
		List<Resort> resorts = ht.find("from Resort r where r.name = '" + name + "'");
		if(resorts != null && resorts.size() > 0)
			return true;
		return false;
	}
	
	@Override
	public boolean add(Resort resort) {
		if(!check(resort.getName())){
			ht.merge(resort);
			return true;
		}			
		return false;
	}

	@Override
	public boolean update(Resort resort) {
		ht.update(resort);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resort> getAll() {
		return ht.find("from Resort");
	}

	@Override
	public Resort getById(int id) {
		return ht.get(Resort.class, id);
	}

	@Override
	public Resort getByName(String name) {
		@SuppressWarnings("unchecked")
		List<Resort> resorts = ht.find("from Resort r where r.name = '" + name + "'");
		if(resorts != null && resorts.size() > 0){
			return resorts.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resort> getInterCities() {
		return ht.find("from Resort r where r.isInterCity = true");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resort> getInterResorts() {
		return ht.find("from Resort r where r.isInterResort = true");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resort> getCities() {
		return ht.find("from Resort r where r.isDomesticCity = true");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Resort> getResorts() {
		return ht.find("from Resort r where r.isDomesticResort = true");
	}

	@Override
	public int numOfInterCities() {
		@SuppressWarnings("unchecked")
		List<Long> count = ht.find("select count(*) from Resort r where r.isInterCity = true");
		long r = count.get(0);
		int t = (int)r;
		return t;
	}

	@Override
	public int numOfInterResorts() {
		@SuppressWarnings("unchecked")
		List<Long> count = ht.find("select count(*) from Resort r where r.isInterResort = true");
		long r = count.get(0);
		int t = (int)r;
		return t;
	}

	@Override
	public int numOfCities() {
		@SuppressWarnings("unchecked")
		List<Long> count = ht.find("select count(*) from Resort r where r.isDomesticCity = true");
		long r = count.get(0);
		int t = (int)r;
		return t;
	}

	@Override
	public int numOfResorts() {
		@SuppressWarnings("unchecked")
		List<Long> count = ht.find("select count(*) from Resort r where r.isDomesticResort = true");
		long r = count.get(0);
		int t = (int)r;
		return t;
	}

	@Override
	public long getMailSubCount(String name) {
		@SuppressWarnings("unchecked")
		List<Resort> resorts = ht.find("from Resort r where r.name = '" + name + "'");
		if(resorts != null && resorts.size() > 0){
			if(resorts.get(0).getMails() != null)
				return resorts.get(0).getMails().size();
		}
		return 0;
	}
	
	@Override
	public long getVoteCount(String name){
		@SuppressWarnings("unchecked")
		List<Resort> resorts = ht.find("from Resort r where r.name = '" + name + "'");
		if(resorts != null && resorts.size() > 0){
			if(resorts.get(0).getVoters() != null)
				return resorts.get(0).getVoters().size();
		}
		return 0;
	}

	@Override
	public String getArea(String name){
		@SuppressWarnings("unchecked")
		List<Resort> resorts = ht.find("from Resort r where r.name = '" + name + "'");
		if(resorts != null && resorts.size() > 0){
			return resorts.get(0).getArea().getName();
		}
		return null;
	}
	
}
