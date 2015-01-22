#include<iostream>
#include<bitset>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <time.h>
using namespace std;

class tnode{
private:
	bitset<16> ibmp;   //internal bitmap for other node
	bitset<16> ebmp;   //external bitmap for other node
	vector<tnode*> children;  //child pointer dynamic array for other node
	vector<int> hops;  //next hop array
public:
	void setIbmp(bitset<16> ibitmap){ibmp = ibitmap;}
	void setEbmp(bitset<16> ebitmap){ebmp = ebitmap;}
	void setChildren(vector<tnode*> childs){children = childs;}
	void setHops(vector<int> nexthops){hops = nexthops;}
	bitset<16> getIbmp(){return ibmp;}
	bitset<16> getEbmp(){return ebmp;}
	vector<tnode*> getChildren(){return children;}
	vector<int> getHops(){return hops;}
};


class rnode{
private:
	bitset<16384> ibmp;   //internal bitmap for root node
	bitset<16384> ebmp;   //external bitmap for root node
	vector<tnode*> children;  //child pointer dynamic array for root node
	vector<int> hops;     //next hop array
public:
	void setIbmp(bitset<16384> ibitmap){ibmp = ibitmap;}
	void setEbmp(bitset<16384> ebitmap){ebmp = ebitmap;}
	void setChildren(vector<tnode*> childs){children = childs;}
	void setHops(vector<int> nexthops){hops = nexthops;}
	bitset<16384> getIbmp(){return ibmp;}
	bitset<16384> getEbmp(){return ebmp;}
	vector<tnode*> getChildren(){return children;}
	vector<int> getHops(){return hops;}
};

int bitPosCross(int maskBase, int mask, int firstpart, int secondpart){
	//Function purpose is to retun the internal bitmap's position for given address. Assist setbitmap16.
	//cout << "in poscross fp: " << firstpart << endl;
	//cout << "in poscross sp: " << secondpart << endl;
	int gap = mask-maskBase;
	//cout << "gap: " << gap << endl;
	if(gap == 0){
		return 0;
	}else if(gap == 1){//check the 15th or 23th bit, the second bit in firstpart
		if(firstpart&2 == 0)
			return 1;
		else
			return 2;
	}else if(gap == 2){//check the 15th and 16th or 23th and 24th bits, the second and last bits in firstpart
		if((firstpart&3) == 0)
			return 3;
		else if((firstpart&3) == 1)
			return 4;
		else if((firstpart&3) == 2)
			return 5;
		else
			return 6;
	}else{//gap == 3, //check 15th,16th and 17th or 23th, 24th and 25th bits, the second and last bits in firstpart and the only bit in secondpart
		//cout << "fp AND 3: " << (firstpart&3) << endl;
		if((firstpart&3 == 0) && secondpart == 0)
			return 7;
		else if((firstpart&3) == 0 && secondpart == 1)
			return 8;
		else if((firstpart&3) == 1 && secondpart == 0)
			return 9;
		else if((firstpart&3) == 1 && secondpart == 1)
			return 10;
		else if((firstpart&3) == 2 && secondpart == 0)
			return 11;
		else if((firstpart&3) == 2 && secondpart == 1)
			return 12;
		else if((firstpart&3) == 3 && secondpart == 0)
			return 13;
		else
			return 14;
	}
}

int bitPosDirect(int maskBase, int mask, int firstpart){
	//Function purpose is to retun the internal bitmap's position for given address. Assist setbitmap16.
	int gap = mask - maskBase;
	//cout << "gap: " << gap << endl;
	//cout << "fp: " << firstpart << endl;
	if(gap == 0){
		return 0;
	}else if(gap == 1){
		//cout << "fp AND 4: " << (firstpart&4) << endl;
		if(maskBase == 30){
			if((firstpart & 2) == 0)
				return 1;
			else
				return 2;			
		}
		if((firstpart & 4) == 0)
			return 1;
		else
			return 2;			
	}else if(gap == 2){
		if(maskBase == 30){
			if((firstpart & 3) == 0)
				return 3;
			else if((firstpart & 3) == 1)
				return 4;
			else if((firstpart & 3) == 2)
				return 5;
			else
				return 6;	
		}
		if((firstpart & 6) == 0)
			return 3;
		else if((firstpart & 6) == 2)
			return 4;
		else if((firstpart & 6) == 4)
			return 5;
		else
			return 6;
	}else{//gap == 3
		if((firstpart & 7) == 0)
			return 7;
		else if((firstpart & 7) == 1)
			return 8;
		else if((firstpart & 7) == 2)
			return 9;
		else if((firstpart & 7) == 3)
			return 10;
		else if((firstpart & 7) == 4)
			return 11;
		else if((firstpart & 7) == 5)
			return 12;
		else if((firstpart & 7) == 6)
			return 13;
		else
			return 14;
	}
}

int bitPosChildCross(int firstpart, int secondpart){
	//Function purpose is to return the position to be inserted into child array for non-root nodes. Assist setchild16() method.
	int sum = 0, cvalue = 2, multiple = 1;
	if(((cvalue & firstpart) >> 1) == 1)
		sum += multiple;
	cvalue >>= 1;
	multiple *= 2;
	if((cvalue & firstpart) == 1)
		sum += multiple;
	cvalue = 128;
	multiple *= 2;
	if(((cvalue & secondpart) >> 7) == 1)
		sum += multiple;
	cvalue >>=1;
	multiple *=2;
	if(((cvalue & secondpart) >> 6) == 1)
		sum += multiple;
	return sum;	
}

int bitPosChildDirect(int firstpart){
	//Function purpose is to return the position to be inserted into child array for non-root nodes. Assist setchild16() method.
	int sum = 0, cvalue = 32, multiple = 1;
	for(int i = 5; i > 1; i--){//four middle bits inspection
		if(((cvalue & firstpart) >> i) == 1)
			sum += multiple;
		cvalue >>= 1;
		multiple *= 2;
	}
	return sum;
}

int getChildPosInChild(int level, string* prefix){
	//Function purpose is to return prefix position in one child's external bitmap.
	//range division level: 1, 14-17; 2, 18-21; 3, 22-25; 4, 26-29; 5, 30-32.
	//search range: 1, 15-18; 2, 19-22; 3, 23-26; 4, 27-30. for level 5 no need to do this.
	//prefix[1]: 9-16; prefix[2]: 17-24; prefix[3]: 25-32.
	int position = -1;  //position to be set in the bitmap.	
	int firstpart = 0;   //first part of integer
	int secondpart = 0;  //second part of integer
		
	if(level == 1){//14-17 
		firstpart = atoi(prefix[1].c_str());
		secondpart = atoi(prefix[2].c_str());
		//cout << "firstpart: " << firstpart << endl;
		//cout << "secondpart: " << secondpart << endl;
		position = bitPosChildCross(firstpart,secondpart);
	}else if(level == 2 || level == 4){//18-21 
		firstpart = atoi(prefix[2].c_str());
		//cout << firstpart << endl;
		position = bitPosChildDirect(firstpart);
	}else if(level == 3){//22-25
		firstpart = atoi(prefix[2].c_str());
		secondpart = atoi(prefix[3].c_str());
		position = bitPosChildCross(firstpart,secondpart);
	}else if(level == 4){//26-29
		firstpart = atoi(prefix[3].c_str());
		position = bitPosChildDirect(firstpart);
	}else{
		cout << "impossible level value in setchild16() method" << endl;
	}
	//cout << "position is: " << position<< endl;
	return position;
}

int getChildPosInRoot(string* prefix){
	//Function purpose is to return prefix position in root's external bitmap.	
	int firstpart = atoi(prefix[0].c_str());
	int	secondpart = atoi(prefix[1].c_str());
	int cvalue = 128, multiple = 1;
	int sum = 0;	
	int maskBase = 1;
	while(maskBase <= 14){
		if(maskBase == 8){
			if((cvalue & firstpart) == 1){
				sum += multiple;
			}			
			cvalue = 128;
		}else if(maskBase > 8){
			if(((cvalue & secondpart) >> (16-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}else{
			if(((cvalue & firstpart) >> (8-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}
		multiple *= 2;
		maskBase++;
	}
	//cout << "position is: " << sum << endl;
	return sum;
}

int getBitPosInChild(int level, int mask, string* prefix){
	//Function purpose is to return the position of this prefix with mask in internal bitmap of any child node.
	int position = 0;  //position to be set in the bitmap.
	int firstpart = 0;   //first part of integer
	int secondpart = 0;  //second part of integer
	int maskBase = 0;    

	if(mask >= 14 && mask <= 17)
		maskBase = 14;
	else if(mask >= 18 && mask <= 21)
		maskBase = 18;
	else if(mask >= 22 && mask <= 25)
		maskBase = 22;
	else if(mask >= 26 && mask <= 29)
		maskBase = 26;
	else
		maskBase = 30;
	
	if(level == 1){//14-17
		firstpart = atoi(prefix[1].c_str());
		secondpart = atoi(prefix[2].c_str());
		firstpart &= 7;
		//cout << "firstpart: " << firstpart << endl;
		secondpart >>= 7;
		//cout << "secondpart: " << secondpart << endl;
		position = bitPosCross(maskBase,mask,firstpart,secondpart);		
	}else if(level == 2){//18-21
		firstpart = atoi(prefix[2].c_str());
		firstpart &= 120;
		firstpart >>= 3;
		//cout << firstpart << endl;
		position = bitPosDirect(maskBase,mask,firstpart);
	}else if(level == 3){
		firstpart = atoi(prefix[2].c_str());
		secondpart = atoi(prefix[3].c_str());
		firstpart &= 7;
		//cout << firstpart << endl;
		secondpart >>= 7;
		//cout << secondpart << endl;
		position = bitPosCross(maskBase,mask,firstpart,secondpart);	
	}else if(level == 4 || level == 5){
		firstpart = atoi(prefix[3].c_str());
		if(level == 4){
			firstpart &= 120;
			firstpart >>= 3;
		}
		else
			firstpart &= 7;
		//cout << firstpart << endl;
		position = bitPosDirect(maskBase,mask,firstpart);		
	}else{
		//cout << "setbitmap16 inside level: " << level << endl;
		cout << "impossible level value in setTbitmap() method" << endl;
	}
	//cout << "position is: " << position<< endl;
	return position;
}

int getBitPosInRoot(int mask, string* prefix){
	//Function purpose is to return the position of this prefix with mask in root internal bitmap
	int firstpart = atoi(prefix[0].c_str());
	int	secondpart = atoi(prefix[1].c_str());
	//cout << "fp: " << firstpart << endl;
	//cout << "sp: " << secondpart << endl;
	int cvalue = 128, multiple = 1;
	int sum = 0;
	int base = pow(2,mask)-1;
	//cout << "base: " << base << endl;
	int maskBase = 1;
	while(maskBase <= mask){
		if(maskBase == 8){
			if((cvalue & firstpart) == 1){
				sum += multiple;
			}			
			cvalue = 128;
		}else if(maskBase > 8){
			if(((cvalue & secondpart) >> (16-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}else{
			if(((cvalue & firstpart) >> (8-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}
		multiple *= 2;
		maskBase++;
	}
	//now base+sum is the position to be set in the bitmap.
	return base+sum;
}

int setchild16(int level, string* prefix, bitset<16>* bitmap){
	//Function purpose is to (1) set the bit in external bitmap; (2) return the position the child node to be inserted in children array.	
	int position = 0;  //position to be set in the bitmap.
	int result = 0;    //the position of nexthop to be returned.

	position = getChildPosInChild(level,prefix);
	(*bitmap).set(position);  //set this position
	for(int i = 0; i < position; i++){
		if((*bitmap).test(i))
			result++;
	}
	return result;
}

int setbitmap16(int level, int mask, string* prefix, bitset<16>* bitmap){
	//Function purpose is to (1) set the bit in internal bitmap; (2) return the position the next-hop to be used for insertion.
	//range division level: 1, 14-17; 2, 18-21; 3, 22-25; 4, 26-29; 5, 30-32.
	//prefix[1]: 9-16; prefix[2]: 17-24; prefix[3]: 25-32.
	int position = 0;  //position to be set in the bitmap.
	int result = 0;    //the position of nexthop to be returned.
	int firstpart = 0;   //first part of integer
	int secondpart = 0;  //second part of integer
	int maskBase = 0;    

	if(mask >= 14 && mask <= 17)
		maskBase = 14;
	else if(mask >= 18 && mask <= 21)
		maskBase = 18;
	else if(mask >= 22 && mask <= 25)
		maskBase = 22;
	else if(mask >= 26 && mask <= 29)
		maskBase = 26;
	else
		maskBase = 30;
	
	if(level == 1){//14-17
		firstpart = atoi(prefix[1].c_str());
		secondpart = atoi(prefix[2].c_str());
		firstpart &= 7;
		//cout << "firstpart: " << firstpart << endl;
		secondpart >>= 7;
		//cout << "secondpart: " << secondpart << endl;
		position = bitPosCross(maskBase,mask,firstpart,secondpart);		
	}else if(level == 2){//18-21
		firstpart = atoi(prefix[2].c_str());
		firstpart &= 120;
		firstpart >>= 3;
		//cout << firstpart << endl;
		position = bitPosDirect(maskBase,mask,firstpart);
	}else if(level == 3){
		firstpart = atoi(prefix[2].c_str());
		secondpart = atoi(prefix[3].c_str());
		firstpart &= 7;
		//cout << firstpart << endl;
		secondpart >>= 7;
		//cout << secondpart << endl;
		position = bitPosCross(maskBase,mask,firstpart,secondpart);	
	}else if(level == 4 || level == 5){
		firstpart = atoi(prefix[3].c_str());
		if(level == 4){
			firstpart &= 120;
			firstpart >>= 3;
		}
		else
			firstpart &= 7;
		//cout << firstpart << endl;
		position = bitPosDirect(maskBase,mask,firstpart);		
	}else{
		//cout << "setbitmap16 inside level: " << level << endl;
		cout << "impossible level value in setTbitmap() method" << endl;
	}
	//cout << "position is: " << position<< endl;
	(*bitmap).set(position);  //set this position
	for(int i = 0; i < position; i++){
		if((*bitmap).test(i))
			result++;
	}
	return result;
}

int setRnodeChild(string* prefix, bitset<16384>* bitmap){
	//Function purpose is to (1) set the proper bit in this bitmap; (2) return the position the child node to be used for insertion. All for root node.
	//mask is fixed to 14.	
	int position = getChildPosInRoot(prefix);
	(*bitmap).set(position);  //set this position
	
	int result = 0;
	for(int i = 0; i < position; i++){
		if((*bitmap).test(i))
			result++;
	}
	return result;
}

int setRnodeBmp(int mask, string* prefix, bitset<16384>* bitmap){
	//Function purpose is to (1) set the proper bit in this bitmap; (2) return the position the next-hop to be used for insertion. All for root node.
	//mask from 1 to 13
	//base position is 2^mask-1, then AND a proper binary value to get the relative position.
	int firstpart = atoi(prefix[0].c_str());
	int	secondpart = atoi(prefix[1].c_str());
	//cout << "fp: " << firstpart << endl;
	//cout << "sp: " << secondpart << endl;
	int cvalue = 128, multiple = 1;
	int sum = 0;
	int base = pow(2,mask)-1;
	//cout << "base: " << base << endl;
	int maskBase = 1;
	while(maskBase <= mask){
		if(maskBase == 8){
			if((cvalue & firstpart) == 1){
				sum += multiple;
			}			
			cvalue = 128;
		}else if(maskBase > 8){
			if(((cvalue & secondpart) >> (16-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}else{
			if(((cvalue & firstpart) >> (8-maskBase)) == 1){
				sum += multiple;
			}			
			cvalue >>= 1;
		}
		multiple *= 2;
		maskBase++;
	}
	//now base+sum is the position to be set in the bitmap.
	int position = base+sum;
	//cout << "position is: " << position << endl;
	(*bitmap).set(position);  //set this position
	
	int result = 0;
	for(int i = 0; i < position; i++){
		if((*bitmap).test(i))
			result++;
	}
	return result;
				
}

void setNextHop(vector<int>* hops, int nexthop, int position){
	//Function purpose is to insert the nexthop in the next-hop container on given position.
	vector<int>::iterator it = (*hops).begin();
	(*hops).insert(it+position,nexthop);
}

int getNextHop(vector<int> hops, int position){
	//Function is to fetch the next-hop from the position in next-hop container.
	return hops[position];
}

void setChildren(vector<tnode*>* children, tnode* child, int position){
	//Function purpose is to insert the child in the children array on given position.
	vector<tnode*>::iterator it = (*children).begin();
	(*children).insert(it+position,child);
}

tnode* getChild(vector<tnode*> children, int position){
	//Function purpose is to return the child from the position in children array.
	//cout << "children size: " << children.size() << endl;
	return children[position];
}

rnode preprocess(char* filename){
	//Function purpose is to return a root node. ips and mask are temporary arguments. 
	//Parse each line of prefix in routing table file and store each prefix in the tree bitmap.
	string ips[5];  //four parts of ip and mask;
	int nexthop;    //next hop for each prefix
	bitset<16384> ibmp;  //internal bitmap for root node.
	bitset<16384> ebmp;  //external bitmap for root node.
	vector<tnode*> children;   //children array for root node.
	vector<int> hops;          //next hop array for root node
	const char charToSearch = '.';	//current delimitor for line in the prefix file
	const char nexthopChar = ' ';	//current delimitor for nexthop in each line
	size_t charPos;

	//counters area
	int childNodeCounter = 0;   //counter for children nodes.
	
	ifstream preFile;
	preFile.open(filename, ios_base::in);
	if(preFile.is_open()){
		cout << "prefix file opened!" << endl;
		string line;      //each line is a prefix
		int begin;      //the position to begin a part of ip
		size_t oldPos;  //the old position for next part
		int index;      //index for the ips array

		//time counting begin.
		clock_t tPreStart = clock();

		while(preFile.good()){
			getline(preFile,line);
			//cout << "current line: " << line << endl;
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
			//cout << "next hop: " << nexthop1 << endl;
			/*for(int j = 0; j < 5; j++)
				cout << ips[j] << " ";
			cout << endl; */
			
			//now ips[5] holds the whole prefix info and nexthop for next-hop of the prefix
			int mask = atoi(ips[4].c_str());  //turn the mask string to integer	
			int position = -1;    //position of current child node in external bitmap of current parent.

			if(mask <= 13){//only handle internal bitmap.
				int position = setRnodeBmp(mask,ips,&ibmp);
				setNextHop(&hops,nexthop,position);
			}else{//find where the prefix should be stored.
				int t = 14;  //the beginning mask from root node.
				int level = 0;  //level for mask from 1 to 5.
				tnode* current = new tnode();  //current node's pointer.
				while(mask >= t){
					//cout << "now t: " << t << endl;
					if(t == 14){//root node case
						position = getChildPosInRoot(ips);
						//cout << "this position: " << position << endl;
						if(ebmp.test(position)){//exist such child node in external bitmap of root
							//cout << "come here" << endl;
							int result = 0;    //count for child position in the root external bitmap.
							for(int i = 0; i < position; i++){
								if(ebmp.test(i))
								result++;
							}
							current = getChild(children,result);
							//cout << "end here" << endl;
						}else{//not exist. create a 14-bit child node for root and set ebmp and children.
							//create the node in level 1: mask 14-17.
							childNodeCounter++;
							
							bitset<16> ibmp1;
							bitset<16> ebmp1;
							vector<tnode*> children1;
							vector<int> hops1;
							tnode* tn = new tnode();
							tn->setIbmp(ibmp1);
							tn->setEbmp(ebmp1);
							tn->setChildren(children1);
							tn->setHops(hops1);

							//set external bitmap of root for this node and add the node into the children array of root.
							int p1 = setRnodeChild(ips,&ebmp);
							setChildren(&children,tn,p1);
							current = tn;
							//cout << "first child node address: " << current << endl;
						}			
					}else{//non-root node case
						position = getChildPosInChild(level,ips);
						if(current->getEbmp().test(position)){//exist such child node in external bitmap of some node
							int result = 0;    //count for child position in the root external bitmap.
							for(int i = 0; i < position; i++){
								if(current->getEbmp().test(i))
								result++;
							}
							current = getChild(current->getChildren(),result);
						}else{//not exist. create a child node for current and set ebmp and children for current.
							//cout << "should be here" << endl;
							childNodeCounter++;
							
							bitset<16> ibmp1;
							bitset<16> ebmp1;
							vector<tnode*> children1;
							vector<int> hops1;
							tnode* tn2 = new tnode();
							tn2->setIbmp(ibmp1);
							tn2->setEbmp(ebmp1);
							tn2->setChildren(children1);
							tn2->setHops(hops1);
							//cout << "original new node address: " << tn2 << endl;

							bitset<16> parentEbmp = current->getEbmp();	
							//cout << "current level: " << level << endl;
							int p1 = setchild16(level,ips,&parentEbmp);
							current->setEbmp(parentEbmp);
							//cout << "parent ebmp: " << (*current).getEbmp() << endl;
							vector<tnode*> pchildren = current->getChildren();
							setChildren(&pchildren,tn2,p1);
							current->setChildren(pchildren);
							//cout << "parent children size: " << (*current).getChildren().size() << endl;
							//cout << "parent address: " << current << endl;
					
							current = tn2;
							//cout << "second-level child address: " << tn2<< endl;
						}
					}
					t += 4;
					level++;
				}
				//now set current node's ibmp and hops.
				bitset<16> cbmp = current->getIbmp();
				//cout << "current ibmp: " << cbmp << endl;
				vector<int> chops = current->getHops();
				//cout << "current's hop number: " << chops.size() << endl;
				int p2 = setbitmap16(level,mask,ips,&cbmp);
				//cout << "current ibmp: " << cbmp << endl;
				setNextHop(&chops,nexthop,p2);
				//cout << "current's hop number: " << chops.size() << endl;
				//cout << "current's hop: " << chops[0] << endl;
				current->setIbmp(cbmp);
				current->setHops(chops);
			}
		}
		cout << "Preprocessing time elasping: " << (double)(clock() - tPreStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "children node count: " << childNodeCounter << endl;
		cout << "prefix file closing..." << endl;
		preFile.close();
	}
	
	rnode root;
	root.setIbmp(ibmp);
	root.setEbmp(ebmp);
	root.setChildren(children);
	root.setHops(hops);
	return root;
}

int lookup(rnode* root, string address){
	//Function purpose is to find LPM in the tree bitmap structure for given address.
	string prefix[4];  //four parts of IP address.
	const char charToSearch = '.';	//current delimitor for prefix
	size_t charPos;
	int begin;      //the position to begin a part of ip
	size_t oldPos;  //the old position for next part
	int index;      //index for the address array
	charPos = address.find(charToSearch,0);
	index = 0;
	oldPos = 0;
	begin = 0;
	while(charPos != string::npos){
		prefix[index++] = address.substr(begin,charPos-oldPos);
		size_t charSearchPos = charPos + 1;
		begin = charSearchPos;
		oldPos = charSearchPos;
		charPos = address.find(charToSearch,charSearchPos);
	}
	prefix[index] = address.substr(oldPos);
	//cout << prefix[0]+prefix[1]+prefix[2]+prefix[3] << endl;
	//now prefix[] holds the address.
	vector<int> finalhops;    //the lazily fetched nexthop.
	int finalposition = -1;        //the position to finally fetch nexthop from finalhops.
	

	//first step: test if root's internal bitmap has some matched bit set for this address.
	int maskChkInRoot = 13;
	int bitPosInRoot = -1;
	while(maskChkInRoot> 0){
		bitPosInRoot = getBitPosInRoot(maskChkInRoot,prefix);
		if(root->getIbmp().test(bitPosInRoot)){//found bit set in root internal bitmap
			finalhops = root->getHops();
			finalposition = bitPosInRoot;
			break;
		}
		maskChkInRoot--;
	}
	//cout << "reach here" << endl;
	
	//second step: check external bitmap of root, if any child node matched, continue.
	int childPosInRoot = getChildPosInRoot(prefix);  //position of prefix in external bitmap
	if(root->getEbmp().test(childPosInRoot)){//if the child node exists
		bitset<16> childIbmp;   //the internal bitmap for child node.
		bool flag = false;               //actually exist LPM in child node
	
		//fetch the child node from children array with this position by counting 1 bits
		tnode* current = new tnode();  //current node's pointer.
		int result = 0;    //count for child position in the root external bitmap.
		for(int i = 0; i < childPosInRoot; i++){
		if(root->getEbmp().test(i))
			result++;
		}
		current = getChild(root->getChildren(),result);

		//third step: iterate current node until no bit set in its external bitmap. meanwhile record LPM
		int maskChkInChild = 17;  //level 1 child node at the loop's beginning
		int bitPosInChild = -1;
		int level = 1;
		while(current != NULL && level < 6){
			for(int i = 0; i < 4; i++){
				bitPosInChild = getBitPosInChild(level,maskChkInChild-i,prefix);
				if(current->getIbmp().test(bitPosInChild)){//found bit set in current internal bitmap
					finalhops = current->getHops();
					finalposition = bitPosInChild;
					childIbmp = current->getIbmp();
					flag = true;
					break;
				}
			}
			//fourth step: check external bitmap of current, if any child node matched, continue the loop. otherwise set current null.
			//if this is the bottom level (5), ignore this.
			if(level < 5){
				int childPosInChild = getChildPosInChild(level,prefix);
				if(current->getEbmp().test(childPosInChild)){//if the child node exists
					int re = 0;  //count for child position in current's external bitmap.
					for(int i = 0; i < childPosInChild; i++){
						if(current->getEbmp().test(i))
							re++;
						}
					current = getChild(current->getChildren(),re);				
				}else
					current = NULL;
			}
			
			level++;
			maskChkInChild += 4;
		}
		if(flag){//should return here since child node contains LPM for this address
			int res = 0;    //count for position in the child internal bitmap.
			for(int i = 0; i < finalposition; i++){
			if(childIbmp.test(i))			
				res++;
			}
			return getNextHop(finalhops,res);
		}
	}

	//last step: lazily fetch the nexthop 
	if(finalposition > 0){
		int resu = 0;  //count for position in root internal bitmap.
		for(int i = 0; i < finalposition; i++){
			if(root->getIbmp().test(i))			
				resu++;
			}
		return getNextHop(finalhops,resu);
	}else
		return -1;
}

int main(int argc, char* argv[])
{
	//TEST------------------------------------------------------------------------
	bitset<16> ibmp1;
	ibmp1.set(0);
	bitset<16> ebmp1;
	ebmp1.set(15);
	vector<tnode*> children1;
	vector<int> hops1;
	hops1.push_back(15);
	hops1.push_back(77);
	tnode tn1;
	tn1.setIbmp(ibmp1);
	tn1.setEbmp(ebmp1);
	tn1.setChildren(children1);
	tn1.setHops(hops1);
	
	//cout << "test ibmp: " << tn1.getIbmp() << endl;
	//cout << "test ebmp: " << tn1.getEbmp() << endl;
	//cout << "hops vector size: " << tn1.getHops().size() << endl;

	bitset<16384> ibmp2;
	//ibmp2.set(0);
	bitset<16384> ebmp2;
	ebmp2.set(0);
	vector<tnode*> children2;
	children2.push_back(&tn1);
	vector<int> hops2;
	hops2.push_back(10);
	hops2.push_back(11);
	hops2.push_back(12);
	rnode root;
	root.setIbmp(ibmp2);
	root.setEbmp(ebmp2);
	root.setChildren(children2);
	root.setHops(hops2);

	/*cout << "test ibmp: " << root.getIbmp() << endl;
	cout << "test ebmp: " << root.getEbmp() << endl;
	cout << "hops vector size: " << root.getHops().size() << endl;
	cout << "children vector size: " << root.getChildren().size() << endl;
	cout << "child info: " << (*root.getChildren()[0]).getHops().size() << endl;
	cout << "child info: " << (*root.getChildren()[0]).getEbmp() << endl; */

	//Preprocess test case 1. 
	string prefix1[5] = {"255", "255", "255", "255", "32"};
	//int level1 = 4;
	//cout << setbitmap16(level1,31,prefix1,&ibmp1) << endl;
	//setRnodeBmp(12,prefix1,&ibmp2);
	//setRnodeChild(prefix1,&ibmp2);
	//cout << setchild16(level1,prefix1,&ibmp1) << endl;
	int nexthop1 = 77;
	/*rnode root1 = preprocess(prefix1,nexthop1);
	cout << "root's children number: " << root1.getChildren().size()<< endl;
	cout << root1.getIbmp() << endl;
	cout << root1.getEbmp() << endl;
	tnode* tn2 = root1.getChildren()[0];
	cout << "first level's child number: " << tn2->getChildren().size() << endl;
	cout << "first level's ibmp: " << tn2->getIbmp()<< endl;
	cout << "first level's ebmp: " << tn2->getEbmp()<< endl;
	cout << "first level's hops size: " << tn2->getHops().size()<< endl;
	cout << "root's first child address: " << tn2 << endl;
	tnode* tn3 = tn2->getChildren()[0];
	cout << "second level's child number: " << tn3->getChildren().size() << endl;
	cout << "second level's ibmp: " << tn3->getIbmp()<< endl;
	cout << "second level's ebmp: " << tn3->getEbmp()<< endl;
	cout << "second level's hops size: " << tn3->getHops().size()<< endl;
	tnode* tn4 = tn3->getChildren()[0];
	cout << "third level's child number: " << tn4->getChildren().size() << endl;
	cout << "third level's ibmp: " << tn4->getIbmp()<< endl;
	cout << "third level's ebmp: " << tn4->getEbmp()<< endl;
	cout << "third level's hops size: " << tn4->getHops().size()<< endl;
	tnode* tn5 = tn4->getChildren()[0];
	cout << "fourth level's child number: " << tn5->getChildren().size() << endl;
	cout << "fourth level's ibmp: " << tn5->getIbmp()<< endl;
	cout << "fourth level's ebmp: " << tn5->getEbmp()<< endl;
	cout << "fourth level's hops size: " << tn5->getHops().size()<< endl;
	tnode* tn6 = tn5->getChildren()[0];
	cout << "bottom level's child number: " << tn6->getChildren().size() << endl;
	cout << "bottom level's ibmp: " << tn6->getIbmp()<< endl;
	cout << "bottom level's ebmp: " << tn6->getEbmp()<< endl;
	cout << "bottom level's hops size: " << tn6->getHops().size()<< endl;
	*/
	
	//TEST OVER-------------------------------------------------------------------

	//PREPROCESS START-----------------------------------------------------------
	//parse argument
	if(argc != 3){
		cerr << "Usage: " << argv[0] << " with routing table and trace file" << endl;
		return 1;
	}

	cout << "size of tnode: " << sizeof(tnode) << endl;
	cout << "size of rnode: " << sizeof(rnode) << endl;
	cout << "size of bitmap<16>: " << sizeof(bitset<16>) << endl;
	cout << "size of vector<int>: " << sizeof(vector<int>) << endl;
	cout << "size of vector<tnode*> " << sizeof(vector<tnode*>) << endl;

	char* filename = argv[1];
	rnode root2 = preprocess(filename);
	cout << "root children size: " << root2.getChildren().size() << endl;
	cout << "root total prefix number: " << root2.getHops().size() << endl;
	
	//PREPROCESS END-------------------------------------------------------------
	//LOOKUP START---------------------------------------------------------------
	/*string address1 = "223.253.77.31";
	string address2 = "10.86.64.32";
	int result = lookup(&root2,address2);
	cout << "lookup result: " << result << endl; */
	
	ifstream traceFile;
	traceFile.open(argv[2], ios_base::in);
	
	if(traceFile.is_open()){
		cout << "trace file opened!" << endl;
		string line;      //each line is an IP address

		//time counting for lookup.
		clock_t tLookupStart = clock();
		
		while(traceFile.good()){
			getline(traceFile,line);
			//cout << line << endl;
			int nexthop2 = lookup(&root2,line);
			//cout << "next hop: " << nexthop2 << endl;
		}
		cout << "Lookup time elasping: " << (double)(clock() - tLookupStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "trace file closing..." << endl;
		traceFile.close();
	}
	
	//LOOKUP END-----------------------------------------------------------------
	return 0;
}
