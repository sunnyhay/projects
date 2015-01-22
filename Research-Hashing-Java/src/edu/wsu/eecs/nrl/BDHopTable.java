package edu.wsu.eecs.nrl;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * Bidirectional Hop table, the last in the hierarchy to hold all colliding items finally.
 * the total number of elements in the table is the sum of colCounter and those inserted into empty buckets
 */
public class BDHopTable {
	private HashMap<Integer,BDHopItem> bdTable;
	private int colCounter;  //count the number of colliding elements inserted successfully
	private int failCounter;  //count the number of colliding elements failed to insert
	/*
	 * two counters are duplicate to return values of 1 and -1 for insert().
	 */
	private int k;  //determining factor for bitmap size
	
	public BDHopTable(int size, int k){
		this.setBdTable(new HashMap<Integer,BDHopItem>(size));
		for(int i = 0; i < size; i++)
			bdTable.put(i,null);
		this.setColCounter(0);
		this.setFailCounter(0);
		this.setK(k);
	}
	
	/* -------------------------------------------------
	 * main methods start
	 */
	/**
	 * check invariant in last table
	 * that's if bit 0 (bucket empty?) is set, no next no prev, the next bits are not set.
	 * in other words, for these 2k+3 bits, if no next or no prev bits (1 or 2) is set, no relative 
	 * distance is assigned. 
	 * need to traverse last table to check each non-empty BDHopItem whenever a deletion or insertion
	 * takes place.
	 * output: true if invariant is kept, false otherwise.
	 */
	public boolean checkInvariant(){
		boolean result = true;
		int tableSize = getBdTable().size();
		
		for(int i = 0; i < tableSize; i++){
			BDHopItem curBDHopItem = getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				boolean nextWrong = false;
				boolean prevWrong = false;
				//check next position block
				for(int j = 3; j < (tableSize-3)/2+2; j++)
					if(curBDHopItem.getBitmapPosition(j) && !curBDHopItem.hasNext())
						nextWrong = true;
				for(int j = (tableSize-3)/2+3; j < tableSize; j++)
					if(curBDHopItem.getBitmapPosition(j) && !curBDHopItem.hasPrev())
						prevWrong = true;
				if(nextWrong || prevWrong){
					result = false;
					//System.out.println("this broken item " + curBDHopItem);
				}
			}
		} 
		
		return result;
	}
		
	/**
	 * locate an item in the last table, return its index
	 * -1 if not found
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public int locateItem(Item targetItem, int hashSelection, int tableNo) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		int result = -1;
		long hash[] = HashFunctions.getHash(hashSelection, targetItem.getValue().getVal(), 
				tableNo, bdTable.size());
		//System.out.println("inside locate method with key: " + hash[0] + " and index: " + hash[1]);
				
		BDHopItem curBDHopItem = getItem((int) hash[1]);
		if(curBDHopItem == null || curBDHopItem.isEmpty())
			return result;
		//package a new item using targetItem
		Item temp = new Item(hash[0],targetItem.getValue());
		
		if(curBDHopItem.getItem().equals(temp)){//found
			return (int) hash[1];
		}else{
			if(curBDHopItem.hasNext()){
				int nextPos = (curBDHopItem.getPosFromBitmapBlock(k, true) + (int) hash[1]) % bdTable.size();
				BDHopItem nextBDHopItem = getNextItem((int) hash[1]);
				if(nextBDHopItem != null && nextBDHopItem.getItem().equals(temp))
					return nextPos;
			}
		}
		
		return result;
	}
	
	/**
	 * In query, if the first bit is clear, sure query fails without any match. 
	 * Otherwise, still need to check current item’s key for further match since it is possible current 
	 * item is a colliding item to some other element. Even if the check is false, we inspect the second 
	 * bit. If it’s set, we check the first k bits to get position of next possible item. Check that item. 
	 * At most 2 items are checked during a query.
	 * 
	 * query the table using a given BDHopItem. return values: 0, 1, or 2, 3.
	 * 0: empty bucket; 1: match.
	 * 2: mismatch, but its colliding item match; 
	 * 3: mismatch and no colliding item or colliding item mismatch.
	 */
	public int query(BDHopItem targetBDHopItem, int index){
		int result = -1;
		BDHopItem curItem = bdTable.get(index); 
		
		if(curItem == null || curItem.isEmpty())  
			//null bucket (not yet initialized) or set empty-bit already
			result = 0;
		else if(curItem.equals(targetBDHopItem))  //match
			result = 1;
		else{
			//check its next item in colliding position
			 if(curItem.hasNext()){//there is a colliding item
				 int pos = curItem.getPosFromBitmapBlock(k, true);  //pos is relative position
				 BDHopItem next = bdTable.get(index+pos);  //get next item.
				 if(next!= null && next.equals(targetBDHopItem))
					 result = 2;
				 else
					 result = 3;				 
			 }else
				 result = 3;  //no colliding item
		}
		
		return result;
	}
	
	//assistant method for next position calculation
	public int getPos(int pos, int k, int tableSize){
		int result = pos;
		int bound = (int)Math.pow(2, k)/2;
		
		if(result < -bound)
			result = tableSize + result;
		if(result > bound)
			result = result - tableSize;
		
		return result;
	}
	
	/*
	 * In insertion, if the first bit is clear, insert the item. Otherwise, check the second bit. If it is 
	 * clear, we sure can insert it in backup position. Using a selection mechanism we can set the second 
	 * bit and the first k bits for next element to proper position. If the second bit is set, drop the item 
	 * and give an alert.
	 * input: a designated position in bdTable and the value, tableSize for last table.
	 * output: -2 fail of no available room; -1 fail as next bucket is occupied; 
	 * 0 insert to current empty bucket; 
	 * 1 current not empty and so inserted in the first available position according to probeNext().
	 * 2 duplicate item, so keep current. 
	 */
	
	public int insert(long key, String val, int index, int k, int tableSize){
		int result = -1;
		BDHopItem targetItem = new BDHopItem(new Item(key, new Value(val)), k);  
		//new instance to be inserted
		BDHopItem curItem = bdTable.get(index); 
		
		targetItem.setBitmapPosition(0);  //set not-empty bit for target BDHopItem
		if(curItem == null || curItem.isEmpty()){ 
			//if current bucket is not initialized 
			//or empty indicated by its bitmap status
			bdTable.put(index, targetItem);
			result = 0;
			
			//for test
			//System.out.println("empty insert index: " + index + " with item: " + targetItem);			
		}else if(curItem.equals(targetItem)){//for duplicate item
			result = 2;
			/*System.out.println("key: " + targetItem.getItem().getKey() + " with value: "
					+ targetItem.getItem().getValue().getVal());
			System.out.println("current key: " + curItem.getItem().getKey() + " with value: "
					+ curItem.getItem().getValue().getVal());*/
		}else{//current not available, check its next available bucket in the probing sequence.
			if(!curItem.hasNext()){
				int nextIndex = probeNext(getNextSeqByLinear(index, k),tableSize);  
				//index for colliding item to be inserted
				if(nextIndex != -1){//available
					curItem.setBitmapPosition(1);  //set current's colliding bit
					
					int pos1 = getPos(nextIndex-index, k , tableSize);
					curItem.setBitmapBlock(k, pos1, true);  
					//set current's bitmap for colliding item's relative position
					int pos2 = getPos(index-nextIndex, k, tableSize);
					targetItem.setBitmapBlock(k, pos2, false);  
					//set next's bitmap for previous (current)
					targetItem.setBitmapPosition(2);					
					bdTable.put(nextIndex, targetItem);
					result = 1;
					colCounter++;  //increment the counter for inserted colliding elements
					
					//for test
					//System.out.println("collide index: " + nextIndex + " with item: " + targetItem);
					//System.out.println("current index: " + index + " with item: " + curItem);
				}else{
					result = -2;  //neighborhood full
				}
			}else
				failCounter++;  //increment the counter for failed (dropped) elements			
		}		
		return result;
	}
	
	/**
	 * real delete()
	 * four conditions:
	 * 1. no next, no prev. just clear its occupied bit;
	 * 2. no prev, has next. iteratively delete and make all next relations null;
	 * 3. has prev, no next. clear the occupied bit and prev bits for current item and clear
	 * next bits of its prev item;
	 * 4. has prev and has next. clear next bits of its prev item and prev bits for current item.
	 * then apply current item with condition2. 
	 * return: the number of items involved. 0 if no deletion
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public int deleteItem(Item targetItem, int hashSelection, int tableNo) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		int result = 0;
		
		//1. get current BDHopItem in last table
		int targetIndex = locateItem(targetItem, hashSelection, tableNo);
		BDHopItem curBDHopItem = bdTable.get(targetIndex);
		
		if(curBDHopItem == null || curBDHopItem.isEmpty()){
			return result;
		}
		
		if(!curBDHopItem.hasPrev() && !curBDHopItem.hasNext()){//condition1
			curBDHopItem.clearOccupied();
			result = 1;
		}else if(!curBDHopItem.hasPrev() && curBDHopItem.hasNext()){//condition2
			LinkedList<BDHopItem> bdItemStack = new LinkedList<BDHopItem>();
			LinkedList<Item> itemStack = new LinkedList<Item>();
			//for test create a whole same stack
			LinkedList<BDHopItem> backup = new LinkedList<BDHopItem>();
						
			BDHopItem sBDHopItem = curBDHopItem;
			bdItemStack.push(sBDHopItem);
			result = 1;
			//2. get two stacks
			while(sBDHopItem.hasNext()){
				result++;
				int sIndex = locateItem(sBDHopItem.getItem(), hashSelection, tableNo);
				sBDHopItem = getNextItem(sIndex);
				bdItemStack.push(sBDHopItem);
				itemStack.push(new Item(sBDHopItem.getItem().getKey(),
						sBDHopItem.getItem().getValue()));
			}
			backup.addAll(bdItemStack);
			
			//3. iterative deletion
			BDHopItem lastBDBdHopItem = bdItemStack.peek();
			lastBDBdHopItem.clearOccupied();
			lastBDBdHopItem.clearNext(k);
			lastBDBdHopItem.clearPrev(k);
			
			while(bdItemStack.size() > 1){
				BDHopItem nowBDHopItem = bdItemStack.pop();
				BDHopItem prevBDHopItem = bdItemStack.peek();
				nowBDHopItem.clearPrev(k);
				prevBDHopItem.clearNext(k);
				Item replaceItem = itemStack.pop();
				prevBDHopItem.setItem(replaceItem);
			}
		}else if(curBDHopItem.hasPrev() && !curBDHopItem.hasNext()){//condition3
			BDHopItem prevBDHopItem = getPrevItem(targetIndex);
			if(prevBDHopItem != null)
				prevBDHopItem.clearNext(k);  //clear prev's next
			curBDHopItem.clearOccupied();
			curBDHopItem.clearPrev(k);  //clear current's prev
			result = 2;
		}else{//condition4
			LinkedList<BDHopItem> bdItemStack = new LinkedList<BDHopItem>();
			LinkedList<Item> itemStack = new LinkedList<Item>();
			//for test create a whole same stack
			LinkedList<BDHopItem> backup = new LinkedList<BDHopItem>();
						
			BDHopItem sBDHopItem = curBDHopItem;
			bdItemStack.push(sBDHopItem);
			result = 1;
			//2. get two stacks
			while(sBDHopItem.hasNext()){
				result++;
				int sIndex = locateItem(sBDHopItem.getItem(), hashSelection, tableNo);
				sBDHopItem = getNextItem(sIndex);
				bdItemStack.push(sBDHopItem);
				itemStack.push(new Item(sBDHopItem.getItem().getKey(),
						sBDHopItem.getItem().getValue()));
			}
			backup.addAll(bdItemStack);
			
			BDHopItem topBDHopItem = getPrevItem(targetIndex);
			if(topBDHopItem != null)
				topBDHopItem.clearNext(k);  //clear prev's next
			curBDHopItem.clearPrev(k);  //clear current's prev
			
			//3. iterative deletion
			BDHopItem lastBDBdHopItem = bdItemStack.peek();
			lastBDBdHopItem.clearOccupied();
			lastBDBdHopItem.clearNext(k);
			lastBDBdHopItem.clearPrev(k);
			
			while(bdItemStack.size() > 1){
				BDHopItem nowBDHopItem = bdItemStack.pop();
				BDHopItem prevBDHopItem = bdItemStack.peek();
				nowBDHopItem.clearPrev(k);
				prevBDHopItem.clearNext(k);
				Item replaceItem = itemStack.pop();
				prevBDHopItem.setItem(replaceItem);
			}
		}
		
		return result;
	}
	
	/**
	 * deprecated!!!
	 * recursively delete
	 * check three conditions
	 * 1. no prev no next, just delete current;
	 * 2. no next have prev, delete current and set prev's next to null;
	 * 3. have next, move next's item to current position and call delete() for next;
	 */
	public void delete(int curIndex){
		BDHopItem curItem = bdTable.get(curIndex);
		if(curItem == null || curItem.isEmpty()){
			System.out.println("found a weird empty item in last table.");
			return;
		}
			
		if(curItem.hasNext()){//has next
			int nextPos = curItem.getPosFromBitmapBlock(k, true);
			BDHopItem nextItem = getNextItem(curIndex);
			curItem.setItem(nextItem.getItem());  //reset current item use its next item
			int pos = (curIndex+nextPos) % bdTable.size();
			if(pos < 0)
				pos += bdTable.size();
			delete(pos);  //recursively delete next
		}else{//no next
			if(curItem.hasPrev()){//has prev
				BDHopItem prevItem = getPrevItem(curIndex);
				if(prevItem != null)
					prevItem.clearNext(k);  //clear prev's next
				curItem.clearPrev(k);  //clear current's prev
			}
			curItem.clearOccupied();  //make the position empty
		}
	}
	
	/**
	 * get the next item from current 
	 */
	public BDHopItem getNextItem(int curIndex){
		BDHopItem curItem = bdTable.get(curIndex);
		if(curItem.isEmpty() || !curItem.hasNext())
			return null;
		int nextPos = curItem.getPosFromBitmapBlock(k, true);
		//System.out.println("nextPos: " + nextPos);
		int index = (curIndex+nextPos) % bdTable.size();
		if(index < 0)
			index += bdTable.size();
		return bdTable.get(index);
	}
	
	/**
	 * get the prev item from current
	 */
	public BDHopItem getPrevItem(int curIndex){
		BDHopItem curItem = bdTable.get(curIndex);
		if(curItem.isEmpty() || !curItem.hasPrev())
			return null;
		int prevPos = curItem.getPosFromBitmapBlock(k, false);
		int index = (curIndex+prevPos) % bdTable.size();
		if(index < 0)
			index += bdTable.size();
		return bdTable.get(index);
	}
	
	/*
	 * selection mechanism 1: linear probing from position of 1 to 2^(k-1) in forward direction followed, 
	 * by backward from -1 to -2^(k-1), e.g. when k = 4, from 1 to 8; next backwards from -1 to -8.
	 * only input is current item's position in the table. return a collection of indices to be search  
	 */
	
	public ArrayList<Integer> getNextSeqByLinear(int curIndex, int k){
		ArrayList<Integer> sequence = new ArrayList<Integer>();
		int distance = (int)Math.pow(2, k)/2;
				
		for(int i = curIndex+1; i <= curIndex+distance; i++)
			sequence.add(i);			
		for(int i = curIndex-1; i >= curIndex-distance; i--)
			sequence.add(i);
		
		return sequence;
	}

	//probe method. input is a sequence from any probing mechanism.
	//output is the first available position. if -1 is returned, no available.
	private int probeNext(ArrayList<Integer> sequence, int tableSize){
		int result = -1;
		
		for(int i = 0; i < sequence.size(); i++){
			int nextIndex = sequence.get(i)%tableSize; 
			if(bdTable.get(nextIndex) == null || bdTable.get(nextIndex).isEmpty())
				return nextIndex;
		}
		
		return result;
	}
	
	public BDHopItem getItem(int index){
		return bdTable.get(index);
	}
	
	//get all items in current table
	public ArrayList<Item> getAllItems(){
		ArrayList<Item> result = new ArrayList<Item>();
		
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curItem = getItem(i);
			if(curItem != null && (!curItem.isEmpty()))
				result.add(curItem.getItem());			
		}
		
		return result;
	}
	
	//get current capacity of the table, i.e. amount of items
	public int getCapacity(){
		int result = 0;
		
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curItem = getItem(i);
			if(curItem != null && (!curItem.isEmpty()))
				result++;			
		}		
		return result;
	}
	/**
	 * specific test for last table.
	 * return arraylist of three ratios for state 2, state 3 and dropped item in the first case
	 */
	public ArrayList<Double> testLastTable(ArrayList<String> source, int k, int hashSelection, 
			int tableNo, int tableSize) 
			throws InvalidKeyException, NoSuchAlgorithmException{
		int temp = 0;
		long hash[] = new long[2];
		long key;
		int index;
		String val;
		int[] counters = new int[5];
		ArrayList<Double> result = new ArrayList<Double>();
				
		//1. insert
		for(int i = 0; i < source.size(); i++){
			val = source.get(i);
			hash = HashFunctions.getHash(hashSelection, val, tableNo, tableSize);
			key = hash[0];
			index = (int) hash[1];
			temp = insert(key, val, index, k, tableSize);	
			if(temp == 0){
				counters[0]++;
			}else if(temp == 1)
				counters[1]++;
			else if(temp == -1)
				counters[2]++;
			else if(temp == 2)
				counters[3]++;
			else
				counters[4]++;
			
			//test
			//System.out.println("index: " + index + " with item: " + bdTable.get(index));
		}
				
		//System.out.println("last table has " + getCapacity() + " items");
		System.out.println("items inserted in current position: " + counters[0]);
		System.out.println("items inserted in adjacent position: " + counters[1]);
		System.out.println("items fail collision second time: " + counters[2]);
		System.out.println("item duplicate: " + counters[3]);
		System.out.println("items fail neighborhood full: " + counters[4]);
		
		//put into result arraylist
		result.add((double) counters[0] / source.size());
		result.add((double) counters[1] / source.size());
		result.add((double) (counters[2]+counters[4]) / source.size());
		
		//System.out.println("failed counter: " + getFailCounter());
		//System.out.println("collision counter: " + getColCounter());
		
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curBDHopItem = getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				/*System.out.println("last table item " + curBDHopItem.getItem() + " with bitmap: " 
						+ curBDHopItem.getBitmap()+ " in index: " + i);*/
			}
		}
		
		//2. try to locate each item which has been inserted
		/*int notFound = 0;
		for(String str : source){
			hash = HashFunctions.getHash(hashSelection, str, tableNo, tableSize);
			Item curItem = new Item(hash[0], new Value(str));
			int locateResult = locateItem(curItem, hashSelection, tableNo);
			if(locateResult == -1)
				notFound++;
			System.out.println("item: " + curItem + " is in index: " + locateResult);
		}
		System.out.println("not found count: " + notFound);*/
		//for test
		/*System.out.println("next of 3: " + getNextItem(3));
		System.out.println("prev of 4: " + getPrevItem(4));*/
		
		//3. delete all items inserted
		ArrayList<Item> deleteItems = new ArrayList<Item>();
		//System.out.println("current bdtable capacity (not actual item number): " + bdTable.size());
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curBDHopItem = bdTable.get(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty())
				deleteItems.add(curBDHopItem.getItem());
		}
		
		int totalCount = 0;
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curBDHopItem = bdTable.get(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty())
				totalCount++;
		}
		System.out.println("actual item number in the bdtable before deletion: " + totalCount);
		
		//3.1 do deletion 
		int deleteBound = deleteItems.size();
		System.out.println("item number to be deleted: " + deleteBound);
		Collections.sort(deleteItems);
		
		//for test
		/*System.out.println("53 prev item: " + getPrevItem(53));
		System.out.println("53 next item: " + getNextItem(53));*/
		
		int involvedCount = 0;  //total items involved in deletion
		for(int i = 0; i < deleteBound; i++){
			//System.out.println("index to be deleted: " + deleteItems.get(i));
			Item curItem = deleteItems.get(i); 
			//System.out.println("current deleting item: " + curItem);
			int delResult = deleteItem(curItem, hashSelection, tableNo);
			if(delResult == 0)
				System.out.println("empty deletion on item: " + curItem);
			involvedCount += delResult;
		}
		System.out.println("total items involved in deletion: " + involvedCount);
		
		totalCount = 0;
		for(int i = 0; i < bdTable.size(); i++){
			BDHopItem curBDHopItem = bdTable.get(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				totalCount++;
				System.out.println("not deleted item: " + curBDHopItem.getItem());
			}				
		}
		System.out.println("actual item number in the bdtable after deletion: " + totalCount);
		
		return result;
	}
	
	/* main methods end
	 * -------------------------------------------------
	 */
		
	public HashMap<Integer,BDHopItem> getBdTable() {
		return bdTable;
	}

	public void setBdTable(HashMap<Integer,BDHopItem> bdTable) {
		this.bdTable = bdTable;
	}

	public int getColCounter() {
		return colCounter;
	}

	public void setColCounter(int colCounter) {
		this.colCounter = colCounter;
	}
	
	public int getK(){
		return k;
	}
	
	public void setK(int k){
		this.k = k;
	}	

	public int getFailCounter() {
		return failCounter;
	}

	public void setFailCounter(int failCounter) {
		this.failCounter = failCounter;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//1. insert the first item
		/*int tableSize = 100, k = 4;  //size of table and bitmap hop
		BDHopTable lastTable = new BDHopTable(tableSize, k);
		
		String val1 = "I love you for ever and this is a good choice";
		long hash1[] =  HashFunctions.getHash(0, val1,	0, tableSize);
		
		System.out.println("key1: " + hash1[0] + " with index: " + hash1[1]);
		
		int result1 = lastTable.insert(hash1[0], val1, (int)hash1[1], k, tableSize);
		System.out.println("insert first element result: " + result1);*/
		
		//1.1 test delete() condition 1
		/*result1 = lastTable.locateItem(new Item(hash1[0], new Value(val1)), 0, 0);
		System.out.println("locate result before deletion: " + result1);
		
		lastTable.delete((int)hash1[1]);
		
		result1 = lastTable.locateItem(new Item(hash1[0], new Value(val1)), 0, 0);
		System.out.println("locate result after deletion: " + result1);*/
				
		//2. test getNextSeqByLinear()
		/*ArrayList<Integer> nextSeq = lastTable.getNextSeqByLinear((int)hash1[1],k);
		for(int i = 0; i < nextSeq.size(); i++)
			System.out.print(nextSeq.get(i) + " ");
		System.out.println();*/
		
		//3. test probeNext()
		/*int probeResult = lastTable.probeNext(nextSeq);
		System.out.println("probe for first item: " + probeResult);*/
		
		//4. insert a colliding item to the first one
		/*String val2 = "Now you konw this is a loop while I wanna break!";
		long key2 = 1391180111;
		int index2 = 9;
		int result2 = lastTable.insert(key2, val2, index2, k, tableSize);
		System.out.println("insert colliding element with first one: " + result2);*/
		
		//System.out.println("first item: " + lastTable.getItem(index2));
		//System.out.println("second item: " + lastTable.getItem(68));
		
		//4.0 test getAllItems()
		/*ArrayList<Item> allItems = lastTable.getAllItems();
		for(Item item : allItems)
			System.out.println(item);*/
		
		//4.1 test getNextItem()
		/*BDHopItem nextBDHopItem = lastTable.getNextItem(index2);
		System.out.println("nextItem:" + nextBDHopItem);*/
		
		//4.2 test getPrevItem()
		/*BDHopItem prevBDHopItem = lastTable.getPrevItem(68);
		System.out.println("prevItem:" + prevBDHopItem);*/
		
		//4.3 test locate()
		/*int result4 = lastTable.locateItem(new Item(hash1[0], new Value(val1)), 0, 0); 
		System.out.println("locate result: " + result4);
		int result5 = lastTable.locateItem(new Item(key2, new Value(val2)), 0, 0);
		System.out.println("locate result next: " + result5);*/
		
		//4.4 test delete() condition 2 and 3
		/*String val3 = "Suppose we have a absolutely weird box to be revealed together";
		long key3 = 1300517896;
		int index3 = 10;
		int result3 = lastTable.insert(key3, val3, index3, k, tableSize);
		System.out.println("insert colliding element with second one: " + result3);
		
		System.out.println("prev item before deletion:" + lastTable.getItem(index2));
		System.out.println("current item before deletion: " + lastTable.getItem(index3));
		System.out.println("next item before deletion: " + lastTable.getItem(index3+1));
		
		lastTable.delete(index3+1);
		
		System.out.println("prev item after deletion:" + lastTable.getItem(index2));
		System.out.println("current item after deletion: " + lastTable.getItem(index3));
		System.out.println("next item after deletion: " + lastTable.getItem(index3+1));
		System.out.println();
		System.out.println();
		System.out.println();*/
		
		/*lastTable.delete((int)hash1[1]);
		
		result1 = lastTable.locateItem(new Item(hash1[0], new Value(val1)), 0, 0);
		System.out.println("locate result after deletion: " + result1);*/
		
		//5. query first item and second item
		/*int index3 = 67;
		//long key3 = 293456781; 
		String val3 = "Please call me up when I wake up in the morning.";
		BDHopItem targetBDHopItem = new BDHopItem(key2, val3, k);
		int result3 = lastTable.query(targetBDHopItem, index3);
		System.out.println("query result: " + result3);*/
		
		//6. specific test from real data
		/*int minLineLen = 60;
		int interval = 15;
		//String filename = "testHashTable1";
		String filename = "testClassifier1-fw3-original-100k-a0";
		int n = 1000;
		ArrayList<String> source = Testbed.randomFromFile(filename, n, minLineLen, interval);
		
		int tableSize = 10000, k = 6;  //size of table and bitmap hop
		BDHopTable last = new BDHopTable(tableSize, k);
		int hashSelection = 0;
		int tableNo = 0;  //from 0 to 9
		
		last.testLastTable(source, k, hashSelection, tableNo, tableSize);*/
		
		//7. test delete() using data.
		/*String filename = "testHashTable1";
		
		ArrayList<String> source = new ArrayList<String>();
		BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		String line = "";
		int bound = 89;
				
		while((line = br.readLine())!= null)
		{
			source.add(line);
			bound--;
			if(bound <= 0)
				break;
			//System.out.println(line);
		}
		br.close();
		
		int tableSize1 = 110, k1 = 4;  //size of table and bitmap hop
		BDHopTable last = new BDHopTable(tableSize1, k1);
		int hashSelection = 0;
		int tableNo = 0;  //from 0 to 9
		last.testLastTable(source, k1, hashSelection, tableNo, tableSize1);*/
		
		/*String s1 = "@192.0.0.0/2	162.211.32.2";
		String s2 = "@135.15.0.0/16	0.0.0.0/0";
		long[] hashs = HashFunctions.getHash(hashSelection, s1, tableNo, tableSize1);
		System.out.println("s1 index: " + hashs[1]);
		hashs = HashFunctions.getHash(hashSelection, s2, tableNo, tableSize1);
		System.out.println("s2 index: " + hashs[1]);*/
		
		//7. special data test
		/*String str1 = "@169.201.100.0/22	1";
		String str2 = "@0.0.0.0/0	25.186.237.180";
		String str3 = "@0.0.0.0/0	1.229.63.2";
		String str4 = "@0.0.0.0/1	188.244.113.";
		String str5 = "@0.0.0.0/0	131.57.84";
		String str6 = "@64.0.0.0/2	59.18.176.1";
		String str7 = "@0.0.0.0/0	15.237.202";
		String str8 = "@0.0.0.0/0	20.66.57.216/30";
		long key4 = 7;
		
		int tableSize2 = 15, k2 = 4;
		BDHopTable lastTable2 = new BDHopTable(tableSize2, k2);
		lastTable2.insert(key4, str1, 0, k2, tableSize2);
		lastTable2.insert(key4, str2, 14, k2, tableSize2);
		lastTable2.insert(key4, str3, 14, k2, tableSize2);
		lastTable2.insert(key4, str4, 11, k2, tableSize2);
		lastTable2.insert(key4, str5, 7, k2, tableSize2);
		lastTable2.insert(key4, str6, 0, k2, tableSize2);
		lastTable2.insert(key4, str7, 11, k2, tableSize2);
		lastTable2.insert(key4, str8, 8, k2, tableSize2);
		for(int i = 0; i < lastTable2.getBdTable().size(); i++){
			BDHopItem curBDHopItem = lastTable2.getItem(i);
			if(curBDHopItem != null && !curBDHopItem.isEmpty()){
				System.out.println("last table item " + curBDHopItem.getItem() + " with index: " + i);
				System.out.println("the item's bitmap: " + curBDHopItem.getBitmap());
			}
		}
		System.out.println("table size: " + lastTable2.getBdTable().size());
		System.out.println(lastTable2.getNextItem(11));
		System.out.println(lastTable2.getPrevItem(12));*/
		
		//8. test using random file
		String filename = "testClassifier1-fw3-original-100k-a0";
		int minLineLen = 60;
		int interval = 15;
		int n = 5000;
		int hashSelection = 0;
		int tableNo = 0;  //from 0 to 9
		int tableSize2 = 100000, k2 = 4;  //size of table and bitmap hop
		
		int times = 50;
		ArrayList<ArrayList<Double>> testSet = new ArrayList<ArrayList<Double>>();
		
		for(int i = 0; i < times; i++){
			ArrayList<String> source = TestTableCollection.randomFromFile(filename, n, minLineLen, interval);
			ArrayList<String> noDupSource = TestTableCollection.removeDup(source);
			//System.out.println("duplicate string count: " + (source.size()-noDupSource.size()));
			System.out.println("total item count to be inserted: " + noDupSource.size());
			BDHopTable lastTable2 = new BDHopTable(tableSize2, k2);
			testSet.add(lastTable2.testLastTable(noDupSource, k2, hashSelection, tableNo, tableSize2));
		}
		//System.out.println("count: " + testSet.size());
		System.out.println("now output averages**********************");
		ArrayList<Double> avgResult = CommonUtil.sumStat(3, testSet);
		for(Double avg : avgResult)
			System.out.println(avg);
	}

}
