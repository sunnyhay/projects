#include<iostream>
#include<bitset>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <time.h>
#include<sstream>
using namespace std;

#include "longestMatch.h"
using namespace lpmlib;


int main(int argc, char* argv[])
{
	//TEST AREA START-------------------------------------------------------
	//bitset<256> bitmap;
	//bitmap.set(0);
	//int result = setBitmap(25,128,&bitmap);
	//cout << "result: " << result << endl;
	//cout << "bitmap now: " << bitmap << endl;

	//result = setBitmap(31,136,&bitmap);
	//cout << "result: " << result << endl;
	//cout << "bitmap now: " << bitmap << endl;

	//result = setBitmap(30,136,&bitmap);
	//cout << "result: " << result << endl;
	//cout << "bitmap now: " << bitmap << endl;

	//vector<int> lookupresult = getPosition(24,137,&bitmap);
	//cout << "get mask: " << lookupresult[0] << endl;
	//cout << "get ip adr: " << lookupresult[1] << endl;
	
	/*int lookupresult = lookup(24,137,&bitmap);
	cout << "look up result: " << lookupresult << endl;*/

	/*vector<int> hops;
	vector<int>::iterator it = hops.begin();
	hops.insert(it,5,300);
	int nexthop = 500, position = lookupresult;
	setNextHop(&hops,nexthop,position);*/
	/*for (size_t i = 0; i < hops.size (); i++) {
		cout << hops[i] << endl;
	}
	cout << "get next-hop: " << getNextHop(hops,position) << endl;
	*/

	/*bnode node;
	node.setPrefix("202.1.1.102");
	node.setBitmap(bitmap);
	node.setHops(hops);*/
	//cout << node.getPrefix() << " : " << node.getBitmap() << " : " << node.getHops().size() << endl;

	/*map<string,bnode> hash24bit;
	hash24bit.insert(pair<string,bnode>("20211",node));
	string ips[5] = {"202","1","2","254","31"};
	
	
	prefixProcess(ips,31,99,&hash24bit);*/
	//TEST AREA END-----------------------------------------------------------

	//parse argument
	/*if(argc != 2){
		cerr << "Usage: " << argv[0] << " with routing table" << endl;
		return 1;
	}*/
	char* filename = "addnextHop-ipv4-as131072.txt";

	map<string,int> table32bit;          //32-bit hash table
	map<string,bnode> table24bit;        //24-bit hash table
	map<string,bnode> table16bit;        //16-bit hash table
	map<string,bnode> table8bit;         //8-bit hash table
	bitset<256> less8bit;                //less than 8-bit bitmap
	vector<int> less8hops;               //less than 8-bit nexthop container

	vector<string> prefixes1;    //contain all the prefixes

	//PREPROCESS START-------------------------------------------------------
	
	ifstream preFile;
	preFile.open(filename, ios_base::in);
	if(preFile.is_open()){
		cout << "file opened!" << endl;
		string line;      //each line is a prefix
		//time counting begin.
		clock_t tPreStart = clock();
		while(preFile.good()){
			getline(preFile,line);
			//cout << "current line: " << line << endl;
			if(line.length() <= 1) //ignore any empty line.
				break;
			prefixes1.push_back(line);
		}
	}else
		cout << "file open failed" << endl;
	cout << "file closing..." << endl;
	preFile.close();
	
	lpmlib::preprocess(&prefixes1, &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);

	string traceAdr[] = {"1","12","0","4"};
	string queryResult = lpmlib::query(traceAdr, &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);
	cout << "query result: " << queryResult << endl;
	
	//PREPROCESS END------------------------------------------------------------------

	//test vector add
	vector<string> prefixes;
	prefixes.push_back("123.2");
	prefixes.push_back("124.3");
	prefixes.push_back("163.5");
	prefixes.push_back("223.8");	
	for (size_t i = 0; i < prefixes.size (); i++) {
		string* item = &(prefixes[i]);
		(*item).append(" 0");
	}
	for (size_t i = 0; i < prefixes.size (); i++) {
		cout << prefixes[i] << endl;
	}
	vector<string> anotherPfxs;
	for (size_t i = 0; i < prefixes.size (); i++) {
		anotherPfxs.push_back(prefixes[i] + " 0");
	}
	for (size_t i = 0; i < anotherPfxs.size (); i++) {
		cout << anotherPfxs[i] << endl;
	}
	

	return 0;
}
