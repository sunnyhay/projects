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

class bnode{
private:
	string prefix;  //should be an IP address like "202.102.1.0"
	bitset<256> bitmap;
	vector<int> hops;
public:
	void setPrefix(string pfx){prefix = pfx;}
	void setBitmap(bitset<256> bmp){bitmap = bmp;}
	void setHops(vector<int> nexthops){hops = nexthops;}
	string getPrefix(){return prefix;}
	bitset<256> getBitmap(){return bitmap;}
	vector<int> getHops(){return hops;}
};

void setNextHop(vector<int>* hops, int nexthop, int position){
	//Function purpose is to insert the nexthop in the next-hop container on given position.
	vector<int>::iterator it = (*hops).begin();
	(*hops).insert(it+position,nexthop);
}

int getNextHop(vector<int> hops, int position){
	//Function is to fetch the next-hop from the position in next-hop container.
	return hops[position];
}

int lookup(int maskBase, int adrPart, bitset<256>* bitmap){
	//Function purpose is to look up the bitmap for matched LPM for the adress part. return the position of next-hop to be used for nexthop retrieval.
	int maskOffset = maskBase+8;
	int mask = 0, position = 0, realAdr = 0;
	
	for(int i = 1; i <= 8; i++){
		mask = maskOffset-i;
		realAdr = adrPart >> i;
		realAdr = realAdr << i;
		//cout << "current val: " << realAdr << endl;
		position = pow(2,mask-maskBase)-1+realAdr/pow(2,maskOffset-mask);
		if((*bitmap).test(position)){
			int result = 0;
			for(int i = 0; i < position; i++){
				if((*bitmap).test(i))
					result++;
			}
			return result;
		}			
	}
	
	return -1;
}

//get the LPM through bit position.
//return two integers: the mask and the address part
vector<int> getPosition(int maskBase, int adrPart, bitset<256>* bitmap){
	//Function purpose is to look up the bitmap for matched LPM for the adress part. return the position of longest matched bit
	int maskOffset = maskBase+8;
	int mask = 0, position = 0, realAdr = 0;
	vector<int> result;
	
	for(int i = 1; i <= 8; i++){
		mask = maskOffset-i;
		realAdr = adrPart >> i;
		realAdr = realAdr << i;
		//cout << "current val: " << realAdr << endl;
		position = pow(2,mask-maskBase)-1+realAdr/pow(2,maskOffset-mask);
		if((*bitmap).test(position)){
			break;
		}			
	}

	//use the bit position to reverse prefix
	if(position == 0){
		result.push_back(maskBase);
		result.push_back(0);
	}else{
		for(int k = 7; k >= 0; k--){
			if(position >= (pow(2,k)-1)){
				result.push_back(maskBase+k);
				result.push_back((position-(pow(2,k)-1)) * pow(2,8-k));
				break;
			}
		}
	}	
	
	return result;
}

int setBitmap(int mask, int adrPart, bitset<256>* bitmap){
	//Function purpose is to (1) set the proper bit in this bitmap; (2) return the position the next-hop to be used for insertion.
	int maskBase = 0, maskOffset = 0;
	if(mask >= 24){
		maskBase = 24;
		maskOffset = 32;
	}else if(mask >= 16){
		maskBase = 16;
		maskOffset = 24;
	}else{//mask >=8
		maskBase = 8;
		maskOffset = 16;
	}

	int position = pow(2,mask-maskBase)-1+adrPart/pow(2,maskOffset-mask);
	//cout << "position is: " << position << endl;
	(*bitmap).set(position);  //set this position
	
	if(position == 0)
		return 0;
	else{
		int result = 0;
		for(int i = 0; i < position; i++){
			if((*bitmap).test(i))
				result++;
		}
		return result;
	}			
}

void prefixProcess(string* ips, int mask, int nexthop, map<string,bnode>* table){
	//Function purpose is to store each prefix in corresponding hash tables except 32-bit hash table.
	//cout << "table size: " << (*table).size() << endl;
	//cout << ips[0] << " : " << mask << endl;
	int adrPart = atoi(ips[mask/8].c_str());
	string prefix;
	for(int i = 0; i < mask/8; i++){
		prefix += ips[i];
	}
	//cout << prefix << endl;
	map<string,bnode>::iterator it = (*table).find(prefix);
	if(it != (*table).end()){//if found the prefix entry, set its bitmap in this node
		bitset<256> bitmap = it->second.getBitmap();
		int position = setBitmap(mask,adrPart,&bitmap);  //get position of nexthop and set the bitmap
		//cout << "position: " << position << " with prefix: " << prefix << endl;
		//cout << "current bitmap: " << bitmap << endl;
		it->second.setBitmap(bitmap);
		vector<int> hops = it->second.getHops();
		setNextHop(&hops,nexthop,position);   //insert nexthop.		
		it->second.setHops(hops);
	}else{//if no entry exists,create a new bnode
		bitset<256> bitmap;
		vector<int> hops;
		int position = setBitmap(mask,adrPart,&bitmap);
		setNextHop(&hops,nexthop,position);
		/*cout << "new bitmap: " << bitmap << endl;
		for (size_t i = 0; i < hops.size (); i++) {
			cout << hops[i] << endl;
		}*/
		bnode node;
		node.setPrefix(prefix);
		node.setBitmap(bitmap);
		node.setHops(hops);
		(*table).insert(pair<string,bnode>(prefix,node));
		//cout << "now table size: " << (*table).size() << endl;		
	}	
	
}

void preprocess(char* filename, map<string,int>* table32bit, map<string,bnode>* table24bit,
	map<string,bnode>* table16bit, map<string,bnode>* table8bit, bitset<256>* less8bit,
	vector<int>* less8hops){
	//First step: initialize four hash tables for 8, 16, 24 and 32 bit prefix
	string address[5];    //the first four are ip, the last one is mask
	int nexthop1;         //the nexthop information for each prefix
	const char charToSearch = '.';	//current delimitor for line in the prefix file
	const char nexthopChar = ' ';	//current delimitor for nexthop in each line
	size_t charPos;

	//Second step: read prefixes from input
	ifstream preFile;
	preFile.open(filename, ios_base::in);
	if(preFile.is_open()){
		cout << "file opened!" << endl;
		string line;      //each line is a prefix
		int begin;      //the position to begin a part of ip
		size_t oldPos;  //the old position for next part
		int index;      //index for the ips array

		//time counting begin.
		clock_t tPreStart = clock();

		while(preFile.good()){
	//Third step: parse each line in prefix file and store into a five-tuple string array
			getline(preFile,line);
			//cout << "current line: " << line << endl;
			if(line.length() <= 1) //ignore any empty line.
				break;
			charPos = line.find(charToSearch,0);
			index = 0;
			oldPos = 0;
			begin = 0;
			while(charPos != string::npos){
				address[index++] = line.substr(begin,charPos-oldPos);
				//cout << ips[index] << endl;
				size_t charSearchPos = charPos + 1;
				begin = charSearchPos;
				oldPos = charSearchPos;
				charPos = line.find(charToSearch,charSearchPos);
			}
			begin = line.find(nexthopChar,0);
			address[index] = line.substr(oldPos,begin-oldPos);
			nexthop1 = atoi(line.substr(begin).c_str());  //the next-hop for this prefix
			//cout << "next hop: " << nexthop1 << endl;
			/*for(int j = 0; j < 5; j++)
				cout << address[j] << " ";
			cout << endl; */
			
			//now address[5] holds the whole prefix info and nexthop1 for next-hop of the prefix
			int mask = atoi(address[4].c_str());  //turn the mask string to integer
			
			if(mask == 32){
				string ip = address[0]+address[1]+address[2]+address[3];  //32-bit ip
				(*table32bit).insert(pair<string,int>(ip,nexthop1));   //insert the 32-bit prefix
				string pre24 = address[0]+address[1]+address[2];  //24-bit prefix for this 32-bit ip
				//chech whether 24-bit hash table contains the 32-bit prefix
				map<string,bnode>::iterator it = (*table24bit).find(pre24);
				if(it != (*table24bit).end()){//if found the 24-bit prefix entry
					bitset<256> bitmap32 = it->second.getBitmap();
					bitmap32.set(255);
				}else{
					bitset<256> bitmap24;
					vector<int> hops24;
					bitmap24.set(255);
					bnode node24;
					node24.setBitmap(bitmap24);
					node24.setHops(hops24);
					node24.setPrefix(pre24);
					(*table24bit).insert(pair<string,bnode>(pre24,node24));
				}
			}else if(mask >= 24){
				prefixProcess(address,mask,nexthop1,table24bit);
				//cout << "24-bit hash table" << endl;
			}else if(mask >= 16){
				prefixProcess(address,mask,nexthop1,table16bit);
				//cout << "16-bit hash table" << endl;
			}else if(mask >= 8){
				prefixProcess(address,mask,nexthop1,table8bit);
				//cout << "8-bit hash table" << endl;
			}else{//< 8
				//TO BE TESTED.
				int pp = setBitmap(mask,atoi(address[0].c_str()),less8bit);
				setNextHop(less8hops,nexthop1,pp);
				//cout << "rarely happen!" << endl;
			}
		}
		cout << "Preprocessing time elasping: " << (double)(clock() - tPreStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "file closing..." << endl;
		preFile.close();
	}else
		cout << "file open failed" << endl;
	cout << "24-bit hash table size: " << (*table24bit).size() << endl;
	cout << "16-bit hash table size: " << (*table16bit).size() << endl;
	cout << "8-bit hash table size: " << (*table8bit).size() << endl;
	cout << "32-bit hash table size: " << (*table32bit).size() << endl;

	/*cout << "size of bnode: " << sizeof(bnode) << endl;
	cout << "size of bitmap<256>: " << sizeof(bitset<256>) << endl;
	cout << "size of vector<int>: " << sizeof(vector<int>) << endl;
	cout << "size of string: " << sizeof(string) << endl;*/

	cout << "END OF PREPROCESSING" << endl;
}

//given an IP address, return the longest prefix match
string query(string* traceAdr, map<string,int>* table32bit, map<string,bnode>* table24bit,
	map<string,bnode>* table16bit, map<string,bnode>* table8bit, bitset<256>* less8bit,
	vector<int>* less8hops){
	int maskBase;  //mask base to try 24-bit, 16-bit and 8-bit hash tables.
	string tryprefix = "";  //the prefix to be tried consequently.
	
	//firstly try 24-bit hash table
	maskBase = 24;
	tryprefix = traceAdr[0]+traceAdr[1]+traceAdr[2];
	map<string,bnode>::iterator it24 = (*table24bit).find(tryprefix);
	if(it24 != (*table24bit).end()){//if found a matched entry in 24-bit hash table
		//cout << "beginning here" << endl;
		bitset<256> bmp24 = it24->second.getBitmap();
		if(bmp24.test(255)){//try 32-bit hash table
			tryprefix = traceAdr[0]+traceAdr[1]+traceAdr[2]+traceAdr[3];
			map<string,int>::iterator it32 = (*table32bit).find(tryprefix);
			if(it32 != (*table32bit).end()){//if found a matched entry in 32-bit hash table
				//cout << "found 32-bit match: " << it32->second << endl;
				cout << "32 bit here" << endl;
				return traceAdr[0]+"."+traceAdr[1]+"."+traceAdr[2]+"."+traceAdr[3]+".32";				
			}
		}
		vector<int> position24 = getPosition(maskBase,atoi(traceAdr[maskBase/8].c_str()),&bmp24);
		if(!position24.empty()){//found the LPM in 24-bit hash table
			//cout << "next hop in 24-bit table: " << getNextHop(it24->second.getHops(),position24) << endl;
			stringstream ss;  //create a stringstream
			ss << position24[1];  //add number to the stream
			stringstream ss1;
			ss1 << position24[0];
			cout << "24 bit here" << endl;
			return traceAdr[0]+"."+traceAdr[1]+"."+traceAdr[2]+"."+ss.str()+"."+ss1.str();			
		}				
	}
	//secondly try 16-bit hash table
	maskBase = 16;
	tryprefix = traceAdr[0]+traceAdr[1];
	map<string,bnode>::iterator it16 = (*table16bit).find(tryprefix);
	if(it16 != (*table16bit).end()){//if found a matched entry in 16-bit hash table
		bitset<256> bmp16 = it16->second.getBitmap();
		vector<int> position16 = getPosition(maskBase,atoi(traceAdr[maskBase/8].c_str()),&bmp16);
		if(!position16.empty()){//found the LPM in 16-bit hash table
			//cout << "next hop in 16-bit table: " << getNextHop(it16->second.getHops(),position16) << endl;
			stringstream ss;  //create a stringstream
			ss << position16[1];  //add number to the stream
			stringstream ss1;
			ss1 << position16[0];
			cout << "16 bit here" << endl;
			return traceAdr[0]+"."+traceAdr[1]+"."+ss.str()+".0."+ss1.str();			
		}
	}
	//then try 8-bit hash table
	maskBase = 8;
	tryprefix = traceAdr[0];
	map<string,bnode>::iterator it8 = (*table8bit).find(tryprefix);
	if(it8 != (*table8bit).end()){//if found a matched entry in 8-bit hash table
		bitset<256> bmp8 = it8->second.getBitmap();
		vector<int> position8 = getPosition(maskBase,atoi(traceAdr[maskBase/8].c_str()),&bmp8);
		if(!position8.empty()){//found the LPM in 8-bit hash table
			//cout << "next hop in 8-bit table: " << getNextHop(it8->second.getHops(),position8) << endl;
			stringstream ss;  //create a stringstream
			ss << position8[1];  //add number to the stream
			stringstream ss1;
			ss1 << position8[0];
			cout << "8 bit here" << endl;
			return traceAdr[0]+"."+ss.str()+".0.0."+ss1.str();			
		}
	}
	//last try less than 8-bit bitmap
	maskBase = 0;
	vector<int> positionless8 = getPosition(maskBase,atoi(traceAdr[maskBase/8].c_str()),less8bit);
	if(!positionless8.empty()){//found the LPM in less than 8-bit bitmap
		//cout << "next hop in less than 8-bit table: " << getNextHop(less8hops,positionless8) << endl;
		stringstream ss;  //create a stringstream
		ss << positionless8[1];  //add number to the stream
		stringstream ss1;
		ss1 << positionless8[0];
		cout << "less 8 bit here" << endl;
		return ss.str()+".0.0.0."+ss1.str();	
	}
	
	//cout << "fail to find any LPM!" << endl;
	return "none";
}

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
	if(argc != 3){
		cerr << "Usage: " << argv[0] << " with routing table and lookup files" << endl;
		return 1;
	}

	map<string,int> table32bit;          //32-bit hash table
	map<string,bnode> table24bit;        //24-bit hash table
	map<string,bnode> table16bit;        //16-bit hash table
	map<string,bnode> table8bit;         //8-bit hash table
	bitset<256> less8bit;                //less than 8-bit bitmap
	vector<int> less8hops;               //less than 8-bit nexthop container

	//PREPROCESS START-------------------------------------------------------

	preprocess(argv[1], &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);

	string traceAdr[] = {"1","12","0","4"};
	string queryResult = query(traceAdr, &table32bit, &table24bit, &table16bit, &table8bit, 
		&less8bit, &less8hops);
	cout << "query result: " << queryResult << endl;
	
	//PREPROCESS END------------------------------------------------------------------

	return 0;
}

