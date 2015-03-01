#include<iostream>
#include<bitset>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <time.h>

namespace tbmplib{
	class tnode{
private:
	std::bitset<16> ibmp;   //internal bitmap for other node
	std::bitset<16> ebmp;   //external bitmap for other node
	std::vector<tnode*> children;  //child pointer dynamic array for other node
	std::vector<int> hops;  //next hop array
public:
	void setIbmp(std::bitset<16> ibitmap){ibmp = ibitmap;}
	void setEbmp(std::bitset<16> ebitmap){ebmp = ebitmap;}
	void setChildren(std::vector<tnode*> childs){children = childs;}
	void setHops(std::vector<int> nexthops){hops = nexthops;}
	std::bitset<16> getIbmp(){return ibmp;}
	std::bitset<16> getEbmp(){return ebmp;}
	std::vector<tnode*> getChildren(){return children;}
	std::vector<int> getHops(){return hops;}
};

	class rnode{
private:
	std::bitset<16384> ibmp;   //internal bitmap for root node
	std::bitset<16384> ebmp;   //external bitmap for root node
	std::vector<tnode*> children;  //child pointer dynamic array for root node
	std::vector<int> hops;     //next hop array
public:
	void setIbmp(std::bitset<16384> ibitmap){ibmp = ibitmap;}
	void setEbmp(std::bitset<16384> ebitmap){ebmp = ebitmap;}
	void setChildren(std::vector<tnode*> childs){children = childs;}
	void setHops(std::vector<int> nexthops){hops = nexthops;}
	std::bitset<16384> getIbmp(){return ibmp;}
	std::bitset<16384> getEbmp(){return ebmp;}
	std::vector<tnode*> getChildren(){return children;}
	std::vector<int> getHops(){return hops;}
};
	
	rnode preprocess(std::vector<std::string>* prefixes);
	int lookup(rnode* root, std::string* address);
}
