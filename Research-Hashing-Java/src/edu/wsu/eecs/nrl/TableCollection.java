package edu.wsu.eecs.nrl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * correct implementation of table group with k number of tables as well as (k-1) collision tables
 */
public class TableCollection {
	private ArrayList<HTable> tables;   //the previous (n-1), indexed by 0 to (n-2), table. 
	private ArrayList<CollisionTable> colTables;  //the corresponding (n-1) collision tables.
	private BDHopTable lastTable;  //the last table, the only one to hold colliding items indexed (n-1)
	private int[] tableSizeArray;  //contains sizes for all the tables. 
	private int k;  //k value used in BDTable
	
	public TableCollection(int[] tableSizes, int k){
		this.setTableSizeArray(tableSizes);
		int amount = tableSizes.length;
		tables = new ArrayList<HTable>(amount-1);
		colTables = new ArrayList<CollisionTable>();
		for(int i = 0; i < (amount-1); i++){
			HTable table = new HTable(tableSizes[i]);
			tables.add(table);
			CollisionTable colTable = new CollisionTable(tableSizes[i]);
			colTables.add(colTable);
		}
		this.k = k;
		setLastTable(new BDHopTable(tableSizes[amount-1],k));
	}
	/*
	 * -------------------------------------------------------------------------
	 * main method start
	 */
	/**
	 * check the first invariant for the tables, that is 
	 * the number of items in collision table n is the sum of tables from n+1 to last table
	 * return false once the invariant is broken. 
	 */
	public boolean checkInvariant(){
		boolean result = true;
		int[] tableCapacities = new int[tableSizeArray.length];
		int[] colTableCapacities = new int[tableSizeArray.length-1];
		
		//1. get all capacities
		for(int i = 0; i < tableSizeArray.length-1; i++){
			tableCapacities[i] = tables.get(i).getCapacity();
		}
		tableCapacities[tableSizeArray.length-1] = lastTable.getCapacity();
		
		for(int i = 0; i < tableSizeArray.length-1; i++)
			colTableCapacities[i] = colTables.get(i).getSize();
		 
		//2. check all invariants
		for(int i = 0; i < tableSizeArray.length-1; i++){
			int sum = 0;
			for(int j = i+1; j < tableSizeArray.length; j++)
				sum += tableCapacities[j];
			if(sum != colTableCapacities[i])
				result = false;
		}
		
		return result;
	}
	
	/**
	 * check deep invariant, i.e. the existence of copies in collision tables for each item
	 * return true if the invariant maintains for all items, false otherwise
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public boolean checkDeepInvariant(int hashSelection) throws InvalidKeyException, NoSuchAlgorithmException{
		boolean result = true;
		HashSet<String> valSet = new HashSet<String>();
		
		//1. collect all the values in tables and collision tables, remove duplicate automatically
		for(int i = 0; i < tableSizeArray.length-1; i++){
			HTable curTable = tables.get(i);
			for(int j = 0; j < curTable.getHtable().size(); j++){
				Item curItem = curTable.getHtable().get(j);
				if(curItem != null && curTable.bitmapCheck(j)){
					valSet.add(curItem.getValue().getVal());
				}		
			}
		}
		for(int i = 0; i < lastTable.getBdTable().size(); i++){
			BDHopItem curBDHopItem = lastTable.getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				valSet.add(curBDHopItem.getItem().getValue().getVal());				
			}
		} 
		for(int i = 0; i < tableSizeArray.length-1; i++){
			CollisionTable curColTable = colTables.get(i);
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				ArrayList<Item> curList = curColTable.getColTable().get(j);
				if(!curList.isEmpty()){
					for(Item nItem : curList)
						valSet.add(nItem.getValue().getVal());						
				}
			}
		}
		//System.out.println("value count: " + valSet.size());
		
		//2. use each value to locate the item in tables, if not exist, wrong deletion result.
		for(String val : valSet){
			//System.out.println("current str: " + val);
			ArrayList<Integer> locateResult = locateItem(0, new Item(1,new Value(val)), hashSelection);
			if(locateResult.get(0) == -1 && locateResult.get(1) == -1){
				//System.out.println("val: " + val + " happens not to be inside!");
				for(int i = 0; i < tableSizeArray.length-1; i++){
					long hash[] = HashFunctions.getHash(hashSelection, val, i, tableSizeArray[i]);
					boolean inColTable = colTables.get(i).findItem(new Item(hash[0],new Value(val)));
					if(inColTable){
						//System.out.println(val + " in colTable: " + i);
					}						
				}
				result = false;
				break;
			}				
		}
		
		/*for(int i = 0; i < tableSizeArray.length; i++){
			if(i == tableSizeArray.length-1){//last table
				for(int j = 0; j < lastTable.getBdTable().size(); j++){
					BDHopItem curBDHopItem = lastTable.getItem(j);
					if(curBDHopItem != null && !curBDHopItem.isEmpty()){
						Item curItem = curBDHopItem.getItem();
						
					}
				}
				
			}else{
				
			}
		}*/
		
		return result;
	}
	
	/**
	 * check second invariant: any colliding list contain more than one items.
	 * return true if the second invariant maintained, false otherwise
	 */
	public boolean check2ndInvariant(){
		boolean result = true;
		
		for(int i = 0; i < tableSizeArray.length-1; i++){
			CollisionTable curColTable = colTables.get(i);
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				if(curColTable.isOnlyOneInList(j))
					result = false;
			}
		}
		
		return result;
	}
	
	/**
	 * add a new item using recursive inserting in one table after another
	 * check two conditions: collision or not, last table or not.
	 * in all tables except the last one, we have three cases:
	 * 1. empty bucket, insert;
	 * 2. not empty bucket with one colliding item inside, remove current item from the position,
	 * add both into a new ArrayList, add both into current collision table, call recursion.
	 * 3. empty bucket but with colliding list. add the given item into the list, add it in a new 
	 * ArrayList (for recursion), call recursion.
	 * in last table we need to collect all the items dropped, after deleting all their copies in
	 * collision tables, use rebuild() to get balance again.
	 * 
	 * targetItems is the set containing all items to be inserted into current table
	 * return a list of two values: 
	 * (0) items involved in adding the given item, (1) items dropped due to this. 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public ArrayList<Integer> addNewItem(int tableNo, int hashSelection, int k,
			ArrayList<Item> targetItems) throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Integer> result = new ArrayList<Integer>();
		int involveSum = 0;  //num of items to be evicted from a double out hash table
		int dropSum = 0;  //num of items dropped accordingly
		long[] hash = null; 
		
		//for test print all items in tables and collision tables
		/*for(int i = 0; i < tableSizeArray.length-1; i++){
			HTable curTable = tables.get(i);
			for(int j = 0; j < curTable.getHtable().size(); j++){
				Item curItem = curTable.getHtable().get(j);
				if(curItem != null && curTable.bitmapCheck(j)){
					System.out.println("table " + i + " has item " + curItem + " with index: " + j);					
				}		
			}
		}
		for(int i = 0; i < lastTable.getBdTable().size(); i++){
			BDHopItem curBDHopItem = lastTable.getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				System.out.println("last table item " + curBDHopItem.getItem() + " with bitmap: " 
						+ curBDHopItem.getBitmap()+ " in index: " + i);
			}
		} 
		for(int i = 0; i < tableSizeArray.length-1; i++){
			CollisionTable curColTable = colTables.get(i);
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				ArrayList<Item> curList = curColTable.getColTable().get(j);
				if(!curList.isEmpty()){
					for(Item nItem : curList)
						System.out.println("col table " + i + " has item " + nItem 
								+ " with index: " + j);
				}
			}
		}*/		
		
		if(tableNo == tableSizeArray.length-1){//last table
			for(Item curItem : targetItems){
				hash = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
						tableNo, tableSizeArray[tableNo]);			
				int insertResult = lastTable.insert(hash[0], curItem.getValue().getVal(), 
						(int)hash[1], k, tableSizeArray[tableNo]);
				//check last table invariant
				boolean lastTableInvariant = lastTable.checkInvariant();
				if(!lastTableInvariant){
					//for test print last table content
					/*for(int i = 0; i < lastTable.getBdTable().size(); i++){
						BDHopItem curBDHopItem = lastTable.getItem(i);
						if(curBDHopItem != null && !curBDHopItem.isEmpty()){
							System.out.println("last table item " + 
									curBDHopItem.getItem() + " with bitmap: " 
									+ curBDHopItem.getBitmap()+ " in index: " + i);
						}
					}
					System.out.println("LAST TABLE INVARIANT BROKEN when inserting item " + curItem);*/
				}					
				
				if(insertResult == 0 || insertResult == 1){//inserted
					//involveSum++;
				}else{//insertion failure
					//delete curItem's copies in all its collision tables
					long[] hashRemove = null;
					for(int i = 0; i < tableNo; i++){
						CollisionTable curColTable = colTables.get(i);
						hashRemove = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
								i, tableSizeArray[i]);
						//Item removedItem = curColTable.clearList((int)hashRemove[1]);
						boolean removedFlag = curColTable.removeItem((int)hashRemove[1], curItem);
						if(!removedFlag){
							System.out.println("empty item to be removed in collision table " + i);
							System.out.println("please pay attention to " + curItem);
						}						
					}
					//involveSum += tableNo;
					dropSum++;					
				}
			}
			rebuild(hashSelection);
			//for test print all items in tables and collision tables
			/*System.out.println();
			System.out.println("before rebuild");
			for(int i = 0; i < tableSizeArray.length-1; i++){
				HTable curTable = tables.get(i);
				for(int j = 0; j < curTable.getHtable().size(); j++){
					Item curItem = curTable.getHtable().get(j);
					if(curItem != null && curTable.bitmapCheck(j)){
						System.out.println("table " + i + " has item " + curItem + " with index: " + j);					
					}		
				}
			}
			for(int i = 0; i < lastTable.getBdTable().size(); i++){
				BDHopItem curBDHopItem = lastTable.getItem(i);
				if(curBDHopItem != null && !curBDHopItem.isEmpty()){
					System.out.println("last table item " + curBDHopItem.getItem() + " with bitmap: " 
							+ curBDHopItem.getBitmap()+ " in index: " + i);
				}
			} 
			for(int i = 0; i < tableSizeArray.length-1; i++){
				CollisionTable curColTable = colTables.get(i);
				for(int j = 0; j < curColTable.getColTable().size(); j++){
					ArrayList<Item> curList = curColTable.getColTable().get(j);
					if(!curList.isEmpty()){
						for(Item nItem : curList)
							System.out.println("col table " + i + " has item " + nItem 
									+ " with index: " + j);
					}
				}
			}*/
		}else{//in other tables
			HTable curTable = tables.get(tableNo);
			CollisionTable curColTable = colTables.get(tableNo);
			ArrayList<Item> recurItems = new ArrayList<Item>();  //uniformly
			for(Item curItem : targetItems){
				hash = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
						tableNo, tableSizeArray[tableNo]);
				int curIndex = (int)hash[1];
				
				if(curTable.getHtable().get(curIndex) == null || (!curTable.bitmapCheck(curIndex) 
						&& curColTable.isCurListEmpty(curIndex))){//case 1
					curTable.insert(curItem, curIndex);
					//involveSum++;
				}else if(curTable.bitmapCheck(curIndex)){//case 2
					Item removedItem = curTable.removeItem(curIndex);
					recurItems.add(curItem);
					recurItems.add(removedItem);
					curColTable.addItem(curIndex, curItem);
					curColTable.addItem(curIndex, removedItem);
					involveSum++;
				}else{//case 3
					recurItems.add(curItem);
					curColTable.addItem(curIndex, curItem);
					//involveSum++;					
				}
			}
			if(!recurItems.isEmpty()){
				ArrayList<Integer> nextResult = addNewItem(tableNo+1, hashSelection, k, recurItems);
				involveSum += nextResult.get(0);
				dropSum += nextResult.get(1);
			}
		}
		
		result.add(involveSum);
		result.add(dropSum);
		return result;
	}
	
	/**
	 * DEPRECATED
	 * add new item
	 * different than insert() because this is a dynamic insertion method.
	 * recursively add new item to the tables
	 * firstly calculate hashes for current value in current table 
	 * check whether current table is the last one
	 * 1. if last table, use its insert();
	 * 2. not last table, check bucket with current index is empty or not.
	 * 2.1 empty, check the corresponding collision table's same index position for empty list
	 * 2.1.1 if empty list, insert a new item with the given value, done.
	 * 2.1.2 not empty list, add the value (item) in the list and call the same method for next table.
	 * 2.2 not empty, remove current item in the position, add these two into the colliding list
	 * and finally call the same add() method for next table for these two values.
	 * return values:
	 * -1 dropped, returned by the last table insert(); tableNo from 0 to (n-1)
	 * for 2.2 returned result is the same value's evetual inserted table No.
	 */
	public int add(int tableNo, int hashSelection, String val, int k)
			throws InvalidKeyException, NoSuchAlgorithmException{
		int result = tableNo;
		long[] hash = HashFunctions.getHash(hashSelection, val,	tableNo, tableSizeArray[tableNo]);
		long curKey = hash[0];
		int curIndex = (int)hash[1];
		if(tableNo == tableSizeArray.length-1){//case 1 last table
			int temp = lastTable.insert(curKey, val, curIndex, k, tableSizeArray[tableNo]);
			if(temp == -1 || temp == 2)
				result = -1;
		}else{
			HTable curTable = tables.get(tableNo);
			CollisionTable curColTable = colTables.get(tableNo);
			Item targetItem = new Item(curKey,new Value(val));
			if(curTable.bitmapCheck(curIndex)){//case 2.2
				Item curItem = curTable.removeItem(curIndex);
				curColTable.addItem(curIndex, curItem);
				curColTable.addItem(curIndex, targetItem);
				result = add(tableNo+1, hashSelection, val, k);
				add(tableNo+1, hashSelection, curItem.getValue().getVal(), k);
				//int otherAddResult = add(tableNo+1, hashSelection, curItem.getValue().getVal(), k);
				/*System.out.println(curItem.getValue().getVal() + 
						" is finally inserted into table " + otherAddResult);*/
			}else{//case 2.1
				if(curColTable.isCurListEmpty(curIndex)){//case 2.1.1
					curTable.insert(targetItem, curIndex);
				}else{//case 2.1.2
					curColTable.addItem(curIndex, targetItem);
					result = add(tableNo+1, hashSelection, val, k);
				}
			}
		}
		
		return result;
	}
	/**
	 * pure delete, for test delete and rebuild
	 * given an element's table number and the index, delete it from the table and all its copies in
	 * the previous collision tables.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void pureDelete(Item targetItem, int tableNo, int targetIndex, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		long[] hash = null;
		
		//1. delete the item
		if(tableNo == tableSizeArray.length-1){//in last table
			int lastDeleteRsult = lastTable.deleteItem(targetItem, hashSelection, tableNo);
			if(lastDeleteRsult == 0)
				System.out.println("empty deletion");
			//lastTable.delete(originalIndex);
		}else{
			tables.get(tableNo).clearBit(targetIndex);
		}
		//2. remove all its copies from collision tables
		for(int i = 0; i < tableNo; i++){
			CollisionTable curColTable = colTables.get(i);
			hash = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
					i, tableSizeArray[i]);
			boolean removeResult = curColTable.removeItem((int)hash[1], 
					new Item(hash[0], targetItem.getValue())); 
			
			if(!removeResult){
				System.out.println("impossibly remove an empty item");
			}
		}
	}
		
	/**
	 * in the colliding list of (tableNo) with (targetIndex) there is only one item: targetItem.
	 * firstly remove it from the list;
	 * secondly insert it into the same table (tableNo) in the same position (targetIndex);
	 * thirdly locate the item in the next tables, and remove it from one table;
	 * fourthly iterate from (tableNo+1) collision table until the last to
	 * remove its copies from these colliding lists and meanwhile if any list contains only one
	 * item after deletion, call the method on the only item.  
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public int deleteItem(Item targetItem, int tableNo, int targetIndex, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		long[] hash = null;
		int result = 1;
		
		//1. remove the targetItem from its colliding list in tableNo's targetIndex
		CollisionTable curColTable = colTables.get(tableNo);
		Item curItem = curColTable.clearList(targetIndex);
		if(curItem == null){
			//System.out.println("empty item to be removed?");
			//System.out.println("please pay attention to " + targetItem);
			return 0;
		}			
		if(!curItem.equals(targetItem))
			System.out.println("remove the wrong item from a colliding list");
		
		//2. insert it into the same table (tableNo) in the same position (targetIndex);
		HTable curTable = tables.get(tableNo);
		curTable.insert(targetItem, targetIndex);

		//3. locate the item in the next tables, and remove it from one table;
		ArrayList<Integer> orignalPos = locateItem(tableNo+1, targetItem, hashSelection);
		//maybe sometimes recursive deletion happens, so decide whether or not deletion should be done
		int originalTableNo = orignalPos.get(1);
		int originalIndex = orignalPos.get(0);
		
		if(originalTableNo == -1 && originalIndex == -1){
			return 0;
		}
		
		if(originalTableNo == tableSizeArray.length-1){//in last table
			int lastDeleteRsult = lastTable.deleteItem(targetItem, hashSelection, originalTableNo);
			if(lastDeleteRsult == 0)
				System.out.println("empty deletion");
			//lastTable.delete(originalIndex);
		}else{
			tables.get(originalTableNo).clearBit(originalIndex);
		}
		
		//4.iterate from (tableNo+1) collision table until the last to
		// remove its copies from these colliding lists and meanwhile if any list contains only one
		// item after deletion, add the item into queue. 
		HashSet<CapItem> removedList = new HashSet<CapItem>();
		for(int i = tableNo+1; i < tableSizeArray.length-1; i++){
			hash = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
					i, tableSizeArray[i]);
			int nextIndex = (int)hash[1];
			CollisionTable nextColTable = colTables.get(i);
			
			boolean removeResult = nextColTable.removeItem(nextIndex, 
					new Item(hash[0], targetItem.getValue())); 
			
			if(!removeResult){
				//System.out.println("impossibly remove an empty item");
			}					
			if(nextColTable.isOnlyOneInList(nextIndex)){
				Item removedItem = nextColTable.getList(nextIndex).get(0);
				if(removedItem == null)
					System.out.println("fail to get the item to be removed in collision list");
				//result += deleteItem(removedItem, i, nextIndex, hashSelection);
				removedList.add(new CapItem(removedItem, i, nextIndex));
			}			
		}
		
		//5. call the same method on queue's each CapItem.
		//remove duplicate in the list
		ArrayList<CapItem> noDupList = new ArrayList<CapItem>();
		for(CapItem rItem : removedList){
			boolean dupFlag = false;
			for(CapItem nItem : noDupList){
				if(rItem.equals(nItem))
					dupFlag = true;
			}
			if(!dupFlag)
				noDupList.add(rItem);
		}
		for(CapItem cItem : noDupList){
			Item dItem = cItem.getItem();
			int dTableNo = cItem.getTableNo();
			int dIndex = cItem.getIndex();
			result += deleteItem(dItem, dTableNo, dIndex, hashSelection);
		}
					
		return result;
	}
	
	/**
	 * remove an item from the tables
	 * firstly remove the item from current able.
	 * then remove its copies in previous collision tables.
	 * if the colliding list containing the given item has only one item left,
	 * call deleteItem() on this left item. 
	 * notice we have to calculate the item's indices using other hash functions.
	 * return the number of items the deletion involves.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 *
	 */
	public int remove(int targetTableNo, int targetIndex, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		
		//for test
				
		/*for(int i = 0; i < tableSizeArray.length-1; i++){
			int sizeForEach = tables.get(i).getCapacity();
			System.out.println("table " + i + " with capacity: " + sizeForEach);
		}
		System.out.println("last table capacity: " + lastTable.getCapacity());
		
		for(int i = 0; i < tableSizeArray.length-1; i++)
			System.out.println("collision table " + i + " size: " + colTables.get(i).getSize());*/
		
		//for test print all items in tables and collision tables
		/*for(int i = 0; i < tableSizeArray.length-1; i++){
			HTable curTable = tables.get(i);
			for(int j = 0; j < curTable.getHtable().size(); j++){
				Item curItem = curTable.getHtable().get(j);
				if(curItem != null && curTable.bitmapCheck(j)){
					System.out.println("table " + i + " has item " + curItem + " with index: " + j);					
				}		
			}
		}
		for(int i = 0; i < lastTable.getBdTable().size(); i++){
			BDHopItem curBDHopItem = lastTable.getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				System.out.println("last table item " + curBDHopItem.getItem() + " with bitmap: " 
						+ curBDHopItem.getBitmap()+ " in index: " + i);
			}
		} 
		for(int i = 0; i < tableSizeArray.length-1; i++){
			CollisionTable curColTable = colTables.get(i);
			for(int j = 0; j < curColTable.getColTable().size(); j++){
				ArrayList<Item> curList = curColTable.getColTable().get(j);
				if(!curList.isEmpty()){
					for(Item nItem : curList)
						System.out.println("col table " + i + " has item " + nItem 
								+ " with index: " + j);
				}
			}
		}*/
		
		//for test
		//System.out.println("index 14 prev in last table: " + lastTable.getPrevItem(14));
		
		int result = 1;
		Item targetItem = null;
		
		//1. get item to be removed
		if(targetTableNo == tableSizeArray.length-1){
			if(lastTable.getItem(targetIndex) != null)
				targetItem = lastTable.getItem(targetIndex).getItem();
		}else{
			targetItem = tables.get(targetTableNo).getHtable().get(targetIndex);
		}
		if(targetItem == null)
			return result;
		
		//for test
		//System.out.println("removed item is: " + targetItem);		
		
		//2. delete the item from its table
		if(targetTableNo == tableSizeArray.length-1){//in last table
			//lastTable.delete(targetIndex);
			lastTable.deleteItem(targetItem, hashSelection, targetTableNo);
		}else{
			tables.get(targetTableNo).clearBit(targetIndex);			
		}
		
		//3. delete its copies in collision tables, after deletion if only one item in the list,
		//add the item into queue
		ArrayList<CapItem> removedList = new ArrayList<CapItem>();
		for(int i = 0; i < targetTableNo; i++){
			//recalculate the hash
			long[] hash = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
					i, tableSizeArray[i]);
			int curIndex = (int)hash[1];
			CollisionTable curColTable = colTables.get(i);
			
			//for test
			/*System.out.println("current colliding list in collision table " + i + " has size: "
					+ curColTable.getList((int)hash[1]).size());*/
			boolean removeResult = curColTable.removeItem(curIndex, 
					new Item(hash[0], targetItem.getValue())); 
			/*System.out.println("current colliding list in collision table " + i + " has size: "
					+ curColTable.getList((int)hash[1]).size());*/
			
			if(!removeResult){
				//System.out.println("impossibly remove an empty item");
			}					
			if(curColTable.isOnlyOneInList(curIndex)){
				//System.out.println("current colliding table capacity: " + curColTable.getSize());				
				Item removedItem = curColTable.getList(curIndex).get(0);
				if(removedItem == null)
					System.out.println("fail to get the item to be removed in collision list");
				//result += deleteItem(removedItem, i, curIndex, hashSelection);
				removedList.add(new CapItem(removedItem, i, curIndex));
			}
		}
		//4. call delete() on these items
		//remove duplicate in the list
		ArrayList<CapItem> noDupList = new ArrayList<CapItem>();
		for(CapItem rItem : removedList){
			boolean dupFlag = false;
			for(CapItem nItem : noDupList){
				if(rItem.equals(nItem))
					dupFlag = true;
			}
			if(!dupFlag)
				noDupList.add(rItem);
		}
		
		for(CapItem cItem : noDupList){
			Item dItem = cItem.getItem();
			int dTableNo = cItem.getTableNo();
			int dIndex = cItem.getIndex();
			result += deleteItem(dItem, dTableNo, dIndex, hashSelection);
		}		
		
		//rebuild if necessary
		//rebuild(hashSelection);
		
		return result;
	}
	
	/**
	 * locate a given item's position in a table
	 * concerning duplicate value, give another parameter of beginTableNo
	 * return both index, arraylist(0), and tableNo, arraylist(1) of the item
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public ArrayList<Integer> locateItem(int beginTableNo, Item targetItem, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Integer> result = new ArrayList<Integer>();		
		for(int i = 0; i < 2; i++)
			result.add(-1);  //initialization
		if(targetItem == null)
			return result;
		
		int temp;  //temporary query result
		long hash[] = new long[2];  //hash digests
		
		//1. try from table beginTableNo
		for (int tableIndex = beginTableNo; tableIndex < tableSizeArray.length - 1; tableIndex++) {
			// create key and index from val and query current table
			HTable curTable = tables.get(tableIndex);
			hash = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
					tableIndex, tableSizeArray[tableIndex]);
			//package a new Item with the same value of targetItem but with new key in current table
			Item curItem = new Item(hash[0], targetItem.getValue());
			
			temp = curTable.query(curItem, (int) hash[1]);
			if (temp == 1){
				result.set(0, (int) hash[1]);
				result.set(1, tableIndex);
				break;
			}				
		}

		// 2. try the last table if searching in previous tables does not locate the item
		if(result.get(0) == -1){
			hash = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
					tableSizeArray.length - 1, tableSizeArray[tableSizeArray.length - 1]);			
			
			//last table's query() cannot meet the locate requirement.
			//so use last table's locate() method.
			temp = lastTable.locateItem(targetItem, hashSelection, tableSizeArray.length - 1);
			//temp = lastTable.locateItem(targetItem, hashSelection, tableSizeArray.length - 1, hash);
			if (temp != -1){
				result.set(0, temp);
				result.set(1, tableSizeArray.length - 1);
			}
		}				
		
		return result;
	}
	
	/**
	 * collision table query, check (n-1) collision tables whether the corresponding list is empty.
	 * output is a boolean array (size n-1) to indicate query result: if empty set true; otherwise false.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public ArrayList<Boolean> colTableQuery(String val, int hashSelection) throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for(int i = 0; i < tableSizeArray.length-1; i++)
			result.add(false);  //initialization
		long hash[] = null;  //hash digests
		for(int i = 0; i < tableSizeArray.length-1; i++){
			//create key and index from val and check current collision table's list
			CollisionTable curColTable = colTables.get(i);
			hash = HashFunctions.getHash(hashSelection, val, i, tableSizeArray[i]);
			boolean temp = curColTable.isCurListEmpty((int)hash[1]);
			if(temp)
				result.set(i, true);
		}
		
		return result;
	}
	
	/**
	 * filter query, only check (n-1) tables' bitmaps, input is a target value.
	 * output is a boolean array (size n-1) to indicate query result: false not bit set, true bit set.
	 * so each table i has two values.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public ArrayList<Boolean> filterQuery(String val, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Boolean> result = new ArrayList<Boolean>();
		for(int i = 0; i < tableSizeArray.length-1; i++)
			result.add(false);  //initialization
		long hash[] = null;  //hash digests
		for(int i = 0; i < tableSizeArray.length-1; i++){
			//create key and index from val and check current table's bitmap
			HTable curTable = tables.get(i);
			hash = HashFunctions.getHash(hashSelection, val, i, tableSizeArray[i]);
			boolean temp = curTable.bitmapCheck((int)hash[1]);
			if(temp)
				result.set(i, true);
		}
		
		return result;
	}
	
	/**
	 * query an item in the tables
	 * input is a target value.
	 * output is an integer array to indicate query result.
	 * searching is conducted in parallel upon all tables, but the order is from 0 to (n-1) table
	 * without false positive since colliding items must be in positions they never collide afterwards.
	 * so result array has a size of n, the number of tables. 
	 * initial value for each array bucket is -1.
	 * each table i (from 0 to n-1) corresponds to i position with three possible values.  
	 * -1 no such item in the table; 0 match; 1 false positive case 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 *   
	 */
	public ArrayList<Integer> query(String val, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int i = 0; i < tableSizeArray.length; i++)
			result.add(-1);  //initialization
		int temp;  //temporary query result
		long hash[] = new long[2];  //hash digests
		
		//1. try the previous (n-1) tables
		for(int tableIndex = 0; tableIndex < tableSizeArray.length-1; tableIndex++){
			//create key and index from val and query current table
			HTable curTable = tables.get(tableIndex);
			hash = HashFunctions.getHash(hashSelection, val, tableIndex, tableSizeArray[tableIndex]);
						
			temp = curTable.query(new Item(hash[0], new Value(val)), (int)hash[1]);
			if(temp == 1)
				result.set(tableIndex, 0);
			else if(temp == 2)
				result.set(tableIndex, 1);
		}
		
		//2. try the last table
		hash = HashFunctions.getHash(hashSelection, val, tableSizeArray.length-1, 
				tableSizeArray[tableSizeArray.length-1]);		
		temp = lastTable.query(new BDHopItem(hash[0], val, k), (int)hash[1]);
		if(temp == 1 || temp == 2)
			result.set(tableSizeArray.length-1, 0);
		
		return result;
	}
	
	/**
	 * build the first table (never be BDHopTable, must be a HTable) using input strings
	 * if no collision in the first table, next build() never happens.
	 * set the number of items inserted into the first table or collision numbers
	 * colCounter[0]: number of items successfully inserted
	 * colCounter[1]: number of collision in the first table
	 * return whether to build next table, true YES, false NO.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public boolean buildFirstTable(ArrayList<String> source, int hashSelection, int[] colCounter) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		boolean result = false;
		int temp = -1;  //insert result
		int dupCounter = 0;  //duplicate counter
		
		//for test
		//System.out.println("total num: " + source.size());
		
		for(int i = 0; i < source.size(); i++){
			temp = insert(source.get(i), 0, hashSelection);
			if(temp == -1){//duplicate
				System.out.println("to be dropped");
			}else if(temp == 0){
				colCounter[0]++;
			}else if(temp == 1){
				result = true;  //collision exists
				colCounter[1]++;
			}else if(temp == 3){
				dupCounter++;
			}else{//never happen because this is not the last table
				System.out.println("impossible insert in the last table.");
			}
		}
		
		/*System.out.println("table 0 has item number: " + tables.get(0).getCapacity());
		System.out.println("collision table 0 has item number: " + colTables.get(0).getSize());*/
		System.out.println("duplicate item count: " + dupCounter);
		
		return result;
	}
	
	/**
	 * build one table (not the first) from table 1 to last table
	 * so parameter tableNo > 0.
	 * set the number of items inserted into current table or collision numbers
	 * colCounter[0]: number of items successfully inserted
	 * colCounter[1]: number of collision in the first table
	 * but if current table is last table,
	 * colCounter[0]: number of items successfully inserted
	 * colCounter[1]: number of items dropped or duplicated
	 * return whether to build next table, true YES, false NO.
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * 
	 */
	public boolean buildTable(int tableNo, int hashSelection, int[] colCounter) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		boolean result = false;
		CollisionTable prevColTable = colTables.get(tableNo-1);  //previous collision table
		ArrayList<Item> items = prevColTable.getAllItems();  //get all the colliding items
		int temp = -1;
				
		for(Item curItem : items){
			temp = insert(curItem.getValue().getVal(), tableNo, hashSelection);	
			if(temp == -1){//duplicate
				if(tableNo == tableSizeArray.length-1){
					colCounter[1]++;
					handleDroppedItem(curItem.getValue().getVal(), hashSelection);
				}
								
			}else if(temp == 0){
				if(tableNo == tableSizeArray.length-1)
					System.out.println("impossible for last table");
				else{
					colCounter[0]++;
				}
			}else if(temp == 1){
				if(tableNo == tableSizeArray.length-1)
					System.out.println("impossible for last table");
				else{
					result = true;  //collision exists
					colCounter[1]++;
				}				
			}else{//may happen for last table
				if(tableNo == tableSizeArray.length-1)
					colCounter[0]++;				
			}
		}
				
		return result;
	}
	
	/**
	 * rebuild after deleting copies of dropped items
	 * used to restore balance of tables and remove the second invariant, which is
	 * that any non-empty colliding list contains more than one items.
	 * no input, return the number of items involved
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public int rebuild(int hashSelection) throws InvalidKeyException, NoSuchAlgorithmException{
		int result = 0;
		HTable curTable = null;
		CollisionTable curColTable = null;
		
		//iterate all collision tables
		for(int i = 0; i < tableSizeArray.length-1; i++){
			curTable = tables.get(i);
			curColTable = colTables.get(i);
			//iterate all non-empty list in current collision table
			for(int j = 0; j < tableSizeArray[i]; j++){
				if(curColTable.isOnlyOneInList(j)){//only one item in current list
					Item curItem = curColTable.getItem(j);  //remove the item from the list 
					curTable.insert(curItem, j);  //insert the item in its hash table
					result += 2;  //two copies of the item involved
					
					//locate curItem from table (i+1) to last table and delete it
					ArrayList<Integer> pos = locateItem(i+1, curItem, hashSelection);
					int originalTableNo = pos.get(1);  //original table number where the item resides
					int originalTableIndex = pos.get(0);  //original table index
					if(originalTableNo == tableSizeArray.length-1){//in last table
						int temp = lastTable.deleteItem(curItem, hashSelection, originalTableNo);
						if(temp == 0)
							System.out.println("delete zero in last table");
					}else{
						if(originalTableNo != -1){
							Item removedItem = tables.get(originalTableNo).removeItem(originalTableIndex);
							if(!removedItem.getValue().equals(curItem.getValue()))
								System.out.println("delete wrong item in later table");
						}						
					}
					result++;  //another item involved.
										
					//iterate collision table from (i+1) to originalTableNo-1 and delete curItem's copies
					long[] hash = null;
					for(int k = i+1; k < originalTableNo; k++){
						CollisionTable colTable = colTables.get(k);
						hash = HashFunctions.getHash(hashSelection, curItem.getValue().getVal(), 
								k, tableSizeArray[k]);
						boolean removeResult = colTable.removeItem((int)hash[1], curItem);	
						if(!removeResult)
							System.out.println("remove wrong item in later collision table");
					}
					result += (originalTableNo-i-1);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * remove all the copies of each dropped item in all its collision tables
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public void handleDroppedItem(String val, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		long[] hash = null;
		
		for(int i = 0; i < tableSizeArray.length-1; i++){
			CollisionTable curColTable = colTables.get(i);
			hash = HashFunctions.getHash(hashSelection, val, i, tableSizeArray[i]);
			boolean eachResult = curColTable.removeItem((int)hash[1], new Item(hash[0], new Value(val)));
			if(!eachResult)
				System.out.println("something wrong in deleting copies of dropped item.");
		}		
	}
	
	/**
	 * try to insert current value. 
	 * two outcomes: (1) success. either current bucket is null, or its collision list is empty.
	 * (2) failure. collision exists in this position, insert it into its collision list.
	 * the two conditions happen in the previous (n-1) HTables.
	 * For last table, the val is possibly dropped.
	 * output: -1 dropped because last table cannot hold it; 0 insert into current HTable; 1 insert into
	 * current CollisionTable; 2 insert into the last table; 3 duplicate string
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public int insert(String val, int tableNo, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		int result = -1;
		
		
		//change hash function here
		long hash[] = HashFunctions.getHash(hashSelection, val, tableNo, tableSizeArray[tableNo]);
		long key = hash[0];
		int index = (int) hash[1];
		
		//1. first check whether current table is the last one.
		if (tableNo == tableSizeArray.length - 1) {// last one
			result = lastTable.insert(key, val, index, k, tableSizeArray[tableNo]);
			if (result == 0 || result == 1) {
				result = 2;
			} 
		}else{// not the last one
			HTable currentTable = tables.get(tableNo);  
			Item targetItem = new Item(key, new Value(val));   
			int queryResult = currentTable.query(targetItem, index);
			
			//2. depending on query result, then (1) empty position, insert; (2) duplicate, drop 
			//(2) collision, insert into current CollisionTable for this HTable.			
			if(queryResult == 0 && colTables.get(tableNo).isCurListEmpty(index)){//insert in current table
				currentTable.insert(targetItem, index);
				result = 0;
			}else{//collision resolution
				if(queryResult == 1){//duplicate 
					//System.out.println("duplicate");
					return 3;
				}
				CollisionTable curColTable = colTables.get(tableNo);
				if(currentTable.bitmapCheck(index)){//bit is set, move the item into collision list
					Item toBeRemovedItem = currentTable.removeItem(index);
					curColTable.addItem(index, toBeRemovedItem);
				}
				curColTable.addItem(index, targetItem);
				result = 1;
			}
		}
		
		return result;		
	}
	
	//get the colliding list for a specific item
	//now for remove() test only
	public ArrayList<Item> getListForItem(int tableNo, Item item, int hashSelection) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		ArrayList<Item> result = null;
		long[] hash = HashFunctions.getHash(hashSelection, item.getValue().getVal(),	
				tableNo, tableSizeArray[tableNo]);
		result = colTables.get(tableNo).getColTable().get((int)hash[1]);
		return result;
	}
	
	/*
	 * main method end
	 * -------------------------------------------------------------------------
	 */

	public ArrayList<HTable> getTables() {
		return tables;
	}

	public void setTables(ArrayList<HTable> tables) {
		this.tables = tables;
	}

	public ArrayList<CollisionTable> getColTables() {
		return colTables;
	}

	public void setColTables(ArrayList<CollisionTable> colTables) {
		this.colTables = colTables;
	}

	public BDHopTable getLastTable() {
		return lastTable;
	}

	public void setLastTable(BDHopTable lastTable) {
		this.lastTable = lastTable;
	}

	public int[] getTableSizeArray() {
		return tableSizeArray;
	}

	public void setTableSizeArray(int[] tableSizeArray) {
		this.tableSizeArray = tableSizeArray;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
		int [] tableSizes1 = {100,100,100};
		int k1 = 4;
		int hashSelection = 0;
		TableCollection tableCollection = new TableCollection(tableSizes1, k1);
		
		//1. test insert(). given the first value, insert it into the first table
		String val1_1 = "For example I want to talk with my wife about my daughter.111";
		long hash[] = HashFunctions.getHash(hashSelection, val1_1, 0, tableSizes1[0]);
		/*int result1_1 = tableCollection.insert(val1_1, 0, hashSelection);
		System.out.println("insert the first one: " + result1_1);*/
		//System.out.println("bit set? " + tableCollection.getTables().get(0).bitmapCheck((int)hash[1]));
		
		String val1_2 = "For example I want to talk with my wife about my daughter.111";
		/*int result1_2 = tableCollection.insert(val1_2, 0, hashSelection);
		System.out.println("insert the second one colliding with first: " + result1_2);*/
		
		/*Item item1_1 = tableCollection.getColTables().get(0).getItem((int)hash[1]);
		System.out.println("colliding one in collision table: " + item1_1);*/
		//System.out.println(tableCollection.getColTables().get(0).getColTable().get((int)hash[1]).size());
		//System.out.println("bit set? " + tableCollection.getTables().get(0).bitmapCheck((int)hash[1]));
		
		//2. test buildFirstTable()
		ArrayList<String> source = new ArrayList<String>();
		source.add(val1_1);
		source.add(val1_2);		
		int[] colCounter = new int[2];
		for(int i = 0; i < colCounter.length; i++)
			colCounter[i] = 0;
		boolean buildResult1 = tableCollection.buildFirstTable(source, hashSelection, colCounter);
		System.out.println("build first table result: " + buildResult1);
		
		//3. test buildTable()
		boolean buildResult2 = tableCollection.buildTable(1, hashSelection, colCounter);
		System.out.println("build second table result: " + buildResult2);
		
		boolean buildResult3 = tableCollection.buildTable(2, hashSelection, colCounter);
		System.out.println("build third table result: " + buildResult3);
		
		//4. test locate()
		ArrayList<Integer> locateResult1 = tableCollection.locateItem(0, 
				new Item(hash[0],new Value(val1_1)), hashSelection);
		for(int r: locateResult1)
			System.out.println(r + " ");
		
		//5. test query()
		ArrayList<Integer> queryResults = tableCollection.query(val1_1, hashSelection);
		for(int q: queryResults)
			System.out.println(q + " ");
		ArrayList<Item> items = tableCollection.getLastTable().getAllItems();
		for(Item item: items)
			System.out.println(item);
		
		//6. test remove()
		System.out.println("before deletion 0 collision table has items: " + 
				tableCollection.getColTables().get(0).getSize());
		System.out.println("before deletion 1 collision table has items: " + 
				tableCollection.getColTables().get(1).getSize());
		int num = tableCollection.remove(2, 14, hashSelection);
		System.out.println("items involved in the deletion: " + num);
		System.out.println("after deletion 0 collision table has items: " + 
				tableCollection.getColTables().get(0).getSize());
		System.out.println("after deletion 1 collision table has items: " + 
				tableCollection.getColTables().get(1).getSize());
	}

}
