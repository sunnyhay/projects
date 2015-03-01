#include<iostream>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <sstream>
#include <time.h>
using namespace std;

#include "bsol.h"
using namespace bsollib;


int main(int argc, char* argv[])
{
	map<string,node*> array1[32];
	for(int i = 0; i < 32; i++){
		map<string,node*> table1;  //HERE MAY BE A PROBLEM!!!
		array1[i] = table1;
	}

	//TEST START-------------------------------------------------------
	//PREPROCESS START------------------------------------------------
	char* filename = "addnextHop-ipv4-as131072.txt";
	vector<string> prefixes1;    //contain all the prefixes
	
	ifstream preFile;
	preFile.open(filename, ios_base::in);
	if(preFile.is_open()){
		cout << "file opened!" << endl;
		string line;      //each line is a prefix
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

	bsollib::preprocess(&prefixes1, array1);

	//counter area
	int markerCounter = 0, prefixCounter = 0, hybridCounter = 0;
	
	for(int i = 0; i < 32; i++){
		map<string,node*> table2 = array1[i];
		
		for(map<string,node*>::iterator it = table2.begin(); it != table2.end(); it++){
			if(it->second->getFlag() == 0)
				prefixCounter++;
			else if(it->second->getFlag() == 1)
				markerCounter++;
			else
				hybridCounter++;
		}
	}
	cout << "hybrid node count: " << hybridCounter << endl;
	
	//PREPROCESS END--------------------------------------------------
	
	//LOOKUP START----------------------------------------------------
	char* trace = "traceIPv4-routeview-as131072-2.txt";
	ifstream traceFile;
	traceFile.open(trace, ios_base::in);
	
	if(traceFile.is_open()){
		cout << "trace file opened!" << endl;
		string line;      //each line is an IP address

		//time counting for lookup.
		clock_t tLookupStart = clock();
		
		while(traceFile.good()){
			getline(traceFile,line);
			//cout << line << endl;
			int nexthop2 = bsollib::lookup(line,array1);
			//cout << "next hop: " << nexthop2 << endl;
		}
		cout << "Lookup time elasping: " << (double)(clock() - tLookupStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "trace file closing..." << endl;
		traceFile.close();
	}
	//LOOKUP END----------------------------------------------------

	return 0;
}
