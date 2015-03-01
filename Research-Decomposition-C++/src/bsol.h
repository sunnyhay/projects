#include<iostream>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <sstream>
#include <time.h>

namespace bsollib{
	class node{
private:
	int flag;  //0-pure prefix; 1-pure marker; 2-hybrid with prefix and marker.
	std::string bmp;  //BMP field: a prefix with mask in string form
	int mask;    //mask
	int nexthop; //nexthop
public:
	void setFlag(int f){flag = f;}
	void setBmp(std::string bestmp){bmp = bestmp;}
	void setMask(int m){mask = m;}
	void setNexthop(int hop){nexthop = hop;}
	int getFlag(){return flag;}
	std::string getBmp(){return bmp;}
	int getMask(){return mask;}
	int getNexthop(){return nexthop;}
};
	
	void preprocess(std::vector<std::string>* prefixes, std::map<std::string,node*>* array);
	int lookup(std::string* address, std::map<std::string,node*>* array);
}
