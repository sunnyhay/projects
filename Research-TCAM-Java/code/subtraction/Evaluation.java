package subtraction;

import java.io.IOException;
import java.util.ArrayList;

public class Evaluation {
	public static void evalGTRanges(int k){
		//get the comparison for each greater than ranges from [2tok,65535] to [2to(k+1)-1, 65535]
		ArrayList<Integer> outcome = CommonMethods.getGTRanges(k);
		ArrayList<Integer> temp;  //entries from ordinary and our scheme
		long ord = 0, ours = 0; 
		
		System.out.println("begin the great than range calculation");
		for(int i = 0; i < outcome.size(); i+=2){
			temp = CommonMethods.getEntryNum(outcome.get(i), (int)outcome.get(i+1));
			ord += temp.get(0);
			ours += temp.get(1);
		}
		System.out.println("*********All great than Range Generation Result");
		System.out.println("ordinary scheme: " + ord);
		System.out.println("our scheme: " + ours);
	}
	public static void evalRanRanges(int k, int n){
		//get the comparison for each pair of k and n.
		ArrayList<Integer> outcome = CommonMethods.getRanRanges(k, n);
		ArrayList<Integer> temp;  //entries from ordinary and our scheme
		long ord = 0, ours = 0; 
		
		System.out.println("begin the random range calculation");
		for(int i = 0; i < outcome.size(); i+=2){
			temp = CommonMethods.getEntryNum(outcome.get(i), (int)outcome.get(i+1));
			ord += temp.get(0);
			ours += temp.get(1);
		}
		System.out.println("*********All random Range Generation Result");
		System.out.println("ordinary scheme: " + ord);
		System.out.println("our scheme: " + ours);
	}
	
	public static void evalAllRanges(int k){
		//return every possible range generation from k=2 to k=16
		ArrayList<Integer> temp;  //entries from ordinary and our scheme
		long ord = 0, ours = 0;  //entry counts for ordinary and our scheme
				
		int max = (int) Math.pow(2, k) - 1;
		 System.out.println("begin the calculation...");
		for (int i = 0; i < max; i++) {
			for (int j = i + 1; j <= max; j++) {
				temp = CommonMethods.getEntryNum(i, j);
				ord += temp.get(0);
				ours += temp.get(1);
			}
		}
		System.out.println("*********All Range Generation Result");
		System.out.println("ordinary scheme: " + ord);
		System.out.println("our scheme: " + ours);
	}
	
	public static void evalAll(String filename, ArrayList<Long> stat) throws IOException{
		//this filename is "exactPrefix.txt" for stat info of that file.
		//this arraylist is returned from evalOnlyRanges() with six elements corresponding to
		//ordinary src, our scheme src, ordinary dst, our scheme dst, total ordinary, total our scheme.
		int count = FileOperation.norangeStat(filename);   //the common value for exact and (0,65535) entries
		
		System.out.println("src ordinaryAll: " + (stat.get(0)+count) + " oursAll: " + (stat.get(1)+count) 
				+ " and benefit%: " + (double)(stat.get(0)-stat.get(1))/(stat.get(0)+count));
		System.out.println("dst ordinaryAll: " + (stat.get(2)+count) + " oursAll: " + (stat.get(3)+count)
				 + " and benefit%: " + (double)(stat.get(2)-stat.get(3))/(stat.get(2)+count));
		System.out.println("both ordinaryAll: " + (stat.get(4)+count) + " oursAll: " + (stat.get(5)+count)
				 + " and benefit%: " + (double)(stat.get(4)-stat.get(5))/(stat.get(4)+count));
		//System.out.println("exact or * entry num: " + count);
	}
	
	public static ArrayList<Long> evalOnlyRanges(String filename) throws IOException{
		//evaluate the ranges.
		ArrayList<Long> stat = new ArrayList<Long>();  //stat info
		ArrayList<ArrayList<Integer>> results = FileOperation.stat(filename);
		ArrayList<Integer> src = results.get(0);
		ArrayList<Integer> dst = results.get(1);
		long singleSrc1 = 0, singleDst1 = 0, total1 = 0;  //three counts for ordinary scheme.
		long singleSrc2 = 0, singleDst2 = 0, total2 = 0;  //three counts for our scheme.
		
		for(int i = 0; i < src.size(); i+=2){
			singleSrc1 += src.get(i);
			singleSrc2 += src.get(i+1);
			singleDst1 += dst.get(i);
			singleDst2 += dst.get(i+1);
			total1 += src.get(i)*dst.get(i);
			total2 += src.get(i+1)*dst.get(i+1);
		}
		
		System.out.println("src ordinary: " + singleSrc1 + " ours: " + singleSrc2 
				+ " and benefit%: " + (double)(singleSrc1-singleSrc2)/singleSrc1);
		System.out.println("dst ordinary: " + singleDst1 + " ours: " + singleDst2
				 + " and benefit%: " + (double)(singleDst1-singleDst2)/singleDst1);
		System.out.println("both ordinary: " + total1 + " ours: " + total2
				 + " and benefit%: " + (double)(total1-total2)/total1);
		stat.add(singleSrc1);
		stat.add(singleSrc2);
		stat.add(singleDst1);
		stat.add(singleDst2);
		stat.add(total1);
		stat.add(total2);
		return stat;
	}
	public static void main(String[] args) throws IOException {
		/*String name = "testClassifier1-ipc2-original-300k-a0";
		FileOperation.parseClassifier(name);
		String filename = "range.txt";
		ArrayList<Long> result = evalOnlyRanges(filename);
		System.out.println();
		String filename1 = "exactPrefix.txt";
		evalAll(filename1, result);*/
		/*int k = 15;
		evalAllRanges(k);*/
		/*int k = 15, n = 10000;
		evalRanRanges(k, n);*/
		/*for(int i = 0; i < 16; i++){
			System.out.println("k=" + i + " result.");
			evalGTRanges(i);
		}*/
		int k = 14;
		evalGTRanges(k);
	}

}
