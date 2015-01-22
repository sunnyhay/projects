package edu.wsu.eecs.nrl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class TestTableCollection {
	/**
	 * remove duplicate items in the source and return the new non-duplicate one
	 */
	public static ArrayList<String> removeDup(ArrayList<String> source){
		ArrayList<String> noDup = new ArrayList<String>();
		
		for(String str : source){
			boolean dupSign = false;
			for(String s : noDup){
				if(str.equals(s))
					dupSign = true;
			}
			if(!dupSign)
				noDup.add(str);
		}
		
		return noDup;
	}
	/**
	 * test pure delete before and after rebuild
	 * use build to establish hash tables, then randomly choose a certain ratio of elements such as 20%,30%.
	 * delete these elements in hash tables and collision tables, then make statistics about
	 * the ratio of states in each table.
	 * finally rebuild the tables and make the statistics again.
	 * analyze the states.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static ArrayList<Double> testDeleteAndRebuild(ArrayList<String> source, int[] tableSizes, int k, 
			double ratio, int hashSelection, ArrayList<Integer> onlyList) 
					throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		//1. conduct first insertion 
		int[] colCounter = new int[2];
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		System.out.println("before deletion");
		//1.1 get capacity for each table
		/*for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());*/
				
		//1.2 get capacity for each collision table
		/*for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());*/		
		
		//1.3 use rebuild() for balance
		tableCollection.rebuild(hashSelection);
		
		/*System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());*/
		
		System.out.println("*********************BUILD END*********************");
		System.out.println();
		
		//2. delete a percentage of items
		System.out.println("*********************DELETE BEGIN*********************");
		
		ArrayList<Item> existItems = new ArrayList<Item>();

		//2.1 get the same set of items successfully inserted into the tables
		for (int i = 0; i < tableCollection.getTables().size(); i++) {
			HTable curTable = tableCollection.getTables().get(i); // current table
			for (int j = 0; j < curTable.getHtable().size(); j++) {
				Item curItem = curTable.getHtable().get(j);
				//System.out.println(curItem);
				if (curTable.getBitmap().get(j) && curItem != null) {
					existItems.add(curItem);
				}
			}
		}
		ArrayList<Item> itemsInLast = tableCollection.getLastTable().getAllItems();
		//System.out.println("item number in last table: " + itemsInLast.size());
		for(Item item : itemsInLast)
			existItems.add(item);
		System.out.println("item number in all tables: " + existItems.size());
		System.out.println();
		
		//2.2 get the set of items to be deleted
		int bound = (int) (existItems.size() * ratio);		
		//System.out.println("bound: " + bound);
		Random r = new Random();		
		HashSet<Item> deleteSet = new HashSet<Item>();
		for(int i = 0; i < bound; i++){
			int j = r.nextInt(existItems.size()-1);
			deleteSet.add(existItems.get(j));
		}
		System.out.println("count of items to be deleted: " + deleteSet.size());
		
		int[] deleteTableCount = new int[tableSizes.length];
		for(Item targetItem : deleteSet){
			ArrayList<Integer> pos = tableCollection.locateItem(0, targetItem, hashSelection);
			deleteTableCount[pos.get(1)]++;
			tableCollection.pureDelete(targetItem, pos.get(1), pos.get(0), hashSelection);
		}
		
		//2.4 output each table and collision table's status after pure deletion
		for (int i = 0; i < tableSizes.length - 1; i++) {
			int tempCap = tableCollection.getTables().get(i).getCapacity(); 
			System.out.println("table " + i + " with capacity: " + tempCap);
		}
		int lastTempCap = tableCollection.getLastTable().getCapacity(); 
		System.out.println("last table capacity: " + lastTempCap);

		//2.4.2 get capacity for each collision table
		/*for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());*/
		
		//2.4.3 output which table the delete item belongs to
		for(int i = 0; i < deleteTableCount.length; i++)
			System.out.println("in table " + i + " count of deleted items: " + deleteTableCount[i]);
		
		//2.4.4 count only-one-item list in each collision table
		int[] colOneListCount = new int[tableSizes.length - 1];
		for(int i = 0; i < tableSizes.length - 1; i++){
			CollisionTable curColTable = tableCollection.getColTables().get(i);
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				if(curColTable.isOnlyOneInList(j))
					colOneListCount[i]++;
			}
		}
		
		for(int i = 0; i < colOneListCount.length; i++){
			System.out.println("in col table " + i + " one-item list count: " + colOneListCount[i]);
			onlyList.add(colOneListCount[i]);
		}
		
		System.out.println("*********************DELETE END*********************");
		System.out.println();
		
		System.out.println("*********************REBUILD BEGIN*********************");
		//3. rebuild again
		tableCollection.rebuild(hashSelection); //rebalancing
		ArrayList<Double> capDiffRatio = new ArrayList<Double>();
		ArrayList<Integer> caps = new ArrayList<Integer>();

		System.out.println("after rebuild");
		for (int i = 0; i < tableSizes.length - 1; i++) {
			int tempCap = tableCollection.getTables().get(i).getCapacity(); 
			System.out.println("table " + i + " with capacity: " + tempCap);
			caps.add(tempCap);
			
		}
		lastTempCap = tableCollection.getLastTable().getCapacity(); 
		System.out.println("last table capacity: " + lastTempCap);
		caps.add(lastTempCap);
		
		int totalCount = 0;
		for(int cap : caps)
			totalCount += cap;
		
		for(int i = 0; i < caps.size(); i++)
			capDiffRatio.add((double)caps.get(i) / totalCount);
		
		/*for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());*/
		
		System.out.println("*********************REBUILD END*********************");
		return capDiffRatio;
	}
	
	/**
	 * test addNewItem() in TableCollection by firstly building tables using random strings
	 * and then adding new fixed items.
	 * 
	 */
	public static void testAdd(ArrayList<String> source, int[] tableSizes, int k, int hashSelection, 
			ArrayList<String> vals) throws Exception{		
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		//1. conduct insertion using build
		int[] colCounter = new int[2];
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		System.out.println("before rebuild");
		//1.1 get capacity for each table
		for (int i = 0; i < tableSizes.length - 1; i++) {
			System.out.println("table " + i + " with capacity: "
					+ tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: "
				+ tableCollection.getLastTable().getCapacity());

		// 1.2 get capacity for each collision table
		for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());
		
		//conduct rebuild
		tableCollection.rebuild(hashSelection);
		System.out.println();
		System.out.println("after rebuild");		
		
		//1.3 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		
		//1.4 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
				+ tableCollection.getColTables().get(i).getSize());		
		//1.5 check invariants after rebuild
		boolean invariant1 = tableCollection.checkInvariant();
		if(!invariant1){
			System.out.println("FIRST INVARIANT BROKEN after rebuild");			
		}
		boolean invariant2 = tableCollection.check2ndInvariant();
		if(!invariant2){
			System.out.println("SECOND INVARIANT BROKEN after rebuild");
		}
		
		System.out.println();
		System.out.println("after inserting new items");
		//2. add new values
		System.out.println("ready for inserting new item count: " + vals.size());
		int curTableNo = 0;
		long[] hash = null;
		for(String val : vals){
			hash = HashFunctions.getHash(hashSelection, val,
					curTableNo, tableCollection.getTableSizeArray()[curTableNo]);
			Item targetItem = new Item(hash[0], new Value(val));
			ArrayList<Item> targetItems = new ArrayList<Item>();
			targetItems.add(targetItem);
			ArrayList<Integer> addResult = 
					tableCollection.addNewItem(curTableNo, hashSelection, k, targetItems);
			System.out.println("adding " + val + " causes involving items count: " + 
					addResult.get(0) + " and dropping items count: " + addResult.get(1));
			//check invariants every time
			boolean invariant3 = tableCollection.checkInvariant();
			if(!invariant3){
				System.out.println("FIRST INVARIANT BROKEN on item " + targetItem);			
			}
			boolean invariant4 = tableCollection.check2ndInvariant();
			if(!invariant4){
				System.out.println("SECOND INVARIANT BROKEN on item " + targetItem);
			}
		}
				
		//2.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			int sizeForEach = tableCollection.getTables().get(i).getCapacity();
			System.out.println("table " + i + " with capacity: " + sizeForEach);
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
				
		//2.2 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
					+ tableCollection.getColTables().get(i).getSize());		
	}
	
	/**
	 * test build and insert
	 * use the same number of items to be inserted
	 * compare the difference between two insertion methods to build the tables. 
	 * return test result of double-dimension arraylist for occupied item counts
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static ArrayList<ArrayList<Double>> test2Inserts(ArrayList<String> source, 
			ArrayList<String> source1,int[] tableSizes, 
			int k, int hashSelection) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		//1. conduct first insertion 
		int[] colCounter = new int[2];
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		System.out.println("before deletion");
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
				
		//1.2 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());		
		
		//1.3 use rebuild() for balance
		tableCollection.rebuild(hashSelection);
		
		System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());
		
		System.out.println("*********************BUILD END*********************");
		System.out.println();
				
		//2. using addNewItem() to preprocess 
		System.out.println("*********************SECOND INSERTION BEGIN*********************");
		//2.1 use source1 (actually source) as the input
		System.out.println("ready for inserting new item count: " + source1.size());
		tableCollection = new TableCollection(tableSizes, k);
		int curTableNo = 0;
		int involvedItemCount = 0;
		int totalDroppedItemCount = 0;
		long[] hash = null;
		/*
		 * for load factor test, calculate three states in each table
		 * state1 means empty bucket; state2 means occupied item; state3 means empty bit with collision list 
		 */		
		ArrayList<ArrayList<Double>> state1Sets = new ArrayList<ArrayList<Double>>(tableSizes.length-1);
		ArrayList<ArrayList<Double>> state2Sets = new ArrayList<ArrayList<Double>>(tableSizes.length-1);
		ArrayList<ArrayList<Double>> state3Sets = new ArrayList<ArrayList<Double>>(tableSizes.length-1);
		
		for(int i = 0; i < tableSizes.length-1; i++){
			state1Sets.add(new ArrayList<Double>());
			state2Sets.add(new ArrayList<Double>());
			state3Sets.add(new ArrayList<Double>());
		}
			
		int lfInteval = source1.size() / 10;  //sample interval of items, depending on input size
		int lfCount = 0;
		
		for(String val : source1){
			hash = HashFunctions.getHash(hashSelection, val,
					curTableNo, tableCollection.getTableSizeArray()[curTableNo]);
			Item targetItem = new Item(hash[0], new Value(val));
			ArrayList<Item> targetItems = new ArrayList<Item>();
			targetItems.add(targetItem);
			ArrayList<Integer> addResult = 
					tableCollection.addNewItem(curTableNo, hashSelection, k, targetItems);
			
			//count current three states for each table
			lfCount++;
			if(lfCount % lfInteval == 0){
				for(int i = 0; i < tableSizes.length-1; i++){
					HTable curTable = tableCollection.getTables().get(i);
					int curTableSize = tableCollection.getTableSizeArray()[i];
					//occupied item
					state2Sets.get(i).add((double)curTable.getCapacity()/curTableSize);
					//marked item, i.e. bit clear bucket with non-empty colliding list
					int nonEmptyCount = 0;
					CollisionTable curColTable = tableCollection.getColTables().get(i);
					for(int j = 0; j < curColTable.getColTable().size(); j++){
						if(!curColTable.isCurListEmpty(j))
							nonEmptyCount++;
					}
					state3Sets.get(i).add((double)nonEmptyCount/curTableSize);
					//empty item
					state1Sets.get(i).add((double)(curTableSize-curTable.getCapacity()-
							nonEmptyCount)/curTableSize);
				}
			}
			
			/*System.out.println("adding " + val + " causes involving items count: " + 
					addResult.get(0) + " and dropping items count: " + addResult.get(1));*/
			involvedItemCount += addResult.get(0);
			totalDroppedItemCount += addResult.get(1);
			//check invariants every time
			boolean invariant3 = tableCollection.checkInvariant();
			if(!invariant3){
				//System.out.println("FIRST INVARIANT BROKEN on item " + targetItem);			
			}
			boolean invariant4 = tableCollection.check2ndInvariant();
			if(!invariant4){
				//System.out.println("SECOND INVARIANT BROKEN on item " + targetItem);
			}
		}
				
		//2.2 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			int sizeForEach = tableCollection.getTables().get(i).getCapacity();
			System.out.println("table " + i + " with capacity: " + sizeForEach);
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
				
		//2.3 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
					+ tableCollection.getColTables().get(i).getSize());
		
		System.out.println("total involved item count: " + involvedItemCount);
		System.out.println("total dropped item count: " + totalDroppedItemCount);
		
		//collect data for test
		//the size of testResult of the number of double out hash tables
		//each element of testResult is 10, each sample for an occupied item rate in current table
		ArrayList<ArrayList<Double>> testResult = new ArrayList<ArrayList<Double>>(); 
		
		//2.4 output sample interval stat.
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " sample");
			/*System.out.println("empty item count: ");
			ArrayList<Double> temp = state1Sets.get(i);
			for(double stat : temp)
				System.out.print(stat + " ");
			System.out.println();*/
			
			ArrayList<Double> testT = new ArrayList<Double>();
			System.out.println("occupied item count: ");
			ArrayList<Double> temp = state2Sets.get(i);
			for(double stat : temp){
				System.out.print(stat + " ");
				testT.add(stat);
			}
			testResult.add(testT);
			System.out.println();
			
			System.out.println("colliding item count: ");
			temp = state3Sets.get(i);
			for(double stat : temp)
				System.out.print(stat + " ");
			System.out.println();
		}

		System.out.println("*********************SECOND INSERTION END*********************");
		
		return testResult;
	}
	
	/** test all, insert, query and delete
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * 
	 */
	public static void testAll(ArrayList<String> source, int[] tableSizes, int k, 
			int hashSelection, double delRatio) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
				
		//1. conduct insertion 
		int[] colCounter = new int[2];  //actually no use
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());

		// 1.2 get capacity for each collision table
		for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());
		
		//1.3 use rebuild() for balance
		tableCollection.rebuild(hashSelection);
		System.out.println();
		System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
					tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
					+ tableCollection.getColTables().get(i).getSize());
				
		System.out.println("*********************BUILD END*********************");
		System.out.println();
				
		System.out.println("*********************QUERY BEGIN*********************");
		//2. query
		HashSet<Item> existItems = new HashSet<Item>();

		//2.1 query the same set of items successfully inserted into the tables
		for (int i = 0; i < tableCollection.getTables().size(); i++) {
			HTable curTable = tableCollection.getTables().get(i); // current table
			for (int j = 0; j < curTable.getHtable().size(); j++) {
				Item curItem = curTable.getHtable().get(j);
				if (curTable.getBitmap().get(j) && curItem != null) {
					existItems.add(curItem);
				}
			}
		}
		
		ArrayList<Item> itemsInLast = tableCollection.getLastTable().getAllItems();
		for(Item item : itemsInLast)
			existItems.add(item);
		System.out.println("item number in all tables: " + existItems.size());
		
		//2.2 conduct query
		int[] queryCounters = new int[tableCollection.getTableSizeArray().length];
		int realFalseCounter = 0;
		for(int i = 0; i < queryCounters.length; i++){
			queryCounters[i] = 0;
		}
			
		for(Item item : existItems){
			ArrayList<Integer> queryResults = tableCollection.query(item.getValue().getVal(), hashSelection);
			int zeroCount = 0;
			for(int i = 0; i < queryResults.size(); i++){
				int result = queryResults.get(i);
				if(result == 0)
					zeroCount++;
			}
			if(zeroCount > 1)
				realFalseCounter ++;
		}
		
		//2.3 output result		
		System.out.println("false positive count: " + realFalseCounter);
				
		//2.4 for test, check each item's existence in colliding lists
		/*for (Item curItem : existItems) {
			//2.4.1 locate each item's position in HTable
			ArrayList<Integer> location = tableCollection.locateItem(0,	curItem, hashSelection);
			int curItemIndex = location.get(0);
			int curItemTableNo = location.get(1);
			if (curItemIndex == -1 || curItemTableNo == -1) {
				System.out.println("impossible locate result");
			} else {
				for (int i = 0; i < tableCollection.getTableSizeArray().length - 1; i++) {
					CollisionTable curColTable = tableCollection.getColTables().get(i);
					long[] hash = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
							i, tableCollection.getTableSizeArray()[i]);
					Item tempItem = new Item(hash[0], curItem.getValue());
					boolean findResult = curColTable.findItem(tempItem);
					if (findResult && i >= curItemTableNo) {
						System.out.println("found latter terrible, not supposed to be there!");
					}
					if (!findResult && i < curItemTableNo) {
						System.out.println("found previous terrible, not in colliding list");
					}
				}
			}
		}*/
				
		System.out.println("*********************QUERY END*********************");
		System.out.println();
		
		System.out.println("*********************DELETE BEGIN*********************");
		
		//3. delete
		int deleteBound = (int)(delRatio * existItems.size());  //set the number of items to be deleted
		System.out.println("number of items to be deleted: " + deleteBound);
		
		HashSet<Item> delSet = new HashSet<Item>();
		for(Item curItem : existItems){
			if(deleteBound <= 0)
				break;
			delSet.add(curItem);
			deleteBound--;
		}		
		
		int notFoundCounter = 0;
		int totalItemNum = 0;
		for(Item curItem : delSet){
			ArrayList<Integer> locateResult = tableCollection.locateItem(0, curItem, hashSelection);
			if(locateResult.get(1) == -1 || locateResult.get(0) == -1)
				notFoundCounter++;
			else{
				int temp = tableCollection.remove(locateResult.get(1), locateResult.get(0), hashSelection);
				
				//check invariants every time
				boolean invariant1 = tableCollection.checkInvariant();
				if(!invariant1){
					//System.out.println("FIRST INVARIANT BROKEN on item " + curItem);			
				}
				boolean invariant2 = tableCollection.check2ndInvariant();
				if(!invariant2){
					//System.out.println("SECOND INVARIANT BROKEN on item " + curItem);			
				}
				
				totalItemNum += temp;
			}
		}
		
		System.out.println("not found counter: " + notFoundCounter);
		
		System.out.println("after deletion");
		//3.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		
		//3.2 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());
		
		//3.3 output deletion counters for the number of items involved totally
		System.out.println("total involved item count: " + totalItemNum);
		
		System.out.println("*********************DELETE END*********************");
		System.out.println();
	}
	
	/**
	 * test insert and delete
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static int testInsertAndDelete(ArrayList<String> source, int[] tableSizes, int k, 
			int hashSelection, double ratio) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		//counters to count elements' distribution.
		//first for insertion in (n-1) tables; second for last table; third for dropped values.
		int[] insertCounters = new int[3];
		for(int i = 0; i < insertCounters.length; i++)
			insertCounters[i] = 0;
		
		//1. conduct insertion 
		int[] colCounter = new int[2];
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		System.out.println("before deletion");
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		 
		
		//1.2 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());		
		
		//1.3 use rebuild() for balance
		tableCollection.rebuild(hashSelection);
		
		System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());	
		
		//for test
		//tableCollection.checkDeepVariant();
		
		//2. delete
		ArrayList<Item> existItems = new ArrayList<Item>();

		// 2.1 get the same set of items successfully inserted into the tables
		for (int i = 0; i < tableCollection.getTables().size(); i++) {
			HTable curTable = tableCollection.getTables().get(i); // current table
			for (int j = 0; j < curTable.getHtable().size(); j++) {
				Item curItem = curTable.getHtable().get(j);
				//System.out.println(curItem);
				if (curTable.getBitmap().get(j) && curItem != null) {
					existItems.add(curItem);
				}
			}
		}
		ArrayList<Item> itemsInLast = tableCollection.getLastTable().getAllItems();
		//System.out.println("item number in last table: " + itemsInLast.size());
		for(Item item : itemsInLast)
			existItems.add(item);
		System.out.println("item number in all tables: " + existItems.size());
		System.out.println();
		
		//2.1.1 for test, show all the tables, collision tables
		/*for(int i = 0; i < tableCollection.getTables().size(); i++){
			HTable curTable = tableCollection.getTables().get(i);
			CollisionTable curColTable = tableCollection.getColTables().get(i);
			//System.out.println("table " + i + " bitmap: " + curTable.getBitmap());
			//System.out.println("table " + i + " size: " + curTable.getCapacity());
			System.out.println("********************TABLE" + i + " BEGIN***********************");
			for(int j = 0; j < curTable.getHtable().size(); j++){
				Item curItem = curTable.getHtable().get(j);
				if(curItem != null && curTable.bitmapCheck(j)){
					System.out.println("table " + i + " has item " + curItem + " with index: " + j);					
				}		
			}
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				ArrayList<Item> curList = curColTable.getColTable().get(j);
				if(!curList.isEmpty()){
					for(Item nItem : curList)
						System.out.println("col table " + i + " has item " + nItem 
								+ " with index: " + j);
				}
			}
			System.out.println("********************TABLE" + i + " END***********************");
			System.out.println();
		}*/
		//System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		/*System.out.println("********************LAST TABLE BEGIN***********************");
		for(int i = 0; i < tableCollection.getLastTable().getBdTable().size(); i++){
			BDHopItem curBDHopItem = tableCollection.getLastTable().getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				System.out.println("last table item " + curBDHopItem.getItem() + " with index: " + i);
				System.out.println("the item's bitmap: " + curBDHopItem.getBitmap());
			}
		}
		System.out.println("********************LAST TABLE END***********************");*/
		
		//2.2 conduct delete
		int deleteBound = (int) (ratio * existItems.size());
		Random r = new Random();
		HashSet<Item> delItemSet = new HashSet<Item>();
		for(int i = 0; i < deleteBound; i++){
			int j = r.nextInt(existItems.size()-1);
			delItemSet.add(existItems.get(j));
		}
		
		System.out.println("number of items to be deleted: " + deleteBound);
		int notFoundCounter = 0;
		int totalItemNum = 0;
		for(Item curItem : delItemSet){
			if(deleteBound <= 0)
				break;
			//2.2.1 for bug test using one item 
			/*String val = curItem.getValue().getVal();
			long[] hashTest = HashFunctions.getHash(hashSelection, val,	
					4, tableCollection.getTableSizeArray()[4]);
			System.out.println("key: " + hashTest[0] + " with index in last table: " + hashTest[1]
					+ " and value: " + val);
			Item specItem = new Item(hashTest[0],new Value(val));
			
			for(int i = 0; i < 4; i++){//output all the colliding list with the specific item
				System.out.println("table " + i + " colliding list");
				ArrayList<Item> result1 = tableCollection.getListForItem(i, specItem, hashSelection);
				for(Item itemk : result1)
					System.out.println(itemk);
			}*/
			
			ArrayList<Integer> locateResult = tableCollection.locateItem(0, curItem, hashSelection);
			if(locateResult.get(1) == -1 || locateResult.get(0) == -1)
				notFoundCounter++;
			else{
				int temp = tableCollection.remove(locateResult.get(1), locateResult.get(0), hashSelection);
				
				//check invariants every time
				boolean invariant1 = tableCollection.checkInvariant();
				if(!invariant1){
					//System.out.println("FIRST INVARIANT BROKEN on item " + curItem);			
				}
				boolean invariant2 = tableCollection.check2ndInvariant();
				if(!invariant2){
					//System.out.println("SECOND INVARIANT BROKEN on item " + curItem);			
				}
				
				totalItemNum += temp;
			}
				
			deleteBound--;			
		}
		System.out.println("not found counter: " + notFoundCounter);
		System.out.println("after deletion");
		//3.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		
		//3.2 get capacity for each collision table
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
		+ tableCollection.getColTables().get(i).getSize());
		
		//3.3 output deletion counters for the number of items involved totally
		System.out.println("total involved item count: " + totalItemNum);
		return totalItemNum-delItemSet.size();
	}
	
	/**
	 * test query using AddNewItem() method to build tables
	 * there are three cases for query. for (n-1) tables,
	 * 1. more than one bitmap query return true (bit set), sure not in tables;
	 * 2. one bitmap query true, false positive;
	 * 3. no bitmap query true, also false positive since last table should be queried. 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void testQueryWithAdd(ArrayList<String> source, int[] tableSizes, 
			int k,	int hashSelection, 
			ArrayList<String> targets) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		//1. conduct insertion 
		int curTableNo = 0;
		int involvedItemCount = 0;
		int totalDroppedItemCount = 0;
		long[] hash = null;
				
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		for(String val : source){
			hash = HashFunctions.getHash(hashSelection, val,
					curTableNo, tableCollection.getTableSizeArray()[curTableNo]);
			Item targetItem = new Item(hash[0], new Value(val));
			ArrayList<Item> targetItems = new ArrayList<Item>();
			targetItems.add(targetItem);
			ArrayList<Integer> addResult = 
					tableCollection.addNewItem(curTableNo, hashSelection, k, targetItems);
			/*System.out.println("adding " + val + " causes involving items count: " + 
					addResult.get(0) + " and dropping items count: " + addResult.get(1));*/
			involvedItemCount += addResult.get(0);
			totalDroppedItemCount += addResult.get(1);
			//check invariants every time
			boolean invariant3 = tableCollection.checkInvariant();
			if(!invariant3){
				//System.out.println("FIRST INVARIANT BROKEN on item " + targetItem);			
			}
			boolean invariant4 = tableCollection.check2ndInvariant();
			if(!invariant4){
				//System.out.println("SECOND INVARIANT BROKEN on item " + targetItem);
			}
		}
		
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());

		// 1.2 get capacity for each collision table
		for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());
		System.out.println("total involved item count: " + involvedItemCount);
		System.out.println("total dropped item count: " + totalDroppedItemCount);
		
		System.out.println("*********************BUILD END*********************");
		System.out.println();
				
		System.out.println("*********************QUERY BEGIN*********************");
		//2. query
		System.out.println("querying string count: " + targets.size());
		int case1Counter = 0;  //no false positive case 1
		int case2Counter = 0;  //false positive to check one specific HTable
		int case3Counter = 0;  //false positive to check only the last table
			
		for(String val : targets){
			ArrayList<Boolean> queryResults = tableCollection.filterQuery(val, hashSelection);
			int trueCount = 0;
			for(int i = 0; i < queryResults.size(); i++){
				boolean result = queryResults.get(i);
				if(result)
					trueCount++;
			}
			if(trueCount > 1)
				case1Counter++;
			else if(trueCount == 1)
				case2Counter++;
			else
				case3Counter++;
		}
		
		//2.3 output result		
		System.out.println("recognized non-existing item count: " + case1Counter);
		System.out.println("one-match false positive count: " + case2Counter);
		System.out.println("last table try false positive count: " + case3Counter);
		System.out.println("*********************QUERY END*********************");
	}

	/**
	 * test query for non-existing items in tables for evaluating false positive
	 * there are three cases for query. for (n-1) tables,
	 * 1. more than one bitmap query return true (bit set), sure not in tables;
	 * 2. one bitmap query true. For this bit string with only a single one. Suppose 0001000 for 8-table
	 * hierarchy, check the collision tables. The collision lists should be three non-empty lists followed
	 * by one empty list 
	 * 3. no bitmap query true, also false positive since last table should be queried. 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void testQuery(ArrayList<String> source, int[] tableSizes, int k,	int hashSelection, 
			ArrayList<String> targets) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		//1. conduct insertion 
		int[] colCounter = new int[2];  //actually no use
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());

		// 1.2 get capacity for each collision table
		for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());
		
		//1.3 use rebuild() for balance
		int rebuildCount = tableCollection.rebuild(hashSelection);
		System.out.println();
		System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
					tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
					+ tableCollection.getColTables().get(i).getSize());
		System.out.println("rebuild involves item count: " + rebuildCount);
				
		System.out.println("*********************BUILD END*********************");
		System.out.println();
				
		System.out.println("*********************QUERY BEGIN*********************");
		//2. query
		System.out.println("querying string count: " + targets.size());
		int case1Counter = 0;  //no false positive case 1
		int case2Counter = 0;  //false positive to check one specific HTable
		int case3Counter = 0;  //false positive to check only the last table
			
		for(String val : targets){
			ArrayList<Boolean> queryResults = tableCollection.filterQuery(val, hashSelection);
			ArrayList<Boolean> colQueryResults = tableCollection.colTableQuery(val, hashSelection);
			int trueCount = 0;
			for(int i = 0; i < queryResults.size(); i++){
				boolean result = queryResults.get(i);
				if(result)
					trueCount++;
			}
			if(trueCount > 1)
				case1Counter++;
			else if(trueCount == 1){
				boolean realFalse = true;
				for(int i = 0; i < queryResults.size(); i++){
					boolean result = queryResults.get(i);
					boolean colResult = colQueryResults.get(i);
					if(result){
						if(!colResult)
							realFalse = false;
						break;
					}else{
						if(colResult)
							realFalse = false;
					}
				}
				if(realFalse)
					case2Counter++;
			}				
			else
				case3Counter++;
		}
		
		//2.3 output result		
		System.out.println("recognized non-existing item count: " + case1Counter);
		System.out.println("one-match false positive count: " + case2Counter);
		System.out.println("last table try false positive count: " + case3Counter);
		System.out.println("*********************QUERY END*********************");
	}
	
	
	/** 
	 * test insert and query for a source of random strings
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void testInsertAndQuery(ArrayList<String> source, int[] tableSizes, int k, 
			int hashSelection) throws InvalidKeyException, NoSuchAlgorithmException{
		TableCollection tableCollection = new TableCollection(tableSizes, k);
		
		//1. conduct insertion 
		int[] colCounter = new int[2];  //actually no use
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildFirst = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		
		System.out.println();
		System.out.println("*********************BUILD BEGIN*********************");
		if(buildFirst){
			boolean buildNext = true;
			int tableNo = 1;
			while(buildNext){
				if(tableNo == tableSizes.length-1){
					int[] colCounterLast = new int[2];
					for(int i = 0; i < colCounterLast.length; i++)
						colCounterLast[i] = 0;
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounterLast);
					System.out.println("drop items: " + colCounterLast[1]);
				}else
					buildNext = tableCollection.buildTable(tableNo, hashSelection, colCounter);					
				tableNo++;
			}
		}
		
		//1.1 get capacity for each table
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
		tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());

		// 1.2 get capacity for each collision table
		for (int i = 0; i < tableSizes.length - 1; i++)
			System.out.println("collision table " + i + " size: "
					+ tableCollection.getColTables().get(i).getSize());
		
		//1.3 use rebuild() for balance
		int rebuildCount = tableCollection.rebuild(hashSelection);
		System.out.println();
		System.out.println("after rebuild");
		for(int i = 0; i < tableSizes.length-1; i++){
			System.out.println("table " + i + " with capacity: " + 
					tableCollection.getTables().get(i).getCapacity());
		}
		System.out.println("last table capacity: " + tableCollection.getLastTable().getCapacity());
		for(int i = 0; i < tableSizes.length-1; i++)
			System.out.println("collision table " + i + " size: " 
					+ tableCollection.getColTables().get(i).getSize());
		System.out.println("rebuild involves item count: " + rebuildCount);
				
		System.out.println("*********************BUILD END*********************");
		System.out.println();
				
		System.out.println("*********************QUERY BEGIN*********************");
		//2. query
		HashSet<Item> existItems = new HashSet<Item>();

		//2.1 query the same set of items successfully inserted into the tables
		for (int i = 0; i < tableCollection.getTables().size(); i++) {
			HTable curTable = tableCollection.getTables().get(i); // current table
			for (int j = 0; j < curTable.getHtable().size(); j++) {
				Item curItem = curTable.getHtable().get(j);
				if (curTable.getBitmap().get(j) && curItem != null) {
					existItems.add(curItem);
				}
			}
		}
		
		ArrayList<Item> itemsInLast = tableCollection.getLastTable().getAllItems();
		for(Item item : itemsInLast)
			existItems.add(item);
		System.out.println("item number in all tables: " + existItems.size());
		
		//2.2 conduct query
		int[] queryCounters = new int[tableCollection.getTableSizeArray().length];
		int realFalseCounter = 0;
		for(int i = 0; i < queryCounters.length; i++){
			queryCounters[i] = 0;
		}
			
		for(Item item : existItems){
			ArrayList<Integer> queryResults = tableCollection.query(item.getValue().getVal(), hashSelection);
			int zeroCount = 0;
			for(int i = 0; i < queryResults.size(); i++){
				int result = queryResults.get(i);
				if(result == 0)
					zeroCount++;
			}
			if(zeroCount > 1)
				realFalseCounter ++;
		}
		
		//2.3 output result		
		System.out.println("false positive count: " + realFalseCounter);
				
		//2.4 for test, check each item's existence in colliding lists
		for (Item curItem : existItems) {
			//2.4.1 locate each item's position in HTable
			ArrayList<Integer> location = tableCollection.locateItem(0,	curItem, hashSelection);
			int curItemIndex = location.get(0);
			int curItemTableNo = location.get(1);
			if (curItemIndex == -1 || curItemTableNo == -1) {
				System.out.println("impossible locate result");
			} else {
				for (int i = 0; i < tableCollection.getTableSizeArray().length - 1; i++) {
					CollisionTable curColTable = tableCollection.getColTables().get(i);
					long[] hash = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
							i, tableCollection.getTableSizeArray()[i]);
					Item tempItem = new Item(hash[0], curItem.getValue());
					boolean findResult = curColTable.findItem(tempItem);
					if (findResult && i >= curItemTableNo) {
						System.out.println("found latter terrible, not supposed to be there!");
					}
					if (!findResult && i < curItemTableNo) {
						System.out.println("found previous terrible, not in colliding list");
					}
				}
			}
		}
				
		System.out.println("*********************QUERY END*********************");
	}
	
	
	//randomly select strings from a text file
	public static ArrayList<String> randomFromFile(String filename, int n,
			int minLineLen, int interval) throws IOException {
		ArrayList<String> source = new ArrayList<String>();
		ArrayList<String> lines = getLinesFromFile(filename, minLineLen); // lines from the file
		ArrayList<Integer> pairs = getPairsOfRandoms(n, lines.size(), minLineLen, interval);

		for (int i = 0; i < pairs.size(); i = i + 2) {
			source.add(lines.get(pairs.get(i)).substring(0, pairs.get(i + 1)));
		}

		// for bug test, write the selected strings into a file
		/*
		 * String wFile = "testHashTable1"; BufferedWriter bw1 = new
		 * BufferedWriter(new OutputStreamWriter(new FileOutputStream(wFile)));
		 * for(String s: source) bw1.append(s + "\n"); bw1.close();
		 */

		return source;
	}
	
	//return n pairs of line No. and character amount from the first character
	public static ArrayList<Integer> getPairsOfRandoms(int n, int totalLines, 
			int minLineLen, int interval) {
		ArrayList<Integer> pairs = new ArrayList<Integer>();
		Random r1 = new Random();
		Random r2 = new Random();

		for (int i = 0; i < n; i++) {
			pairs.add(r1.nextInt(totalLines - 1) + 1);
			pairs.add(r2.nextInt(minLineLen - interval) + (interval + 2));
		}

		return pairs;
	}
	
	//parse file
	public static ArrayList<String> getLinesFromFile(String filename, int minLineLen) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		String line = "";

		// System.out.println("begin file parsing");
		while ((line = br.readLine()) != null) {
			if (line.length() >= minLineLen)
				lines.add(line);
		}
		br.close();
		// System.out.println("file parsing end");
		return lines;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//1. test getLinesFromFile()
		/*ArrayList<String> lines = getLinesFromFile(filename, minLineLen);
		System.out.println("total line number: " + lines.size());*/
		
		//2. test getPairsOfRandoms()
		/*ArrayList<Integer> pairs = getPairsOfRandoms(100, lines.size(), minLineLen, interval);
		for(int i = 0; i < pairs.size(); i=i+2){
			System.out.println("line No.: " + pairs.get(i) + " with char num: " + pairs.get(i+1));
		}*/
		
		//3. test randomFromFile()
		
		//4. test insert and query
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 7000;
		ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
		int[] tableSizes = {5000,5000};
		int k = 4;  //the hop size for last table
		int hashSelection = 0;  //hash function selector	
		
		ArrayList<String> noDupSource = removeDup(source);
		System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());
		testInsertAndQuery(noDupSource, tableSizes, k, hashSelection);*/
		
		//5. test insert and delete
		//5.1 use large sample file
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int[] tableSizes = {10000, 6321, 3995, 2525, 1596, 1009, 638, 403, 693};
		//int[] tableSizes = {100000,63210,39954,25254,15962,10088,6376,5865};
		int k = 4;  //the hop size for last table
		int hashSelection = 1;  //hash function selector	
		int n = 10025;
		//int n = 102500;
		double ratio = 0.1;
		
		int times = 10;
		int countDel = 0;
		for(int i = 0; i < times; i++){
			ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
			ArrayList<String> noDupSource = removeDup(source);
			//System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
			System.out.println("total item count to be inserted: " + noDupSource.size());
			countDel += testInsertAndDelete(noDupSource, tableSizes, k, hashSelection, ratio); 
		}
		
		System.out.println("average involved item count: " + ((double)countDel / times));*/
		
		//5.2 use small sample file
		/*String filename = "testHashTable1";
		ArrayList<String> source = new ArrayList<String>();
		BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		String line = "";
		int bound = 100;
		int k = 4;  //the hop size for last table
		int hashSelection = 0;  //hash function selector
				
		while((line = br.readLine())!= null)
		{
			source.add(line);
			bound--;
			if(bound < 0)
				break;
			//System.out.println(line);
		}
		br.close();
		int[] tableSizes = {35,35,35,35,35};*/
		
		/*ArrayList<String> noDupSource = removeDup(source);
		System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());
		testInsertAndDelete(noDupSource, tableSizes, k, hashSelection);*/
		
		//6. test all
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 5000;
		ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
		int[] tableSizes = {2500,2500,2500,2500};
		int k = 4;  //the hop size for last table
		int hashSelection = 0;  //hash function selector	
		
		ArrayList<String> noDupSource = removeDup(source);
		System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());
		double delRatio = 1;
		testAll(source, tableSizes, k, hashSelection, delRatio);*/
		
		//7. test add()
		//String filename1 = "testHashTable2";
		/*String filename1 = "testClassifier1-fw3-original-100k-a0";
		ArrayList<String> source1 = new ArrayList<String>();
		BufferedReader br1 = new BufferedReader (new InputStreamReader (new FileInputStream(filename1)));
		String line1 = "";
		//int bound1 = 95;
		int bound1 = 6000;
				
		while((line1 = br1.readLine())!= null)
		{
			source1.add(line1);
			bound1--;
			if(bound1 < 0)
				break;
		}
		br1.close();
		//int[] tableSizes1 = {35,35,35,35,35};
		int[] tableSizes1 = {2500,2500,2500,2500};
		int k1 = 4;  //the hop size for last table
		int hashSelection1 = 0;  //hash function selector
		ArrayList<String> vals = new ArrayList<String>();
		//7.1 add new strings to be inserted
		String filename2 = "testHashTable1";
		br1 = new BufferedReader (new InputStreamReader (new FileInputStream(filename2)));
		while((line1 = br1.readLine())!= null)
		{
			//remove duplicate strings from vals
			if(!source1.contains(line1))
				vals.add(line1);
		}
		br1.close();

		Collections.sort(vals);
		ArrayList<String> noDupSource = removeDup(source1);
		System.out.println("duplicate string count: " + (source1.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());
		testAdd(noDupSource, tableSizes1, k1, hashSelection1, vals);*/
		
		//8. test2Insert()
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 10030;
		//int n = 102500;
		int k = 4;  //the hop size for last table
		int hashSelection = 1;  //hash function selector
		int[] tableSizes = {10000, 6321, 3995, 2525, 1596, 2742};
		//int[] tableSizes = {100000,63210,39954,25254,15962,10088,6376,5865};
		
		int times = 5;
		ArrayList<ArrayList<ArrayList<Double>>> testResult = new ArrayList<ArrayList<ArrayList<Double>>>(7);
		for(int i = 0; i < tableSizes.length-1; i++)
			testResult.add(new ArrayList<ArrayList<Double>>());
		
		for(int i = 0; i < times; i++){
			ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
			ArrayList<String> noDupSource = removeDup(source);
			//System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
			System.out.println("total item count to be inserted: " + noDupSource.size());
			ArrayList<String> noDupSource1 = removeDup(source);
			Collections.sort(noDupSource);
			Collections.sort(noDupSource1);
			
			ArrayList<ArrayList<Double>> temp = 
					test2Inserts(noDupSource, noDupSource1, tableSizes, k, hashSelection);
			for(int j = 0; j < tableSizes.length-1; j++){
				testResult.get(j).add(temp.get(j));
			}
			
		}
		
		for(int i = 0; i < testResult.size(); i++){
			ArrayList<ArrayList<Double>> testSet = testResult.get(i);
			ArrayList<Double> result = CommonUtil.sumStat(10, testSet);
			for(int j = 0; j < result.size(); j++)
				System.out.print(result.get(j) + " ");
			System.out.println();
		}*/
		
		/*for(int i = 0; i < testResult.size(); i++){
			ArrayList<ArrayList<Double>> temp = testResult.get(i);
			for(int j = 0; j < temp.size(); j++){
				ArrayList<Double> cir = temp.get(j);
				for(int m = 0; m < cir.size(); m++)
					System.out.print(cir.get(m) + " ");
				System.out.println();
			}
			System.out.println("**********************************");
		}*/
		
		//9. test removeDup
		/*String str1 = "I love the world";
		String str2 = "I love I love the world";
		ArrayList<String> sourceSpec = new ArrayList<String>();
		sourceSpec.add(str1);
		sourceSpec.add(str1);
		sourceSpec.add(str1);
		sourceSpec.add(str2);
		sourceSpec.add(str2);
		System.out.println("before removing dup: " + sourceSpec.size());
		ArrayList<String> noDup = removeDup(sourceSpec);
		System.out.println("after removing dup: " + noDup.size());*/
		
		//10. test only query for false positive statistics
		int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 10025;
		ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
		int[] tableSizes = {10000,6321,3995,2525,1596,1009, 638,403,256,161,102,64,41,70};
		int k = 4;  //the hop size for last table
		int hashSelection = 0;  //hash function selector	
		
		ArrayList<String> noDupSource = removeDup(source);
		System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());

		int n1 = 100000;
		ArrayList<String> targets = CommonUtil.getRandomStrings(n1);
		
		testQuery(noDupSource, tableSizes, k, hashSelection, targets);
		
		//11. test query using AddNewItem()
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 5000;
		ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
		int[] tableSizes = {3600,2000,1000,800,600,2500};
		int k = 4;  //the hop size for last table
		int hashSelection = 0;  //hash function selector	
		
		ArrayList<String> noDupSource = removeDup(source);
		System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
		System.out.println("total item count to be inserted: " + noDupSource.size());

		int n1 = 1000;
		ArrayList<String> targets = CommonUtil.getRandomStrings(n1);
		
		testQueryWithAdd(noDupSource, tableSizes, k, hashSelection, targets);*/
		
		//12. test deleteAndRebuild()
		/*int minLineLen = 60;
		int interval = 15;
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 30225;
		//int n = 102500;
		int[] tableSizes = {30000,18963,11987,7577,4789,3027,1913,3023};
		//int[] tableSizes = {100000,63210,39954,25254,15962,10088,6376,5865}; 
		int k = 4;  //the hop size for last table
		int hashSelection = 1;  //hash function selector	
		double ratio = 0.0;
		
		int times = 20;
		ArrayList<ArrayList<Double>> testSet = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> onlyList = null;
		ArrayList<ArrayList<Integer>> onlyListSet = new ArrayList<ArrayList<Integer>>();
		
		for(int i = 0; i < times; i++){
			onlyList = new ArrayList<Integer>();
			ArrayList<String> source = randomFromFile(filename, n, minLineLen, interval);
			ArrayList<String> noDupSource = removeDup(source);
			//System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
			System.out.println("total item count to be inserted: " + noDupSource.size());
			Collections.sort(noDupSource);
			testSet.add(testDeleteAndRebuild(noDupSource, tableSizes, k, ratio, hashSelection, onlyList));
			onlyListSet.add(onlyList);
		}
		System.out.println("average ratios after rebalance");
		ArrayList<Double> avgResult = CommonUtil.sumStat(tableSizes.length, testSet);
		for(Double avg : avgResult)
			System.out.println(avg);
		System.out.println("******************************");
		System.out.println("average one-element list count");
		ArrayList<Double> avgOnlyListResult = CommonUtil.sumStatInt(tableSizes.length-1, onlyListSet);
		for(Double avg : avgOnlyListResult)
			System.out.println(avg);*/
		
	}

}
