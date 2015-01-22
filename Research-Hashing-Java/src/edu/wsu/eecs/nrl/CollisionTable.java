package edu.wsu.eecs.nrl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * contain colliding elements for a specific HTable
 */
public class CollisionTable {
	private HashMap<Integer,ArrayList<Item>> colTable;
	
	public CollisionTable(int size){
		colTable = new HashMap<Integer, ArrayList<Item>>(size);
		for(int i = 0; i < size; i++){
			colTable.put(i, new ArrayList<Item>());
		}
	}
	
	//get a colliding list
	public ArrayList<Item> getList(int index){
		return colTable.get(index);
	}
		
	//get the size of collision table
	public int getSize(){
		int result = 0;
		for(int i = 0; i < colTable.size(); i++){
			ArrayList<Item> curList = colTable.get(i);
			result += curList.size();
		}
		return result;
	}
	
	//check whether an item in the collision table and return true or false.
	public boolean findItem(Item targetItem){
		boolean result = false;
		
		for(Item curItem : getAllItems()){
			if(curItem.equals(targetItem))
				return true;
		}		
		return result;
	}
	
	//get all items in current collision table
	public ArrayList<Item> getAllItems(){
		ArrayList<Item> result = new ArrayList<Item>();
		
		for(int i = 0; i < colTable.size(); i++){
			ArrayList<Item> temp = colTable.get(i);
			if(!temp.isEmpty()){
				result.addAll(temp);
			}
		}
		
		return result;
	}
	//clear current list which contains only one item
	public Item clearList(int index){
		if(isOnlyOneInList(index))
			return colTable.get(index).remove(0);
		return null;
	}
	
	
	//check whether a list contains only one item
	public boolean isOnlyOneInList(int index){
		return colTable.get(index).size() == 1 ? true : false;
	}
	
	//empty list or not for a given position
	public boolean isCurListEmpty(int index){
		return colTable.get(index).isEmpty();
	}
	
	//add a new item colliding in position index
	public void addItem(int index, Item item){
		colTable.get(index).add(item);
	}
	
	//remove a given item from current colliding list
	public boolean removeItem(int index, Item targetItem){
		boolean result = false;
		Item temp = null;
		ArrayList<Item> curList = colTable.get(index);
		for(Item item : curList)
			//here due to different keys, we consider equal as VALUE equivalent.
			if(item.getValue().equals(targetItem.getValue())){
				temp = item;
				result = true;
			}			
		curList.remove(temp);		
		
		return result;
	}
	
	//delete an item 
	public Item getItem(int index){
		if(colTable.get(index).isEmpty())
			return null;
		Item item = colTable.get(index).get(0);
		boolean result = removeItem(index, item);
		if(!result)
			System.out.println("illegal removal!");
		return item;
	}

	public HashMap<Integer, ArrayList<Item>> getColTable() {
		return colTable;
	}

	public void setColTable(HashMap<Integer, ArrayList<Item>> colTable) {
		this.colTable = colTable;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int size = 100;
		CollisionTable colTable = new CollisionTable(size);
		int pos = 10;
		long key = 1010;
		Value value = new Value("99");
		Item item = new Item(key, value);
		colTable.addItem(pos, item);
		
		//0. test isOnlyOneInList()
		System.out.println("only one item? " + colTable.isOnlyOneInList(pos));
		
		//0.1 test clearList()
		/*Item item3 = colTable.clearList(pos);
		System.out.println(item3);
		System.out.println(colTable.isCurListEmpty(pos));*/
		
		//0.2 test findItem()
		System.out.println("find? " + colTable.findItem(new Item(11,new Value("22"))));
		
		/*long key1 = 1020;
		Value value1 = new Value("99");
		Item item1 = new Item(key1, value1);
		
		//1. test add()
		colTable.addItem(pos, item1);
		System.out.println("only one item? " + colTable.isOnlyOneInList(pos));
		
		//1.1 test getAllItems()
		ArrayList<Item> allItems = colTable.getAllItems();
		for(Item curItem : allItems)
			System.out.println("contain item: " + curItem);
		
		//1.2 test getSize()
		System.out.println("table size: " + colTable.getSize());
		
		//2. test remove()
		if(!colTable.removeItem(pos, item))
			System.out.println("removal fails");;
		
		//3. test get()
		Item item2 = colTable.getItem(pos);		
		//System.out.println("item num: " + colTable.getColTable().get(pos).size());
		System.out.println("new item: " + item2);
		
		//4. test empty()
		System.out.println("empty? " + colTable.isCurListEmpty(pos));*/
		
	}

}
