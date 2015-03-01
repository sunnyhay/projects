#include <string>
#include <map>
#include<bitset>
#include<vector>
#include<iostream>
#include<math.h> 
#include <stdlib.h>
#include <fstream>
#include <time.h>
#include<sstream>

namespace lpmlib{
	class bnode{
private:
	std::string prefix;  //should be an IP address like "202.102.1.0"
	std::bitset<256> bitmap;
	std::vector<int> hops;
public:
	void setPrefix(std::string pfx){prefix = pfx;}
	void setBitmap(std::bitset<256> bmp){bitmap = bmp;}
	void setHops(std::vector<int> nexthops){hops = nexthops;}
	std::string getPrefix(){return prefix;}
	std::bitset<256> getBitmap(){return bitmap;}
	std::vector<int> getHops(){return hops;}
};

	//void lpmPreprocess(int a);
	void preprocess(std::vector<std::string>* prefixes, std::map<std::string,int>* table32bit, std::map<std::string,bnode>* table24bit,
		std::map<std::string,bnode>* table16bit, std::map<std::string,bnode>* table8bit, 
		std::bitset<256>* less8bit, std::vector<int>* less8hops);
	std::string query(std::string* traceAdr, std::map<std::string,int>* table32bit, std::map<std::string,bnode>* table24bit,
		std::map<std::string,bnode>* table16bit, std::map<std::string,bnode>* table8bit, 
		std::bitset<256>* less8bit, std::vector<int>* less8hops);
	//std::string lpmLookup(std::string* ip);
}
