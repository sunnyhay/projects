package edu.wsu.eecs.nrl;

import java.util.BitSet;

/*
 * BDHopItem is the basic element in Bidirection Hash table, the last hash table in the hierarchy.
 * 'bitmap' is the embedded bitmap within each BDHopItem to indicate the previous and next hop 
 * information. Then bitmap has (2k+3) bits in the form of 1-1-1-k-k.
 * So the nexthop scope to be expressed by the bitmap is 2^k, e.g. 16 when k = 4.
 * In each k bits, the first bit indicates the direction of searching or relative direction, 1 forwards
 * and 0 backwards and the remaining (k-1) encodes absolute nexthop distance.
 * Take an example. when k = 4, the first bit is set, the remaining (k-1) bits are 010 (2). That means 
 * from current item's position its colliding item is 3 from its forward direction. If the first bit is 
 * clear and (k-1) bits are 111, its colliding item is -8 from its backward direction.    
 * 
 * The first bit indicates whether or not this is an empty bucket. 
 * 0: empty 1: occupied
 * The second bit indicates whether there is a colliding element next. 
 * 0: available since no such colliding element, 1: not available
 * The third bit indicates whether the item itself is a colliding element for a previous one.
 * 0: no previous, 1: have previous 
 * If the first bit is clear, other bits must be all clear since deletion operation pushes colliding 
 * elements back to original positions. 
 * If the second bit is set, the first k bits indicate the position of next colliding element. 
 * If the third bit is set, the next k bits indicate the position of the item’s original place.
 */
public class BDHopItem {
	private Item item;
	private BitSet bitmap;
	
	public BDHopItem(long key, String val, int k){
		this.setItem(new Item(key, new Value(val)));
		this.setBitmap(new BitSet(2*k+3));
	}
	
	public BDHopItem(Item item, int k){
		this.setItem(item);
		this.setBitmap(new BitSet(2*k+3));
	}
	
	/* --------------------------------------------------
	 * a series of methods for bitmap operations
	 */
	
	//check whether the bucket is empty.
	public boolean isEmpty(){
		if(item == null || bitmap == null)
			return true;
		return !bitmap.get(0);
	}
	
	//check whether a colliding item exists
	public boolean hasNext(){
		if(item == null || bitmap == null)
			return false;
		return bitmap.get(1);
	}
	
	//check whether current item is a colliding item to its previous
	public boolean hasPrev(){
		if(item == null || bitmap == null)
			return false;
		return bitmap.get(2);
	}
	
	//set bitmap's first three bits to indicate (0) not empty; (1) has colliding item; (2) has previous.
	public void setBitmapPosition(int pos){
		bitmap.set(pos);
	}
	
	//clear the item
	public void clearOccupied(){
		bitmap.clear(0);		
	}
	
	//clear next pointer
	public void clearNext(int k){
		bitmap.clear(1);
		bitmap.clear(3, k+2);
	}
	
	//clear prev pointer
	public void clearPrev(int k){
		bitmap.clear(2);
		bitmap.clear(k+3, 2*k+2);
	}
	
	//set bitmap's k-bit block for either next or previous
	//input 'pos' must fall in range [-2^(k-1),-1] union [1, 2^(k-1)].
	//input isNext is true if set the first k-bit block; otherwise set the second k-bit block for previous.
	//if isNext is true: pos = nextIndex - currentIndex suppose nextIndex is index of item to be inserted
	//if isNext is false: pos = currentIndex - nextIndex
	public void setBitmapBlock(int k, int pos, boolean isNext){
		int begin = 3;  //indicate where to put 'pos', decided by 'isNext'. initial 3 for next.
		if(!isNext){
			begin += k;
		}
		
		int absPos = pos;
		if(pos > 0)
			bitmap.set(begin);  //set the first bit of k-bit block in case of forward direction
		else
			absPos = -pos;  //
		begin++;  
		absPos--;  //now get the absolute position to be encoded into bitmap.
		
		//encode in bitmap.
		while(absPos > 0){
			if((absPos % 2) > 0)
				bitmap.set(begin);
			begin++;
			absPos /= 2;
		}
	}
	
	//reverse method of setBitmapBlock() to retrieve relative distance of next or previous from current
	public int getPosFromBitmapBlock(int k, boolean isNext){
		int result = 0;
		int begin = 3;  //indicate where to begin counting. initial 3 for next.
		if(!isNext){
			begin += k;
		}
		
		int absPos = 0;
		int accu = 1;
		boolean sign = bitmap.get(begin) ? true : false;  //true forward, false backward
		begin++;
		for(int i = 0; i < k-1; i++){
			if(bitmap.get(begin+i))
				absPos += accu;
			accu *= 2;
		}
		
		if(sign)
			result = absPos+1;
		else
			result = -(absPos+1);
		
		return result;
	}
			
	/* end of bitmap methods.
	 * --------------------------------------------------
	 */
	
	public boolean equals(BDHopItem targetBdhitem){
		if(this.item.equals(targetBdhitem.getItem()))
			return true;
		return false;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public BitSet getBitmap() {
		return bitmap;
	}

	public void setBitmap(BitSet bitmap) {
		this.bitmap = bitmap;
	}
	
	public String toString(){
		return item.toString() + " with bitmap: " + bitmap.toString(); 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long key = 10;
		String val = "I love my family";
		int k = 4;  //2^4=16 
		BDHopItem bdhItem = new BDHopItem(key, val, k);
		//System.out.println("bitmap size: " + bdhItem.bitmap.length());
		
		boolean isNext = true;
		int pos = 3;  //fall within [-8,-1] or [1,8] according to k
		bdhItem.setBitmapPosition(1);  //set next bit
		bdhItem.setBitmapBlock(k, pos, isNext);
		System.out.println("after setting next bitmap: " + bdhItem.getBitmap().toString());
		
		int pos1 = -5;  
		bdhItem.setBitmapPosition(2);  //set prev bit
		bdhItem.setBitmapBlock(k, pos1, false);
		System.out.println("after setting prev bitmap: " + bdhItem.getBitmap().toString());
		
		//System.out.println(pos >> 1);
		
		System.out.println("next pos from bitmap: " + bdhItem.getPosFromBitmapBlock(k, isNext));
		System.out.println("prev pos from bitmap: " + bdhItem.getPosFromBitmapBlock(k, false));
		
		//test equals()
		BDHopItem target = new BDHopItem(key, val, k);
		System.out.println(target.equals(bdhItem));
		
		//test clearNext()
		bdhItem.clearNext(k);
		System.out.println("after clearing next bitmap: " + bdhItem.getBitmap().toString());
		
		//test clearPrev()
		bdhItem.clearPrev(k);
		System.out.println("after clearing prev bitmap: " + bdhItem.getBitmap().toString());
		
		System.out.println((-13)%15);
		
	}

}
