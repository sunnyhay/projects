package subtraction;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

//common methods for our subtraction scheme to reduce TCAM entries

public class CommonMethods {
	public static ArrayList<Integer> getGTRanges(int k){
		//return list with all ranges such as [2tok,65535] to [2to(k+1)-1, 65535]
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		
		for(int i = (int)Math.pow(2, k); i < (int)Math.pow(2, k+1); i++){
			ranges.add(i);
			ranges.add(65535);
		}
		
		return ranges;
	}
	public static ArrayList<Integer> getRanRanges(int k, int n){
		//return list with all random generated ranges
		//k is used to control the span, k from 1 to 15.
		//the start of a range is randomly selected from [0, 65535-2to(k+1)+1]
		//the span is randomly chosen from [2tok, 2to(k+1)-1]
		//so a range is [start,start+span]
		//n is the number of random ranges to be generated
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		int start,span;   //start point and span
		
		Random r = new Random();
		System.out.println("begin start generation");
		for(int i = 0; i < n; i++){
			start = r.nextInt((int)Math.pow(2,k));
			span = (int)Math.pow(2, k-1) + r.nextInt((int)Math.pow(2, k-1));
			System.out.println("start: " + start + " end: " + (start+span));
			ranges.add(start);
			ranges.add(start+span);
		}
		
		return ranges;
	}
	public static BitSet getBin(int val){
		//turn the val into a BitSet with its binary form.
		BitSet b = new BitSet();
		
		int current = val, position = 0;
		while(current > 0){
			if(current % 2 != 0)
				b.set(position);
			position++;
			current /= 2;
		}
		
		return b;
	}
		
	public static ArrayList<Integer> getPrefixs(int low, int high){
		//using ordinary prefix expansion scheme here
		//The ArrayList contains items with odd ones for starts, even ones for ends.
		//if even one is -1, that means the odd one is individual prefix instead of a prefix pair
		//ArrayList.size/2 is the prefix number for ordinary expansion scheme.
		//low>0, 32768>h>l. If any of low or high > 32768, the range will be divided for sub-ranges
		ArrayList<Integer> a = new ArrayList<Integer>();
		int current = low;
		
		if(low == high){//or low == high, just one prefix
			a.add(current);
			a.add(-1);
			return a;
		}
		
		if(current%2 != 0){//low is odd and so there is a single prefix for it.
			a.add(current);
			a.add(-1);
			current++;
		}		
		if(current == high){//high == low+1
			//System.out.println("problem here!");
			//System.out.println("getPrefixes()'s low: " + low + " high: " + high);
			a.set(a.size()-1, high);
			return a;
		}
		
		while(current < high){
			BitSet cb = getBin(current);  //b is the binary form of current.
			int temp = current;
			int position = 0;
			
			while(position < 16){
				temp += Math.pow(2, position);
				if(temp > high){//overflow
					a.add(current);
					a.add((int) (temp-Math.pow(2, position)));
					current = (int) (temp-Math.pow(2, position)) + 1;
					break;
				}
				position++;
				if(cb.get(position)){//position with 1
					a.add(current);
					a.add(temp);
					current = temp + 1;
					break;
				}
			}
		}
		//check whether current is equal to high
		if(current == high){//must be even
			a.add(current);
			a.add(-1);
		}
		
		return a;
	}
	
	public static ArrayList<Integer> getHalfEntryNum(int low, int high){
		//System.out.println("getHalfEntryNum()'s low: " + low + " high:" + high);
		//only deal with low>=0 and high < 32768
		//invoked by getEntryNum() to deal with three cases there.
		//return 2 values: first one for the ordinary range expansion; second one for our scheme
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> boundaries = getBoundaries(low, high);
		ArrayList<Integer> prefixes;  //the prefixes from ordinary range expansion scheme
		
		if(!boundaries.isEmpty() && (boundaries.get(0) >= boundaries.get(1))){//boundaries in the same 2tok
			//for test, output all prefixes here.
			prefixes = getPrefixs(low, high);
			
			/*for(int i = 0; i < prefixes.size(); i=i+2){
				System.out.print("prefix start: " + prefixes.get(i) + " ");
				if(prefixes.get(i+1) != -1)
					System.out.print("prefix end: " + prefixes.get(i+1));
				System.out.println();
			}
			System.out.println("no benefit from the same 2tok range");*/
			//for this case, no benefit from our scheme
			result.add(prefixes.size()/2);
			result.add(prefixes.size()/2);
		}else{//worth using our scheme. that means benefit of our scheme by n-m>=1
			if(boundaries.get(0) == -1){//low == 0.
				//the range is the only range without any benefit from our scheme
				//for test, output all prefixes here.
				prefixes = getPrefixs(low, high);
				
				/*for(int i = 0; i < prefixes.size(); i=i+2){
					System.out.print("prefix start: " + prefixes.get(i) + " ");
					if(prefixes.get(i+1) != -1)
						System.out.print("prefix end: " + prefixes.get(i+1));
					System.out.println();
				}
				System.out.println("no benefit from low == 0 or 32768");*/
				//for this case, no benefit from our scheme
				result.add(prefixes.size()/2);
				result.add(prefixes.size()/2);
			}else{//low != 0 and n-m>=1
				//at most three sub-ranges
				if(low == (int)(Math.pow(2, boundaries.get(0)))){//low == 2tom
					if(high == (int)(Math.pow(2, boundaries.get(1))-1)){//high == 2ton-1
						//only one sub-range.
						//for test, output all prefixes here.						
						prefixes = getPrefixs(low, high);
						
						/*for(int i = 0; i < prefixes.size(); i=i+2){
							System.out.print("prefix start: " + prefixes.get(i) + " ");
							if(prefixes.get(i+1) != -1)
								System.out.print("prefix end: " + prefixes.get(i+1));
							System.out.println();
						}
						System.out.println("greatest benefit: " + (boundaries.get(1)-boundaries.get(0)-1));*/
						//for this case, greatest benefit from our scheme.
						result.add(prefixes.size()/2);
						result.add(1);  //only one entry
					}else{
						//two sub-ranges, [low, 2ton-1] and [2ton, high].
						//benefit comes from the first one.
						//for test, output all prefixes here. 
						//System.out.println("I think here!!!"); 
						prefixes = getPrefixs((int)(Math.pow(2, boundaries.get(1))), high);
						
						/*for(int i = 0; i < prefixes.size(); i=i+2){
							System.out.print("prefix start: " + prefixes.get(i) + " ");
							if(prefixes.get(i+1) != -1)
								System.out.print("prefix end: " + prefixes.get(i+1));
							System.out.println();
						}
						System.out.println("benefit low=2tom, high!=2ton-1: " + (boundaries.get(1)-boundaries.get(0)-1));*/
						//combine together
						result.add(prefixes.size()/2 + (boundaries.get(1)-boundaries.get(0)));
						result.add(prefixes.size()/2 + 1);
					}
				}else{
					if(high == (int)(Math.pow(2, boundaries.get(1))-1)){//high == 2ton-1
						//two sub-ranges, [low, 2tom-1], [2tom, high]
						//benefit comes from the second one.
						//for test, output all prefixes here. 
						prefixes = getPrefixs(low, (int)(Math.pow(2, boundaries.get(0))-1));
						
						/*for(int i = 0; i < prefixes.size(); i=i+2){
							System.out.print("prefix start: " + prefixes.get(i) + " ");
							if(prefixes.get(i+1) != -1)
								System.out.print("prefix end: " + prefixes.get(i+1));
							System.out.println();
						}
						System.out.println("benefit low!=2tom, high=2ton-1: " + (boundaries.get(1)-boundaries.get(0)-1));*/
						//combine together 
						result.add(prefixes.size()/2 + (boundaries.get(1)-boundaries.get(0)));
						result.add(prefixes.size()/2 + 1);							
					}else{
						//three ranges, [low,2tom-1], [2tom, 2ton-1] and [2ton,high]
						//benefit comes from the second one.
						//for test, output all prefixes for the first range here. 
						prefixes = getPrefixs(low, (int)(Math.pow(2, boundaries.get(0))-1));
						
						/*for(int i = 0; i < prefixes.size(); i=i+2){
							System.out.print("first prefix start: " + prefixes.get(i) + " ");
							if(prefixes.get(i+1) != -1)
								System.out.print("first prefix end: " + prefixes.get(i+1));
							System.out.println();
						}
						System.out.println("benefit three sub-ranges: " + (boundaries.get(1)-boundaries.get(0)-1));*/
						//count the first sub-range 
						int temp = prefixes.size()/2;  
						
						//last sub-range
						//for test, output all prefixes here. 
						prefixes = getPrefixs((int)(Math.pow(2, boundaries.get(1))), high);
						
						/*for(int i = 0; i < prefixes.size(); i=i+2){
							System.out.print("second prefix start: " + prefixes.get(i) + " ");
							if(prefixes.get(i+1) != -1)
								System.out.print("second prefix end: " + prefixes.get(i+1));
							System.out.println();
						}*/
						//combine together
						temp += prefixes.size()/2;
						result.add(temp + (boundaries.get(1)-boundaries.get(0)));
						result.add(temp + 1);
					}
				}
			}
		}
		
		return result;
	}
	
	public static ArrayList<Integer> getEntryNum(int low, int high){
		//divide the range [low,high] into three sub-ranges and count the entry number using our scheme.
		//the condition of our scheme's benefit is: n-m>=1. n and m are defined in method getBoundaries().
		ArrayList<Integer> part;   //result from getHalfEntryNum().
		ArrayList<Integer> result;  //return the total entry number of both ordinary scheme and our scheme.
				
		if(low < 32768 && high < 32768){//exactly call getHalfEntryNum().
			result = getHalfEntryNum(low, high);	
		}else if(low < 32768 && high >= 32768){
			result = new ArrayList<Integer>();
			if(low == 0 && high == 32768){
				//two prefixes.
				result.add(2);
				result.add(2);
			}else if(low == 0 && high != 32768){
				//two sub-ranges [0, 32767] and [32768,high]
				part = getHalfEntryNum(0, high-32768);
				result.add(part.get(0)+1);
				result.add(part.get(1)+1);
			}else if(low != 0 && high == 32768){
				//two sub-ranges [low, 32767] and 32768
				part = getHalfEntryNum(low, 32767);
				result.add(part.get(0)+1);
				result.add(part.get(1)+1);
			}else{//low != 0 && high != 32768
				//two sub-ranges [low, 32767] and [32768, high].
				part = getHalfEntryNum(low, 32767);
				int t1 = part.get(0), t2 = part.get(1);
				part = getHalfEntryNum(0, high-32768);
				result.add(t1+part.get(0));
				result.add(t2+part.get(1));
			}
		}else{//low >= 32768 and high > 32768
			result = getHalfEntryNum(low-32768, high-32768);
		}
				
		return result;
	}

	public static ArrayList<Integer> getBoundaries(int low, int high){
		//given a range [low,high] return three sub-ranges of [low,2tom-1],  [2tom,2ton-1], [2ton,high]
		//m may equal n; 2tom-1 >= low; 2ton <= high; 
		//for the returned ArrayList, its capability must be 2, corresponding to m, n
		//three cases: low and high < 32768(2to15); low < 2to15 and high > 2to15; low and high > 2to15.
		//we have to deal with low==0 or 32768 special cases.
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		int m = 0, n = 0;
		
		if(low < 32768 && high < 32768){
			for(int i = 0; i < 16; i++)
				if(Math.pow(2, i) >= low){
					m = i;
					break;
				}
			for(int j = 15; j >= 0; j--)
				if(Math.pow(2, j)-1 <= high){
					n = j;
					break;
				}
			if(low == 0)
				results.add(-1);
			else
				results.add(m);
			results.add(n);
		}else if(low < 32768 && high >= 32768){
			for(int i = 0; i < 16; i++)
				if(Math.pow(2, i) >= low){
					m = i;
					break;
				}
			if(low == 0)
				results.add(-1);
			else
				results.add(m);
			results.add(15);
		}else{//low >= 32768 and high > 32768
			for(int i = 0; i < 16; i++)
				if(Math.pow(2, i) >= (low-32768)){
					m = i;
					break;
				}
			for(int j = 15; j >= 0; j--)
				if(Math.pow(2, j)-1 <= (high-32768)){
					n = j;
					break;
				}	
			if(low == 32768)
				results.add(-1);
			else
				results.add(m);
			results.add(n);
		}
		
		return results;
	}	
	
	public static void main(String[] args) {
		//test getBoundaries().
		//first case
		int low = 1, high = 2;
		ArrayList<Integer> results = getBoundaries(low, high);
		System.out.println("first: " + results.get(0) + " with high: " + results.get(1));
		//second case
		//low = 63; 
		//high = 40000;
		//results = getBoundaries(low, high);
		//System.out.println("first: " + results.get(0) + " with high: " + results.get(1));
		//last case
		//low = 36663; 
		//high = 58000;
		//results = getBoundaries(low, high);
		//System.out.println("first: " + results.get(0) + " with high: " + results.get(1));
		
		//test getBin().
		//int val = 13;
		//BitSet b = getBin(val);
		//System.out.println(b);
		//System.out.println(b.get(1));
		
		//test getPrefixes().
		//low = 64;
		//high = 1023;
		//ArrayList<Integer> a = getPrefixs(low, high);
		//System.out.println("prefix num: " + a.size());
		//for(int i = 0; i < a.size(); i++)
			//System.out.println(a.get(i));
		
		//test getEntryNum().
		//low = 71;
		//high = 99;
		//System.out.println("entry num: " + getEntryNum(low, high));
		
		//test getHalfEntryNum().
		//low = 77;
		//high = 18293;
		//ArrayList<Integer> outcome = getHalfEntryNum(low, high);
		//System.out.println("half test. ordinary entry num: " + outcome.get(0));
		//System.out.println("half test. our scheme entry num: " + outcome.get(1));
		
		//test getEntryNum().
		low = 32768+9;
		high = 32768+16389;
		ArrayList<Integer> outcome1 = getEntryNum(low, high);
		System.out.println("test. ordinary entry num: " + outcome1.get(0));
		System.out.println("test. our scheme entry num: " + outcome1.get(1));
		
		//test getRanRanges().
		/*int k = 4, n = 1000;
		ArrayList<Integer> outcome2 = getRanRanges(k, n);
		for(int i = 0; i < outcome2.size(); i+=2){
			System.out.println("start: " + outcome2.get(i) + " end: " + outcome2.get(i+1));
		}*/
		
		//test getGTRanges().
		int k = 4;
		ArrayList<Integer> outcome3 = getGTRanges(k);
		for(int i = 0; i < outcome3.size(); i+=2){
			System.out.println("start: " + outcome3.get(i) + " end: " + outcome3.get(i+1));
		}
	}

}
