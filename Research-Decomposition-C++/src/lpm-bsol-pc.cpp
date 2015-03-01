#include <string>
#include <iostream>
#include <bitset>
#include <math.h>
#include <algorithm>
#include <vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <sstream>
#include <time.h>
#include <stdio.h>
#include <cstring>
#include <functional>
#include <unordered_map>

using namespace std;

#include "bsol.h"
using namespace bsollib;

/*
 * class definitions
 */
class emNode{
private:
	//static int currentId;  //class-scope id
	int id;   //id for each exact-match hash table node
	int counter;  //duplication counter for each exact-match node
public:
	emNode(){
	  //id = ++currentId;
	  counter = 1;
	  //std::cout << "numOfID = " << currentId << std::endl;
    	}
	
	~emNode(){
	  //currentId--;
	  //std::cout << "numOfID = " << currentId << std::endl;
    	}
	
	int getId(){return id;}
	void setId(int id1){id = id1;}
	int getCounter(){return counter;}
	void increCounter(){counter++;}
};
//int emNode::currentId = 0; //emNode's incremental id generator

class pmNode{
private:
	//static int currentId;  //class-scope id for prefix-match table
	int id;  //id for each prefix
	int counter;  //duplication counter for each prefix
public:
	vector<int> plist;  //overlapping prefix list containing IDs
	vector<int> rlist;  //referenced prefix list containing IDs

	pmNode(){
	  //id = ++currentId;
	  counter = 1;	  
	}

	~pmNode(){
	  //currentId--;
	}

	int getId(){return id;}
	void setId(int id1){id = id1;}
	int getCounter(){return counter;}
	void increCounter(){counter++;}
	void insertPlist(int id){plist.push_back(id);}
	void insertRlist(int id){rlist.push_back(id);}
};
//int pmNode::currentId = 0;  //pmNode's incremental id generator

/*
 * exact match methods
 */

//query the exact-match hash table.
//return 0 if not found, otherwise return its ID.
int queryEM(string* key, map<string,emNode>* table){
	//cout << "key: " << *key << endl;
	//cout << "table size: " << (*table).size() << endl;
	map<string,emNode>::iterator it = (*table).find(*key);
	if(it != (*table).end()){
	  //cout << "ID: " << it->second.getId() << endl;
	  //cout << "counter: " << it->second.getCounter() << endl;
	  return it->second.getId();	  
	}
	return 0;
}

//deprecated
//insert method for exact-match hash table. if there is duplicate key, increment the counter, return false. 
//otherwise create a new node and return true
bool insertEMNode(string* key, map<string,emNode>* table){
	int qResult = queryEM(key,table);
	if(qResult == 0){
	  emNode node;
	  (*table).insert(pair<string,emNode>(*key,node));
	  return true;
	}else{
	  map<string,emNode>::iterator it = (*table).find(*key);
	  if(it != (*table).end()){
	  	it->second.increCounter();
	  }
	  return false;
	}
}

//insert method for vectors in a pmNode. 
void insertIntoVector(int id, vector<int>* list){
	(*list).push_back(id);
	/*std::sort ((*list).begin(), (*list).end());
	for(vector<int>::iterator it=(*list).begin(); it!= (*list).end(); it++)
	  cout << *it << endl;*/
}

/*
 * prefix match methods
 */

//query an ID table for a prefix hash table given a prefix.
//if exist, return true, false otherwise
bool checkIDtable(string* pfx, map<string,int>* idTable){
	map<string,int>::iterator it = (*idTable).find(*pfx);
	if(it != (*idTable).end())
	  return true;
	return false;
}

//convert IP address to integer
unsigned long int ipToInt(string* ip){
	char ipadr[1024];
	strcpy(ipadr,(*ip).c_str());	
	unsigned long int num=0,val;
	char *tok,*ptr;
	tok=strtok(ipadr,".");
	while( tok != NULL){
	  val=strtoul(tok,&ptr,0);
	  num=(num << 8) + val;
	  tok=strtok(NULL,".");
	}
	return num;
}

//get the maximum IP addition to a prefix mask
int getMaxMaskAdd(int maskGap){
	return (int)pow(2,maskGap)-1;
}

//retrieve low bound and high bound in the prefix
//to integers and return
vector<unsigned long int> retrieveIPs(string* pfx){
	vector<unsigned long int> ips;

	//1.retrieve ip and mask from a string
	string address[5];    //the first four are ip, the last one is mask
	int nexthop1;         //the nexthop information for each prefix
	const char charToSearch = '.';	//current delimitor for line in the prefix file
	size_t charPos = (*pfx).find(charToSearch,0);
	int index = 0;
	size_t oldPos = 0;
	int begin = 0;
	while(charPos != string::npos){
		address[index++] = (*pfx).substr(begin,charPos-oldPos);
		//cout << ips[index] << endl;
		size_t charSearchPos = charPos + 1;
		begin = charSearchPos;
		oldPos = charSearchPos;
		charPos = (*pfx).find(charToSearch,charSearchPos);
	}
	//cout << "charPos: " << charPos << endl;
	//cout << "oldPos: " << oldPos << endl;
	address[index] = (*pfx).substr(begin,oldPos);
	//cout << "ip part: " << (*pfx).substr(0,oldPos-1) << endl;
	
	/*for(int i = 0; i < 5; i++)
		cout << address[i] << endl;*/
	int adr[4];  //the four parts of the ip
	int mask = atoi(address[4].c_str());  //turn the mask string to integer
	//cout << mask << endl;
	for(int i = 0; i < 4; i++){
		adr[i] = atoi(address[i].c_str());
		//cout << "int IP part: " << adr[i] << endl;
	}
	
	//2. get low bound and high bound
	string lowBound = (*pfx).substr(0,oldPos-1);
	string highBound;
	//cout << "low bound: " << lowBound << endl;
	int maskGap = 32 - mask;
	int curIPPos = 3;  //current IP address in adr[]
	if(maskGap == 0){
		highBound = lowBound;
	}else{
		while(maskGap > 0){
			adr[curIPPos] += getMaxMaskAdd(maskGap >= 8? 8 : maskGap);
			curIPPos--;
			maskGap -= 8; 
		}
	}
	for(int i = 0; i < 4; i++){
		stringstream ss;  //create a stringstream
		ss << adr[i];  //add number to the stream
		highBound += (ss.str()+".");
		//cout << "int IP part: " << ss.str() << endl;
	}
	//cout << "high bound: " << highBound.substr(0,highBound.length()-1) << endl;
	string hbStr = highBound.substr(0,highBound.length()-1);
	//cout << "low bound int: " << ipToInt(&lowBound) << endl;
	//cout << "high bound int: " << ipToInt(&hbStr) << endl;
	ips.push_back(ipToInt(&lowBound));
	ips.push_back(ipToInt(&hbStr));

	return ips;
}

//internal method used for prefix overlapping relation
//if not, return 0; else return 1.
bool isOverlapping(string* pfx1, string* pfx2){
	bool result = false;
	vector<unsigned long int> pfx1Bounds = retrieveIPs(pfx1);
	vector<unsigned long int> pfx2Bounds = retrieveIPs(pfx2);
	unsigned long int pfx1Low = pfx1Bounds[0];	
	unsigned long int pfx1High = pfx1Bounds[1];	
	unsigned long int pfx2Low = pfx2Bounds[0];	
	unsigned long int pfx2High = pfx2Bounds[1];	
	/*cout << "low of pfx1: " << pfx1Low << endl;
	cout << "high of pfx1: " << pfx1High << endl;
	cout << "low of pfx2: " << pfx2Low << endl;
	cout << "high of pfx2: " << pfx2High << endl;*/
	
	if(!(pfx1Low >= pfx2High || pfx2Low >= pfx1High)){
		result = true;
	}

	return result;
}

//increment a pmNode's counter
//precondition is the node must exist
void increPMNodeCounter(string* pfx, map<string,pmNode>* pmTable){
	map<string,pmNode>::iterator it = (*pmTable).find(*pfx);
	if(it != (*pmTable).end()){//if found the prefix
		pmNode* curNode = &(it->second);
		(*curNode).increCounter();
	}else{
		cout << "not found the node for prefix: " << *pfx << endl;
	}
}

//preprocess method for prefix-match table
//1. store all the prefixes into a vector, naturally ordered by the rule No.
//2. for each prefix, query IDtable firstly. 
//2.1 if exist the same prefix, add the counter;
//otherwise 2.2 create a new pmNode and insert it into idtable.
//2.2.1 suppose current prefix has a rule number T,
//from 1 to T-1 iterate the prefix vector to 
//check each prefix's isOverlapping relation with this prefix
//2.2.2 if one prefix has overlapped with current one,
//add one prefix's id into current pmNode's plist
//and add current pmNode's id into that prefix's pmNode's rlist.
//after the iteration, insert the pmNode into pmTable
void preprocessPMTable(vector<string>* prefixes, map<string,pmNode>* pmTable, map<string,int>* idTable){
	//int searchScope = 40;  //the heuristic constant to control the searching scope of prefix overlapping
	int idCounter = 0;  //id generator for pmNode
	
	//iterate each prefix and query idtable firstly
	int ruleNoCounter = 0;
	for(vector<string>::iterator it=(*prefixes).begin(); it!= (*prefixes).end(); it++){
		ruleNoCounter++;
		//cout << *it << endl;
		string prefix = *it;
		bool checkIDtableResult = checkIDtable(&prefix, idTable);
		//cout << prefix << " plus " << checkIDtableResult << endl;
		if(checkIDtableResult){//2.1 prefix exist, add the counter
			increPMNodeCounter(&prefix, pmTable);
		}else{//2.2 create a new pmNode and insert into IDtable
			idCounter++;
			pmNode node1;
			node1.setId(idCounter);
			//cout << "prefix node's ID: " << node1.getId() << endl;
			(*idTable).insert(pair<string,int>(prefix,node1.getId()));
			int begin = 1;  //ruleNoCounter > searchScope ? (ruleNoCounter-searchScope) : 0;
			//2.2.1 iterate from 1 to ruleNoCounter-1 for overlapping relation
			for(vector<int>::size_type i = begin; i < (ruleNoCounter-1); i++) {
			//for(vector<string>::iterator it1=prefixes.begin(); it1!= prefixes.end(); it1++){
				//string prefix1 = *it1;
				string prefix1 = (*prefixes)[i];
				bool isOverlap = isOverlapping(&prefix, &prefix1);
				//cout << "prefix: " << prefix << " plus prefix1: " << prefix1 << " overlap? " << isOverlap << endl;
				if(isOverlap){//2.2.2 overlapped
					//find the overlapped prefix's pmNode
					map<string,pmNode>::iterator it2 = (*pmTable).find(prefix1);
					if(it2 != (*pmTable).end()){//if found the node
						pmNode* curNode = &(it2->second);
						//cout << "prefix1 node's ID: " << curNode.getId() << endl;
						//cout << "prefix node's ID: " << node1.getId() << endl;
						//update plist of node1
						if(std::find(node1.plist.begin(), node1.plist.end(), (*curNode).getId()) == node1.plist.end()) {
							node1.insertPlist((*curNode).getId());
						}
						//update rlist of curNode
						if(std::find((*curNode).rlist.begin(), (*curNode).rlist.end(), node1.getId()) == (*curNode).rlist.end()) {
							(*curNode).insertRlist(node1.getId());
						}
					}else{
						cout << "not found the node for prefixes: " << prefix1 << " plus " << prefix << endl;
						//cout << "current count: " << count << " with ruleNoCounter: " << ruleNoCounter << endl;
					}
				}
			}
			//insert the new pmNode
			(*pmTable).insert(pair<string,pmNode>(prefix,node1));
		}
	}
		
	 
}

//query pmTable, given a prefix query the map to get its corresponding ID only.
//if no result, return 0.
//this method is for real prefix lookup
int queryPMTable(string* prefix, map<string,pmNode>* pmTable){
	int result = 0;
	map<string,pmNode>::iterator it = (*pmTable).find(*prefix);
	if(it != (*pmTable).end()){//if found the node
		pmNode curNode = it->second;
		result = curNode.getId();
	}
	return result;
}

//search pmTable, given a prefix query the map to get its corresponding ID and all
//the other IDs in the plist, if no result return an empty vector.
//otherwise the vector should contain all the IDs related to pmNode of this prefix
//the method is for establishing rule hash table and update.
vector<int> searchPMTable(string* prefix, map<string,pmNode>* pmTable){
	vector<int> result;
	map<string,pmNode>::iterator it = (*pmTable).find(*prefix);
	if(it != (*pmTable).end()){//if found the node
		pmNode curNode = it->second;
		result.push_back(curNode.getId());
		vector<int> plist = curNode.plist;
		for(vector<int>::iterator it1=plist.begin(); it1!= plist.end(); it1++){
			result.push_back(*it1);
		}
	}
	return result;
}

/*
 * rule hash methods
 */

//parse a packet classifier and put each field's items into a vector
void parseClassifier(vector<string>* srcIPs, vector<string>* dstIPs, vector<string>* srcPorts,
	vector<string>* dstPorts, vector<string>* types, char* classifier){
	const char delimChar = '	';	//current delimitor for line
	const char replaceChar = '/';   //the char to be replaced with '.' in ip address
	const char portReplaceChar = ':';  //the char to be replaced with ' ' in port field
	size_t charPos;   //the position of current delimChar
	string content[5];  //temporary strings of a rule
	
	ifstream preFile;
	preFile.open(classifier, ios_base::in);
	if(preFile.is_open()){
		cout << "classifier file opened!" << endl;
		string line;      //each line is a prefix
		int begin;      //the position to begin a part of ip
		size_t oldPos;  //the old position for next part
		int index;

		while(preFile.good()){
			getline(preFile,line);
			if(line.length() <= 1) //ignore any empty line.
				break;
			//cout << "current line: " << line << endl;
			charPos = line.find(delimChar,0);
			oldPos = 0;
			begin = 0;
			index = 0;
			while(charPos != string::npos && index < 5){
				content[index++] = line.substr(begin,charPos-oldPos);
				//cout << "content: " << content[index-1] << endl;
				size_t charSearchPos = charPos + 1;
				begin = charSearchPos;
				oldPos = charSearchPos;
				charPos = line.find(delimChar,charSearchPos);
			}
			/*for(int i = 0; i < 5; i++){
				cout << content[i] << endl;
			}*/
			//1. source ip
			int replaceCharPos = content[0].find(replaceChar,0);
			//cout << content[0].replace(replaceCharPos,1,".").substr(1) << endl;
			(*srcIPs).push_back(content[0].replace(replaceCharPos,1,".").substr(1));
			//2. destination ip
			replaceCharPos = content[1].find(replaceChar,0);
			//cout << content[1].replace(replaceCharPos,1,".") << endl;
			(*dstIPs).push_back(content[1].replace(replaceCharPos,1,"."));
			//3. source port
			replaceCharPos = content[2].find(portReplaceChar,0);
			//cout << content[2].replace(replaceCharPos,1,"") << endl;
			(*srcPorts).push_back(content[2].replace(replaceCharPos,1,""));
			//4. destination port
			replaceCharPos = content[3].find(portReplaceChar,0);
			//cout << content[3].replace(replaceCharPos,1,"") << endl;
			(*dstPorts).push_back(content[3].replace(replaceCharPos,1,""));
			//5. type
			//cout << content[4] << endl;
			(*types).push_back(content[4]);
			
			//cout << "one line done" << endl;
		}
	}
	cout << "classifier file closing..." << endl;
	preFile.close();
}

//return a hash value using stl hash function
//input is a collection of integers
unsigned int getHashVal(vector<int>* ids){
	string str = "";
	for (auto it = (*ids).cbegin(); it != (*ids).cend(); ++it)
		str += to_string(*it);	
    	hash<string> hash_fn;
    	unsigned int str_hash = hash_fn(str);
	return str_hash;
}

bool sortRuleNo (unsigned int i,unsigned int j) { return (i<j); }

//preprocess a classifier using LPM method of bsol
void preprocessBsol(vector<string>* srcIPs, vector<string>* dstIPs, vector<string>* srcPorts,
	vector<string>* dstPorts, vector<string>* types, char* classifier, 
	map<string,pmNode>* srcIPPMTable, map<string,int>* srcIPIDTable, 
	map<string,pmNode>* dstIPPMTable, map<string,int>* dstIPIDTable, map<string,emNode>* typeTable, 
	map<string,node*>* srcIParray, map<string,node*>* dstIParray,
	map<unsigned int,vector<unsigned int>>* ruleHTable){

	int typeIdCounter = 0;  //id counter for type field
	int srcIPIdCounter = 0;  //id counter for source IP 
	int dstIPIdCounter = 0;  //id counter for destination IP
	
	//1. parse the classifier
	parseClassifier(srcIPs, dstIPs, srcPorts, dstPorts, types, classifier);

	//2. deal with each exact-match and prefix-match field
	//2.1 type field
	string typeVal = "";
	for (vector<string>::iterator it=(*types).begin(); it!=(*types).end(); ++it){
		typeVal = *it;
		int typeResult = queryEM(&typeVal,typeTable);
		if(typeResult == 0){
			typeIdCounter++;
			emNode typeNode;
			typeNode.setId(typeIdCounter);
			(*typeTable).insert(make_pair(typeVal,typeNode));
		
		}else{
			map<string,emNode>::iterator it1 = (*typeTable).find(typeVal);
			if(it1 != (*typeTable).end()){				
		 		it1->second.increCounter();
			}		
		}	
	}
	
	/*for(auto e = typeTable.cbegin(); e != typeTable.cend(); e++){
		emNode node1 = e->second;
		cout << "type: " << e->first << " with id: " << node1.getId()
			<< " and counter: " << node1.getCounter() << endl;
	}*/

	//2.2 source IP 

	//time counting begin.
	clock_t srcStart = clock();

	preprocessPMTable(srcIPs, srcIPPMTable, srcIPIDTable);
	/*cout << "size of src IP pmTable: " << (*srcIPPMTable).size() << endl;
	cout << "size of src IP idTable: " << (*srcIPIDTable).size() << endl;*/

	//for test print IDtable
	/*for(auto e = (*srcIPIDTable).cbegin(); e != (*srcIPIDTable).cend(); e++){
		cout << e->first << " " << e->second << endl;
	}*/
	
	int maxPlistCount = 0;
	int maxRlistCount = 0;
	int maxDupCounter = 0;
	for(auto e = (*srcIPPMTable).cbegin(); e != (*srcIPPMTable).cend(); e++){
		pmNode node1 = e->second;
		if(node1.getCounter() > maxDupCounter)
			maxDupCounter = node1.getCounter();
		if(node1.plist.size() > maxPlistCount)
			maxPlistCount = node1.plist.size();
		if(node1.rlist.size() > maxRlistCount)
			maxRlistCount = node1.rlist.size();
		/*if(node1.rlist.size() == 42){//for test
			vector<int> temp = node1.rlist;
			for(int i = 0; i < temp.size(); i++){
				cout << temp[i] << " ";
			}
			cout << endl;
			cout << "max prefix ID: " << node1.getId() << endl;
		}*/		
	}
	/*cout << "max plist volume: " << maxPlistCount << endl;
	cout << "max rlist volume: " << maxRlistCount << endl;
	cout << "max dup counter: " << maxDupCounter << endl;*/
	cout << "source IP pmTable processing time elasping: " << (double)(clock() - srcStart)/CLOCKS_PER_SEC << " seconds." << endl;

	//2.3 destination IP

	//time counting begin.
	clock_t dstStart = clock();

	preprocessPMTable(dstIPs, dstIPPMTable, dstIPIDTable);
	/*cout << "size of dst IP pmTable: " << (*dstIPPMTable).size() << endl;
	cout << "size of dst IP idTable: " << (*dstIPIDTable).size() << endl;*/	
	maxPlistCount = 0;
	maxRlistCount = 0;
	maxDupCounter = 0;
	for(auto e = (*dstIPPMTable).cbegin(); e != (*dstIPPMTable).cend(); e++){
		pmNode node1 = e->second;
		if(node1.getCounter() > maxDupCounter)
			maxDupCounter = node1.getCounter();
		if(node1.plist.size() > maxPlistCount)
			maxPlistCount = node1.plist.size();
		if(node1.rlist.size() > maxRlistCount)
			maxRlistCount = node1.rlist.size();
	}
	/*cout << "max plist volume: " << maxPlistCount << endl;
	cout << "max rlist volume: " << maxRlistCount << endl;
	cout << "max dup counter: " << maxDupCounter << endl;*/
	cout << "destination IP pmTable processing time elasping: " << (double)(clock() - dstStart)/CLOCKS_PER_SEC << " seconds." << endl;

	//2.4 construct LPM data structure
	//first add dummy hops into ip vectors which are new
	vector<string> srcIPsHops;  //add hops to srcIPs
	vector<string> dstIPsHops;  //add hops to dstIPs
	for (size_t i = 0; i < (*srcIPs).size (); i++) {
		srcIPsHops.push_back((*srcIPs)[i] + " 0");
	}
	for (size_t i = 0; i < (*dstIPs).size (); i++) {
		dstIPsHops.push_back((*dstIPs)[i] + " 0");
	}
	//cout << srcIPsHops[0] << endl;
	//cout << dstIPsHops[0] << endl;
	
	//time counting begin.
	clock_t lpmStart = clock();

	//source IP
	bsollib::preprocess(&srcIPsHops, srcIParray);
	cout << "srcIP bsol processing time elasping: " << (double)(clock() - lpmStart)/CLOCKS_PER_SEC << " seconds." << endl;	
	
	lpmStart = clock();
	//destination IP
	bsollib::preprocess(&dstIPsHops, dstIParray);
	cout << "dstIP bsol processing time elasping: " << (double)(clock() - lpmStart)/CLOCKS_PER_SEC << " seconds." << endl;		
		
	//3. iterate each rule to product results from queryEM and queryPM
	int bucketCounter = 0;  //counter for the total number of buckets in the rule hash table

	//time counting begin.
	clock_t rulehashStart = clock();
	
	int maxSrcIPIDs = 0;  //for test
	int maxDstIPIDs = 0;  //for test
	
	for(size_t i = 0; i < (*srcIPs).size(); i++){
		string srcIP = (*srcIPs)[i];
		string dstIP = (*dstIPs)[i];
		string type = (*types)[i];
	
		//get all IDs for this source IP	
		vector<int> srcIPResult = searchPMTable(&srcIP, srcIPPMTable);
		//get all IDs for this destination IP
		vector<int> dstIPResult = searchPMTable(&dstIP, dstIPPMTable);
		int typeID = queryEM(&type, typeTable);
		if(srcIPResult.size() > maxSrcIPIDs)
			maxSrcIPIDs = srcIPResult.size();
		if(dstIPResult.size() > maxDstIPIDs)
			maxDstIPIDs = dstIPResult.size();
		
		
		int ruleNo = i+1;   //current rule number
		for(size_t j = 0; j < srcIPResult.size(); j++){
			for(size_t g = 0; g < dstIPResult.size(); g++){
				vector<int> ids;  //the vector contains all ID for key generation
				ids.push_back(srcIPResult[j]);
				ids.push_back(dstIPResult[g]);
				ids.push_back(typeID);
				unsigned int key = getHashVal(&ids);  //get the key of the rule hash table
				map<unsigned int,vector<unsigned int>>::iterator it2 = (*ruleHTable).find(key);
				if(it2 != (*ruleHTable).end()){//found the existing entry
		 			vector<unsigned int>* curList = &(it2->second);
					(*curList).push_back(ruleNo);
				}else{//not found, then create a new entry
					vector<unsigned int> ruleList;
					ruleList.push_back(ruleNo);
					(*ruleHTable).insert(make_pair(key,ruleList));
				}
			}
		}
		
		//traverse the rule hash table and sort each rule list
		/*for(auto e = (*ruleHTable).begin(); e != (*ruleHTable).end(); e++){
			vector<unsigned int>* curList = &(e->second);
			std::sort ((*curList).begin(), (*curList).end(), sortRuleNo);
		}*/
	}
	
	//stat. about rule hash table
	cout << "rule hash table size: " << (*ruleHTable).size() << endl;	

	/*cout << "max source IP ID count: " << maxSrcIPIDs << endl;
	cout << "max destination IP ID count: " << maxDstIPIDs << endl;	*/
	cout << "rule hash processing time elasping: " << (double)(clock() - rulehashStart)/CLOCKS_PER_SEC << " seconds." << endl;	
	cout << "*****************************************************************************" << endl;
}

//convert an IP to part array
//four parts are stored into traceAdr
void convertIP(string* target, string* traceAdr){
	const char charToSearch = '.';	//current delimitor for line in the prefix file
	size_t charPos = (*target).find(charToSearch,0);
	int begin = 0;      //the position to begin a part of ip
	size_t oldPos = 0;  //the old position for next part
	int index = 0;      //index for the ips array

	while(charPos != string::npos && index < 4){
		traceAdr[index++] = (*target).substr(begin,charPos-oldPos);
		//cout << ips[index] << endl;
		size_t charSearchPos = charPos + 1;
		begin = charSearchPos;
		oldPos = charSearchPos;
		charPos = (*target).find(charToSearch,charSearchPos);
	}
	traceAdr[index] = (*target).substr(begin,charPos-oldPos);	
}

//query the rule hash table with given input by means of LPM method of bitmap-hash
void lookupBsol(char* traceFile, map<string,int>* srcIPIDTable, map<string,int>* dstIPIDTable, map<string,emNode>* typeTable, 
	map<string,node*>* srcIParray, map<string,node*>* dstIParray, map<unsigned int,vector<unsigned int>>* ruleHTable){

	const char delimChar = ' ';	//current delimitor for line
	string content[5];  //temporary strings of a trace
	size_t charPos;   //the position of current delimChar

	string traceAdr[] = {"","","",""};
		
	ifstream preFile;
	preFile.open(traceFile, ios_base::in);
	if(preFile.is_open()){
		cout << "trace file opened!" << endl;
		string line;      //each line is a trace
		int begin;      //the position to begin a part
		size_t oldPos;  //the old position for next part
		int index;

		//time counting begin.
		clock_t tPreStart = clock();
		clock_t tSrcIPSum = 0;
		clock_t tSrcIPLPMSum = 0;
		clock_t tSrcIPLookupSum = 0;

		clock_t tDstIPSum = 0;
		clock_t tDstIPLPMSum = 0;
		clock_t tDstIPLookupSum = 0;
		
		clock_t tTypeSum = 0;

		while(preFile.good()){
			getline(preFile,line);
			if(line.length() <= 1) //ignore any empty line.
				break;
			//cout << "current line: " << line << endl;
			charPos = line.find(delimChar,0);
			oldPos = 0;
			begin = 0;
			index = 0;
			while(charPos != string::npos && index < 5){
				content[index++] = line.substr(begin,charPos-oldPos);
				//cout << "content: " << content[index-1] << endl;
				size_t charSearchPos = charPos + 1;
				begin = charSearchPos;
				oldPos = charSearchPos;
				charPos = line.find(delimChar,charSearchPos);
			}
			content[index] = line.substr(begin,charPos-oldPos);
			/*for(int i = 0; i < 5; i++){
				cout << content[i] << " ";
			}
			cout << endl;*/

			//now content[5] contains a trace
			//1. conduct LPM on IPs
			int srcID = 0;
			int dstID = 0;
			int typeID = 0;
			string srcResult;
			string dstResult;
		
			//source IP
			clock_t tSrcIP = clock();
			convertIP(&content[0], traceAdr);
			clock_t tSrcIPLPM = clock();
			srcResult = bsollib::lookup(traceAdr, srcIParray) + "";
			tSrcIPLPMSum += (double)(clock() - tSrcIPLPM);
			//cout << "src IP: " << content[0] << " with result: " << srcResult << endl;
			clock_t tSrcIPLookup = clock();
			map<string,int>::iterator it1 = (*srcIPIDTable).find(srcResult);
			tSrcIPLookupSum += (double)(clock() - tSrcIPLookup);
			if(it1 != (*srcIPIDTable).end()){
		 		srcID = it1->second;				
			}
			//cout << "srcID: " << srcID << endl;
			tSrcIPSum += (double)(clock() - tSrcIP);

			//destination IP
			clock_t tDstIP = clock();
			convertIP(&content[1], traceAdr);  //now traceAdr with destination IP
			clock_t tDstIPLPM = clock();
			dstResult = bsollib::lookup(traceAdr, dstIParray) + "";
			tDstIPLPMSum += (double)(clock() - tDstIPLPM);			
			//cout << "dst IP: " << content[1] << " with result: " << dstResult << endl;
			clock_t tDstIPLookup = clock();
			map<string,int>::iterator it2 = (*dstIPIDTable).find(dstResult);
			tDstIPLookupSum += (double)(clock() - tDstIPLookup);
			if(it2 != (*dstIPIDTable).end()){
		 		dstID = it2->second;				
			}
			//cout << "dstID: " << dstID << endl;
			tDstIPSum += (double)(clock() - tDstIP);
		
			clock_t tType = clock();
			typeID = queryEM(&content[4], typeTable);
			tTypeSum += (double)(clock() - tType);
			//cout << "typeID: " << typeID << endl;

			//2. query rule hash table
			vector<int> ids;  //the vector contains all ID for key generation
			ids.push_back(srcID);
			ids.push_back(dstID);
			ids.push_back(typeID);
			unsigned int key = getHashVal(&ids);  //get the key of the rule hash table
			map<unsigned int,vector<unsigned int>>::iterator it4 = (*ruleHTable).find(key);
			if(it4 != (*ruleHTable).end()){
		 		vector<unsigned int>* curList = &(it4->second);
				/*cout << "rule no: " << (*curList)[0] << endl;
				cout << "rule vector size: " << (*curList).size() << endl;*/
			}

		}
		cout << "Total time elasping: " << (double)(clock() - tPreStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "srcIP total searching time elasping: " << (double)(tSrcIPSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "srcIP LPM time elasping: " << (double)(tSrcIPLPMSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "srcIP map lookup time elasping: " << (double)(tSrcIPLookupSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "dstIP total searching time elasping: " << (double)(tDstIPSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "dstIP LPM time elasping: " << (double)(tDstIPLPMSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "dstIP map lookup time elasping: " << (double)(tDstIPLookupSum)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "type searching time elasping: " << (double)(tTypeSum)/CLOCKS_PER_SEC << " seconds." << endl;

	}
	cout << "trace file closing..." << endl;
	cout << "*****************************************************************************" << endl;
	preFile.close();
}

int main(int argc, char* argv[])
{
	//parse argument
	if(argc != 3){
		cerr << "Usage: " << argv[0] << " with classifier and trace file" << endl;
		return 1;
	}

	//TEST AREA START-------------------------------------------------------
	//TEST EXACT-MATCH
	//string name1 = "TCP";
	//emNode en1;
	//cout << "id of en1: " << en1.getId() << endl;
	//string name2 = "UDP";
	//emNode en2;
	//cout << "id of en2: " << en2.getId() << endl;

	//map<string,emNode> emTable;
	//emTable.insert(pair<string,emNode>(name1,en1));
	//emTable.insert(pair<string,emNode>(name2,en2));	
	//string name3 = "HTTP";
	//int result = query(&name3,&emTable);
	//cout << "query result: " << result << endl;

	//en1.increCounter();
	//cout << "en1's new counter: " << en1.getCounter() << endl;

	/*map<string,emNode> emTable1;
	string name4 = "IP";
	string name5 = "SSP";
	bool result1 = insertEMNode(&name4, &emTable1);
	cout << "insert the first: " << result1 << endl;
	bool result2 = insertEMNode(&name5, &emTable1);
	cout << "insert the second: " << result2 << endl;
	string name6 = "IP";
	bool result3 = insertEMNode(&name6, &emTable1);
	cout << "insert the third: " << result3 << endl;
	cout << "query duplicate str: " << queryEM(&name4,&emTable1) << endl;*/

	//TEST PREFIX-MATCH	
	/*pmNode pn1;
	pn1.setId(1);
	cout << "id of pn1: " << pn1.getId() << endl;
	pmNode pn2;
	pn2.setId(2);
	cout << "id of pn2: " << pn2.getId() << endl;
	pn1.insertPlist(10);
	pn1.insertPlist(20);
	
	for(vector<int>::iterator it=pn1.plist.begin(); it!= pn1.plist.end(); it++)
		cout << *it << endl;*/
	
	/*map<string,pmNode> pmTable1;
	string prefix1 = "123.7.2.0 24";
	string prefix2 = "123.7.2.128 25";*/

	//char str1[1024] = "255.255.255.255";
	/*string prefix1 = "123.7.2.0";
	unsigned long int result4 = ipToInt(&prefix1);
	cout << "ip's int: " << result4 << endl;*/

	/*insertIntoVector(2, &pn1.plist);
	insertIntoVector(5, &pn1.plist);
	insertIntoVector(3, &pn1.plist);
	
	insertIntoVector(10, &pn1.rlist);
	insertIntoVector(5, &pn1.rlist);
	insertIntoVector(3, &pn1.rlist);*/

	/*map<string,int> idTable1;
	idTable1.insert(pair<string,int>(prefix1,1));
	cout << "check idtable result: " << checkIDtable(&prefix1, &idTable1) << endl;*/

	//insertIntoVector(2, &(pn1.getPlist()));

	//vector<int> v1;
	//v1.push_back(3);
	//v1.push_back(4);
	//insertIntoVector(2,&v1);

	//test getMaxMaskAdd
	//cout << "2^6 = " << getMaxMaskAdd(6) << endl;

	//test retrieveIPs
	/*string prefix3 = "123.10.20.0.24";
	vector<unsigned long int> result4 = retrieveIPs(&prefix3);
	for(vector<unsigned long int>::iterator it=result4.begin(); it!= result4.end(); it++)
		cout << *it << endl;*/
	
	//test isOverlapping
	/*string prefix4 = "123.10.20.0.24";
	string prefix5 = "123.10.20.0.20";
	int result5 = isOverlapping(&prefix4, &prefix5);
	cout << "isOverlapping result: " << result5 <<endl;*/

	//vector iteration test
	/*string val1 = "abc";
	string val2 = "def";
	string val3 = "yut";
	vector<string> vals;
	vals.push_back(val1);
	vals.push_back(val2);
	vals.push_back(val3);
	int counter = 0;
	for(vector<string>::iterator it=vals.begin(); it!= vals.end(); it++){
		if(counter > 1)
			break;
		counter++;
		cout << *it << endl;
	}	
	for(vector<int>::size_type i = 1; i != vals.size(); i++) {
		cout << "new iteration: " << vals[i] << endl;
	}*/
	

	//test preprocessPMTable
	/*map<string,pmNode> pmTable2;
	map<string,int> idTable2;
	vector<string> prefixes;
	
	ifstream preFile;
	preFile.open(argv[1], ios_base::in);
	if(preFile.is_open()){
		cout << "file opened!" << endl;
		string line;      //each line is a prefix
		while(preFile.good()){
			getline(preFile,line);
			if(line.length() <= 1) //ignore any empty line.
				break;
			//cout << "current line: " << line << endl;
			prefixes.push_back(line);
		}
	}
	cout << "file closing..." << endl;
	preFile.close();

	preprocessPMTable(&prefixes, &pmTable2, &idTable2);
	cout << "size of pmTable: " << pmTable2.size() << endl;
	cout << "size of idTable: " << idTable2.size() << endl;	
	
	string prefix5 = "1.8.0.0.21";
	map<string,pmNode>::iterator it = pmTable2.find(prefix5);
	if(it != pmTable2.end()){//if found the node
		pmNode curNode = it->second;
		vector<int> plist = curNode.plist;
		for(vector<int>::iterator it1=plist.begin(); it1!= plist.end(); it1++){
			cout << "in plist: " << *it1 << endl;
		}
	}
	string prefix6 = "1.8.0.0.14";
	map<string,pmNode>::iterator it2 = pmTable2.find(prefix6);
	if(it2 != pmTable2.end()){//if found the node
		pmNode curNode = it2->second;
		vector<int> rlist = curNode.rlist;
		for(vector<int>::iterator it1=rlist.begin(); it1!= rlist.end(); it1++){
			cout << "in rlist: " << *it1 << endl;
		}
	}
	string prefix7 = "1.22.0.0.18";
	map<string,pmNode>::iterator it3 = pmTable2.find(prefix7);
	if(it3 != pmTable2.end()){//if found the node
		pmNode curNode = it3->second;
		vector<int> rlist = curNode.rlist;
		for(vector<int>::iterator it1=rlist.begin(); it1!= rlist.end(); it1++){
			cout << "in rlist: " << *it1 << endl;
		}
	}
	string prefix8 = "1.22.32.0.22";
	map<string,pmNode>::iterator it4 = pmTable2.find(prefix8);
	if(it4 != pmTable2.end()){//if found the node
		pmNode curNode = it4->second;
		vector<int> plist = curNode.plist;
		for(vector<int>::iterator it1=plist.begin(); it1!= plist.end(); it1++){
			cout << "in plist: " << *it1 << endl;
		}
	}*/

	//test queryPMTable
	//int qResult = queryPMTable(&prefix5, &pmTable2);
	//cout << "query prefix " << prefix5 << " with result: " << qResult << endl;

	//test searchPMTable
	//vector<int> sResult = searchPMTable(&prefix8, &pmTable2);
	//for(vector<int>::iterator it5=sResult.begin(); it5!= sResult.end(); it5++)
	//	cout << "search result: " << *it5 << endl;

	//longestMatch test
	/*char* filename = "srcIP_acl4_100_add.txt";

	map<string,int> table32bit;          //32-bit hash table
	map<string,bnode> table24bit;        //24-bit hash table
	map<string,bnode> table16bit;        //16-bit hash table
	map<string,bnode> table8bit;         //8-bit hash table
	bitset<256> less8bit;                //less than 8-bit bitmap
	vector<int> less8hops;               //less than 8-bit nexthop container

	vector<string> prefixes;   //container of all prefixes
	
	//parse the prefix file and store prefixes into a vector
	ifstream preFile;
	preFile.open(filename, ios_base::in);
	if(preFile.is_open()){
		cout << "test prefix file opened!" << endl;
		string line;      //each line is a trace

		while(preFile.good()){
			getline(preFile,line);
			if(line.length() <= 1) //ignore any empty line.
				break;
			//cout << "current line: " << line << endl;
			prefixes.push_back(line);
		}
	}
	cout << "test prefix closing..." << endl;
	preFile.close();

	lpmlib::preprocess(&prefixes, &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);

	string traceAdr[] = {"166","138","129","39"};
	string queryResult = lpmlib::query(traceAdr, &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);
	cout << "query result: " << queryResult << endl;*/

	//test convertIP
	/*string target = "166.138.129.39";
	string traceAdr1[] = {"","","",""};
	convertIP(&target, traceAdr1);
	for(int i = 0; i < 4; i++){
		cout << traceAdr1[i] << endl;
	}*/

	//test parseClassifier
	/*vector<string> srcIPs;
	vector<string> dstIPs;
	vector<string> srcPorts;
	vector<string> dstPorts;
	vector<string> types;
	char* classifier = "fw1_50k";
	parseClassifier(&srcIPs, &dstIPs, &srcPorts, &dstPorts, &types, classifier);
	cout << "source IP count: " << srcIPs.size() << endl;
	cout << "destination IP count: " << dstIPs.size() << endl;
	cout << "source port count: " << srcPorts.size() << endl;
	cout << "destination port count: " << dstPorts.size() << endl;
	cout << "type count: " << types.size() << endl; */

	//test stl hash function
	/*string str = "Meet the1 new bo3113ss... haha";
    	hash<string> hash_fn;
    	unsigned int str_hash = hash_fn(str);
 	cout << str_hash << endl;*/

	//test getHashVal
	/*vector<int> intSet;
	intSet.push_back(11);
	intSet.push_back(12);	
	intSet.push_back(16);
	intSet.push_back(14);
	unsigned int resultHashVal = getHashVal(&intSet);
	cout << "hash val: " << resultHashVal << endl;*/

	//test preprocessClassifier
	vector<string> srcIPs;  //values of source IP
	vector<string> dstIPs;  //values of destination IP
	vector<string> srcPorts;  //values of source port
	vector<string> dstPorts;  //values of destination port
	vector<string> types;  //values of types

	char* classifier = argv[1];

	map<string,pmNode> srcIPPMTable;  //pmTable for source IP
	map<string,int> srcIPIDTable;     //IDtable for source IP
	map<string,pmNode> dstIPPMTable;  //pmTable for destination IP
	map<string,int> dstIPIDTable;     //IDtable for destination IP
	map<string,emNode> typeTable;     //pmTable for type field

	map<string,node*> srcIParray[32];
	for(int i = 0; i < 32; i++){
		map<string,node*> table1;  //HERE MAY BE A PROBLEM!!!
		srcIParray[i] = table1;
	}
	map<string,node*> dstIParray[32];
	for(int i = 0; i < 32; i++){
		map<string,node*> table2;  //HERE MAY BE A PROBLEM!!!
		dstIParray[i] = table2;
	}

	map<unsigned int,vector<unsigned int>> ruleHTable;        //the only rule hash table

	preprocessBsol(&srcIPs, &dstIPs, &srcPorts, &dstPorts, &types, classifier, 
		&srcIPPMTable, &srcIPIDTable, &dstIPPMTable, &dstIPIDTable, &typeTable,
		srcIParray, dstIParray,	&ruleHTable);
	
	//test query
	char* traceFile = argv[2];
	lookupBsol(traceFile, &srcIPIDTable, &dstIPIDTable, &typeTable, 
		srcIParray, dstIParray,	&ruleHTable);

	//evaluate memory usage
	//1. individual field tables
	double indMemUsage = 0.0;
	indMemUsage += (sizeof(pmNode) + sizeof(string)) * srcIPPMTable.size() / (double)(1024 * 1024) +
		(sizeof(pmNode) + sizeof(string)) * dstIPPMTable.size() / (double)(1024 * 1024) +
		(sizeof(emNode) + sizeof(string)) * typeTable.size() / (double)(1024 * 1024) +
		(sizeof(int) + sizeof(string)) * srcIPIDTable.size() / (double)(1024 * 1024) +
		(sizeof(int) + sizeof(string)) * dstIPIDTable.size() / (double)(1024 * 1024);
	/*cout << "size of src PM table (MB): " << (sizeof(pmNode) + sizeof(string)) * srcIPPMTable.size() 
		/ (double)(1024 * 1024) << endl;
	cout << "size of dst PM table (MB): " << (sizeof(pmNode) + sizeof(string)) * dstIPPMTable.size() 
		/ (double)(1024 * 1024) << endl;
	cout << "size of type table (MB): " << (sizeof(emNode) + sizeof(string)) * typeTable.size() 
		/ (double)(1024 * 1024) << endl;
	cout << "size of src ID table (MB): " << (sizeof(int) + sizeof(string)) * srcIPIDTable.size() 
		/ (double)(1024 * 1024) << endl;
	cout << "size of dst ID table (MB): " << (sizeof(int) + sizeof(string)) * dstIPIDTable.size() 
		/ (double)(1024 * 1024) << endl;*/
	cout << "total individual table memory usage (MB): " << indMemUsage << endl;

	//2. LPM tables
	double lpmMemUsage = 0.0;
	int markerCounter = 0, prefixCounter = 0, hybridCounter = 0;
	
	for(int i = 0; i < 32; i++){
		map<string,node*> table2 = srcIParray[i];
		
		for(map<string,node*>::iterator it = table2.begin(); it != table2.end(); it++){
			if(it->second->getFlag() == 0)
				prefixCounter++;
			else if(it->second->getFlag() == 1)
				markerCounter++;
			else
				hybridCounter++;
		}
	}

	for(int i = 0; i < 32; i++){
		map<string,node*> table2 = dstIParray[i];
		
		for(map<string,node*>::iterator it = table2.begin(); it != table2.end(); it++){
			if(it->second->getFlag() == 0)
				prefixCounter++;
			else if(it->second->getFlag() == 1)
				markerCounter++;
			else
				hybridCounter++;
		}
	}

	lpmMemUsage += 	sizeof(node) * (prefixCounter + markerCounter + hybridCounter) / (double)(1024 * 1024);
	
	cout << "total LPM table memory usage (MB): " << lpmMemUsage << endl;

	//3. rule hash table
	long countOfEntry = 0;
	for(auto e = ruleHTable.cbegin(); e != ruleHTable.cend(); e++){
		countOfEntry += e->second.size();
	}
	
	//cout << "size of rule hash table: " << ruleHTable.size() << endl;
	cout << "rule hash table memory usage (MB): " << sizeof(unsigned int) * (countOfEntry+ruleHTable.size()) 
		/ (double)(1024 * 1024) << endl;

	//TEST AREA END-----------------------------------------------------------
	
	return 0;
}

