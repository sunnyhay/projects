package edu.wsu.eecs.nrl;

import java.util.BitSet;
import java.util.HashMap;
/*
 * implementation of (k-1) hash tables. each with a bitmap for summary filter. 
 */
public class HTable {
	private HashMap<Integer,Item> htable;
	private BitSet bitmap;
	
	public HTable(int size){
		htable = new HashMap<Integer, Item>(size);  //set fixed size for the table
		for(int i = 0; i < size; i++)
			htable.put(i, null);
		//System.out.println("table size: " + htable.size());
		bitmap = new BitSet(size);  //set corresponding bitmap for the table
	}
	
	//get current capacity of the table, i.e. amount of items
	public int getCapacity(){
		int result = 0;
		
		for(int i = 0; i < htable.size(); i++){
			Item curItem = htable.get(i);
			if(curItem != null && bitmap.get(i))
				result++;
		}		
		return result;
	}
	
	/**
	 * only check the table's bitmap for quick membership query
	 */
	public boolean bitmapCheck(int index){
		return bitmap.get(index);
	}
	
	/**
	 * query the table using a given item. return values: 0, 1 or 2.
	 * 0: empty bucket. 1: item matched in both bitmap position and key. 
	 * 2: bitmap match but key not, i.e. false positive  
	 */
	public int query(Item targetItem, int index){
		Item curItem = htable.get(index);
		/*if(curItem != null){
			System.out.println("current index: " + index);
			System.out.println("current item: " + curItem.toString());
		}*/			
		
		if(!bitmap.get(index) || curItem == null)
			return 0;
		if((curItem.getKey() == targetItem.getKey()) 
				&& (curItem.getValue().equals(targetItem.getValue())))
			return 1;
		else
			return 2;
	}
	
	/*
	 * remove an item from current table, then return it.
	 * removal means setting its bitmap position to 0 if it is not null
	 */
	public Item removeItem(int index){
		Item item = htable.get(index);
		if(item == null)
			return null;
		bitmap.clear(index);		
		return item;
	}
	
	/*
	 * the method is called only when query return 0 or 1.
	 */
	public void insert(Item item, int index){
		bitmap.set(index);
		htable.put(index, item);
	}
	
	/*
	 * set some bit in its bitmap
	 */
	public void setBit(int bitPos){
		bitmap.set(bitPos);
	}
	
	/*
	 * clear some bit in its bitmap
	 */
	public void clearBit(int bitPos){
		bitmap.clear(bitPos);
	}
	public HashMap<Integer,Item> getHtable() {
		return htable;
	}

	public BitSet getBitmap() {
		return bitmap;
	}

	public void setBitmap(BitSet bitmap) {
		this.bitmap = bitmap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int capacity = 1000;
		HTable table = new HTable(capacity);
		int pos = 10;
		long key = 1010;
		Value value = new Value("99");
		Item item = new Item(key, value);
		
		//1. test insert() and getCapacity()
		table.insert(item, pos);
		table.insert(item, pos+3);
		table.insert(item, pos+2);
		table.insert(item, pos+1);
		System.out.println("current table's capacity: " + table.getCapacity());
		
		//2. test query()
		long key1 = 1020;
		Value value1 = new Value("99");
		
		Item item1 = new Item(key1, value1);
		System.out.println("table query for item: " + table.query(item, pos));
		System.out.println("table query for item1: " + table.query(item1, pos+3));
		
		//2.1 still test query()
		long key2 = 1503426002;
		Value value2 = new Value("@0.0.0.0/0	8.81.225");
		Item item2 = new Item(key2, value2);
		
		table.insert(item2, 50);
		System.out.println("new query result: " + table.query(item2, 50));
		
		/*//3. test remove()
		Item r = table.removeItem(pos);
		System.out.println("remove item with key: " + r.getKey());
		System.out.println("new table query for item: " + table.query(item, pos));
		
		//4. test insert() after remove()
		table.insert(item, pos);
		System.out.println("new new table query for item: " + table.query(item, pos));*/
	}

}
