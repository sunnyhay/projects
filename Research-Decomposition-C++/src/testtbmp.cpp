#include<iostream>
#include<bitset>
#include<math.h>
#include<vector>
#include <map>
#include <stdlib.h>
#include <fstream>
#include <time.h>
using namespace std;

#include "tbmp.h"
using namespace tbmplib;


int main(int argc, char* argv[])
{
	//PREPROCESS START-----------------------------------------------------------
	//parse argument
	if(argc != 3){
		cerr << "Usage: " << argv[0] << " with routing table and trace file" << endl;
		return 1;
	}

	char* filename = argv[1];
	rnode root2 = tbmplib::preprocess(filename);
	cout << "root children size: " << root2.getChildren().size() << endl;
	cout << "root total prefix number: " << root2.getHops().size() << endl;
	
	//PREPROCESS END-------------------------------------------------------------
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
			int nexthop2 = tbmplib::lookup(&root2,line);
			//cout << "next hop: " << nexthop2 << endl;
		}
		cout << "Lookup time elasping: " << (double)(clock() - tLookupStart)/CLOCKS_PER_SEC << " seconds." << endl;
		cout << "trace file closing..." << endl;
		traceFile.close();
	}
	//LOOKUP END-----------------------------------------------------------------

	return 0;
}
