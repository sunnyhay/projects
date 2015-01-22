package edu.wsu.eecs.nrl;
/*
 * capsulated item with its tableNo and index
 * used in TableCollection.remove().
 */
public class CapItem {
	private Item item;
	private int tableNo;
	private int index;
	
	public CapItem(Item item, int tableNo, int index){
		this.item = item;
		this.tableNo = tableNo;
		this.index = index;
	}
	
	public boolean equals(CapItem targetItem){
		if(targetItem.getItem().getValue().equals(item.getValue()))
			return true;
		return false;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getTableNo() {
		return tableNo;
	}

	public void setTableNo(int tableNo) {
		this.tableNo = tableNo;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
