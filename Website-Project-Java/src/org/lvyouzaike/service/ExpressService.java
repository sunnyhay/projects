package org.lvyouzaike.service;

public interface ExpressService {
	//用户对心情系统或认同指数或宰客指数表态，就是增加相应项目的一票
	//其中，name标示这项表态，item是对应的某一项编号
	public boolean increExpressItem(String name, int item);
}
