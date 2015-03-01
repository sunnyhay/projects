#include "bsol.h"

using namespace std;

namespace bsollib{

vector<int> getMarkerArray(int mask){
	//Function purpose is to return a vector store each hash table length for insert markers.
	vector<int> markers;
	if(mask == 1 || mask == 2 || mask == 4 || mask == 8 || mask == 16)
		return markers;
	else if(mask == 3)
		markers.push_back(2);		
	else if(mask == 5 || mask == 6)
		markers.push_back(4);
	else if(mask == 7){
		markers.push_back(4);
		markers.push_back(6);
	}else if(mask == 9 || mask == 10 || mask == 12)
		markers.push_back(8);
	else if(mask == 11 || mask == 13 || mask == 14){
		markers.push_back(8);
		markers.push_back(12);
	}else if(mask == 15){
		markers.push_back(8);
		markers.push_back(12);
		markers.push_back(14);
	}else if(mask == 17 || mask == 18 || mask == 20 || mask == 24)
		markers.push_back(16);
	else if(mask == 19){
		markers.push_back(16);
		markers.push_back(18);
	}else if(mask == 21 || mask == 22){
		markers.push_back(16);
		markers.push_back(20);
	}else if(mask == 23){
		markers.push_back(16);
		markers.push_back(20);
		markers.push_back(22);
	}else if(mask == 25 || mask == 26 || mask == 28){
		markers.push_back(16);
		markers.push_back(24);
	}else if(mask == 27){
		markers.push_back(16);
		markers.push_back(24);
		markers.push_back(26);
	}else if(mask == 29 || mask == 30){
		markers.push_back(16);
		markers.push_back(24);
		markers.push_back(28);
	}else if(mask == 31){
		markers.push_back(16);
		markers.push_back(24);
		markers.push_back(28);
		markers.push_back(30);
	}else{//32-bit mask
		markers.push_back(16);
		markers.push_back(24);
		markers.push_back(28);
		markers.push_back(30);
		markers.push_back(31);
	}
	return markers;
}

string getLessPrefix(string prefix, int dstMask){
	//Function purpose is to return a string with less-bit prefix matched with dstMask length from mask standard. Assist setMarker()'s tables.
	string address[4];    //the four parts of ip address
	const char charToSearch = '.';	//current delimitor for prefix
	size_t charPos;
	int begin;      //the position to begin a part of ip
	size_t oldPos;  //the old position for next part
	int index;      //index for the address array
	charPos = prefix.find(charToSearch,0);
	index = 0;
	oldPos = 0;
	begin = 0;
	while(charPos != string::npos){
		address[index++] = prefix.substr(begin,charPos-oldPos);
		size_t charSearchPos = charPos + 1;
		begin = charSearchPos;
		oldPos = charSearchPos;
		charPos = prefix.find(charToSearch,charSearchPos);
	}
	address[index] = prefix.substr(oldPos);
	//now address[4] holds the four IP parts.
	if((dstMask % 8) == 0){
		string s;
		for(int i = 0; i < (dstMask/8); i++)
			s += (address[i] + ".");
		for(int i = (dstMask/8); i < 4; i++)
			s += "0.";
		return s.substr(0,s.length()-1);
	}else{
		int t = atoi(address[dstMask/8].c_str());
		int k = dstMask - (dstMask/8)*8;
		int base = 128, sum = 0;
		for(int i = 0; i < k; i++){
			sum += base;
			base >>= 1;
		}
		//cout << "t: " << t << " with sum: " << sum << endl;
		t = t & sum;
		//cout << t + "" << endl;
		stringstream ss;
		ss << t;				
		//cout << ss.str() << endl;

		string temp[4];
		for(int i = 0; i < 4; i++)
			if(i == (dstMask/8))
				temp[i] = ss.str();
			else
				temp[i] = address[i];
		//cout << temp[0]+temp[1]+temp[2]+temp[3] << endl;
		string s;
		for(int i = 0; i <= (dstMask/8); i++)
			s += (temp[i] + ".");
		for(int i = (dstMask/8)+1; i < 4; i++)
			s += "0.";
		
		return s.substr(0,s.length()-1);		
	}		
}

string seekBestMatch(map<string,node*>* array, int mask, string prefix){
	//Function purpose is to find best matched prefix from less-bits tables compared to mask-bit table.
	int i = mask - 2;  //beginning prefix length.
	while(i >= 0){
		map<string,node*> ht = array[i];
		string r = getLessPrefix(prefix,i+1);
		map<string,node*>::iterator it = ht.find(r);  //locate the key in this table.
			if(it != ht.end()){//found matched node in this table
				node* n = it->second;
				stringstream stringmask;
				stringmask<< n->getMask();
				stringstream stringnexthop;
				stringnexthop<< n->getNexthop();
				return n->getBmp() + " " + stringmask.str() + " " + stringnexthop.str();
			}
		i--;
	}
	return "null";
}

void setMarker(string prefix, int mask, int nexthop, map<string,node*>* array){
	//Function purpose is to set markers in related tables in this array.
	vector<int> tables = getMarkerArray(mask);
	if(tables.size() > 0)
		//cout << "table size: " << tables.size() << endl;
		for(size_t i = 0; i < tables.size(); i++){
			//cout << "table prefix len: " << tables[i] << endl;
			string key = getLessPrefix(prefix,tables[i]);
			map<string,node*>::iterator it = array[tables[i]-1].find(key);  //locate the key in this table.
			if(it != array[tables[i]-1].end()){//found matched node in this table
				//if the matched node is a pure marker and mask of this marker less than argument mask, update the node
				node* current = it->second;  //fetch current matched node in this hash table.
				//cout << current->getBmp() << endl;  //test here
				if(current->getFlag() == 1){//pure marker node, no action, just count the number.
					
				}else if(current->getFlag() == 0){//prefix node, set the flag to be hybrid, 2
					current->setFlag(2);
				}else{//hybrid node, no action, just count the number

				}
			}else{//not exists, create a pure marker node.
				//cout << "come here? " << endl;
				node* marker = new node();  //new marker node.
				marker->setFlag(1);    //pure marker.
				//next search BMP in less-bit tables.
				string bmp = seekBestMatch(array,tables[i],prefix);
				//cout << "bmp all: " << bmp << endl;
				if(bmp != "null"){
					//parse the bmp string
					string parts[3];    
					const char charToSearch = ' ';	//current delimitor
					size_t charPos;
					int begin;      
					size_t oldPos;  
					int index;      
					charPos = bmp.find(charToSearch,0);
					index = 0;
					oldPos = 0;
					begin = 0;
					while(charPos != string::npos){
						parts[index++] = bmp.substr(begin,charPos-oldPos);
						size_t charSearchPos = charPos + 1;
						begin = charSearchPos;
						oldPos = charSearchPos;
						charPos = bmp.find(charToSearch,charSearchPos);
					}
					parts[index] = bmp.substr(oldPos);
					//now parts[3] holds the three parts respectively
					marker->setBmp(parts[0]);
					marker->setMask(atoi(parts[1].c_str()));
					marker->setNexthop(atoi(parts[2].c_str()));
				}else{//no bmp 
					marker->setBmp("0");
					marker->setMask(0);
					marker->setNexthop(-1);
				}
				//insert into correponding table.
				string dmask = getLessPrefix(prefix,tables[i]);
				array[tables[i]-1].insert(pair<string,node*>(dmask,marker));
			}
		}
}

void preprocess(vector<string>* prefixes, map<string,node*>* array){
	string prefix;
	int mask, nexthop;
	string ips[5];  //four parts of ip and mask;
	const char charToSearch = '.';	//current delimitor for line in the prefix file
	const char nexthopChar = ' ';	//current delimitor for nexthop in each line
	size_t charPos;
	int begin;      //the position to begin a part of ip
	size_t oldPos;  //the old position for next part
	int index;      //index for the ips array

	for (size_t i = 0; i < (*prefixes).size (); i++) {
		string line = (*prefixes)[i];
		if(line.length() <= 1) //ignore any empty line.
			continue;
		charPos = line.find(charToSearch,0);
		index = 0;
		oldPos = 0;
		begin = 0;
		while(charPos != string::npos){
			ips[index++] = line.substr(begin,charPos-oldPos);
			//cout << ips[index] << endl;
			size_t charSearchPos = charPos + 1;
			begin = charSearchPos;
			oldPos = charSearchPos;
			charPos = line.find(charToSearch,charSearchPos);
		}
		begin = line.find(nexthopChar,0);
		ips[index] = line.substr(oldPos,begin-oldPos);
		nexthop = atoi(line.substr(begin).c_str());  //the next-hop for this prefix
		//cout << "next hop: " << nexthop << endl;
		/*for(int j = 0; j < 5; j++)
			cout << ips[j] << " ";
		cout << endl; */

		//now ips[5] holds the whole prefix info and nexthop for next-hop of the prefix
		mask = atoi(ips[4].c_str());  //turn the mask string to integer	
		prefix = ips[0]+"."+ips[1]+"."+ips[2]+"."+ips[3];
		//cout << prefix << " " << mask << " " << nexthop << endl;

		//first step: check the correponding table and insert or update a node.
		map<string,node*>::iterator it = array[mask-1].find(prefix);
		if(it != array[mask-1].end()){//exist a node in the table!
			//cout << (*(it->second)).getBmp() << endl;
			node* current = it->second;
			if(current->getFlag() == 1){//pure marker, update it
				current->setFlag(2);
				current->setBmp(prefix);
				current->setMask(mask);
				current->setNexthop(nexthop);
			}
		}else{//create a new prefix node and insert into this table.
			//cout << "need new node" << endl;
			node* current = new node();
			current->setFlag(0);
			current->setBmp(prefix);
			current->setMask(mask);
			current->setNexthop(nexthop);
			array[mask-1].insert(pair<string,node*>(prefix,current));
		}	
		//second step: insert markers into relevant tables.
		setMarker(prefix,mask,nexthop,array);
	}
	cout << "prefix file closing..." << endl;		
}

int lookup(string* address, map<string,node*>* array){
	//Function purpose is to lookup the hash tables and find out the LPM using binary search on prefix length. BSOL namely!
	int max = 32, min = 0;  //never 0
	string bmp = "";
	int mask = -1, nexthop = -1;
	int middle = (max + min)/2;
	//cout << "address: " << address << endl;
	
	while(min <= max && middle > 0 && middle < 32){
		string key = getLessPrefix(*address,middle);
		map<string,node*>::iterator it = array[middle-1].find(key);
		if(it != array[middle-1].end()){//exist a node in the table!
			node* current = it->second;
			if(current->getFlag() == 0){//pure prefix.done!
				//cout << "LPM inside: " << current->getBmp() << endl;
				//cout << "mask inside: " << middle << endl;
				return current->getNexthop();
			}else{//hybrid or pure marker node.
				bmp = current->getBmp();
				mask = current->getMask();
				nexthop = current->getNexthop();
			}
			//direction: longer prefix length tables
			min = middle+1;
		}else{
			max = middle-1;
		}
		middle = (max + min)/2;
		//cout << "current hash table len: " << middle << endl;
	}
	//cout << "LPM out: " << bmp << endl;
	//cout << "mask out: " << mask << endl;
	return nexthop;
}

}
