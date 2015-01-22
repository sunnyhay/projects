package edu.wsu.eecs.nrl;

/*
 * Hash element prototype.
 * Key is defined as integer and value is a Value type which can be extended by subclasses.
 * If an existing item in a hash table collides with a new one, both will be moved to next hash table in 
 * the hierarchy. Their keys will be recalculated by a new hash function. Yet the item keeps its instance 
 * unless it's deleted.
 */
public class Item implements Comparable<Item>{

	private long key;
	private Value value;
	
	public Item(){
		
	}
	
	public Item(long key, Value value){
		this.setKey(key);
		this.setValue(value);
	}
	
	public boolean equals(Item item){
		if(this.key == item.key && this.value.equals(item.getValue()))
			return true;
		return false;
	}
	
	public boolean equals(long key, Value value){
		if(this.key == key && this.value.equals(value))
			return true;
		return false;
	}
	
	public long getKey() {
		return key;
	}

	public void setKey(long key) {
		this.key = key;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	public String toString(){
		return "key: " + key + " with value: " + getValue().getVal();		
	}
	
	public static void main(String [] args){
		Value v = new Value("20");
		int k = 10;
		Item item = new Item(k,v);
		System.out.println("item: <" + item.getKey() + "," + item.getValue().getVal() + ">");
		
		//test equals()
		Item item2 = new Item(k, v);
		System.out.println("equal? " + item2.equals(item));
		
	}

	@Override
	public int compareTo(Item givenItem) {
		return this.getValue().getVal().compareTo(givenItem.getValue().getVal());
	}

}
