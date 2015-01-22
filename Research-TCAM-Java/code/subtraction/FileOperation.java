package subtraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileOperation {
	/*public static void generateLarge(int k) throws IOException{
		//return every possible range within 2tok, 16>=k>12.
		//the result is written in file accordingly.
		String file1 = "largeRanges.txt";
		BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1)));
		
		int max = (int) Math.pow(2, k) - 1;
		// System.out.println(max);
		for (int i = 0; i < max; i++) {
			for (int j = i + 1; j <= max; j++) {
				bw1.append(i + " " + j + "\n");
			}
		}
		bw1.close();
		System.out.println("all generation over.");
	}*/
	
	public static void parseClassifier(String filename) throws IOException{
		//parse the real classifier and create two files.
		//file1: no range in both src/dst ports. only exact or prefix (0,65535) pairs
		//file2: at least one range, one of the src/dst ports may be exact or prefix (0,65535) pairs.
		//define file1's cases: (1) both exact match; (2)one exact match, the other (0,65535); 
		//(3) both (0,65535).
		
		BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		String file1 = "exactPrefix.txt";
		BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file1)));
		String file2 = "range.txt";
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
		String line = "";
		int[] srcPorts = new int[2];
		int[] dstPorts = new int[2];
		System.out.println("start parse...");
		while((line = br.readLine())!= null)
		{
			//System.out.println(line);
			String[] t = line.split("	");
			/*for(String s: t)
				System.out.print(s + " ");
			System.out.println();*/
			
			String[] s = t[2].split(" ");
			srcPorts[0] = Integer.parseInt(s[0]);
			srcPorts[1] = Integer.parseInt(s[2]);
			s = t[3].split(" ");
			dstPorts[0] = Integer.parseInt(s[0]);
			dstPorts[1] = Integer.parseInt(s[2]);
			/*System.out.print(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1]);
			System.out.println();*/
			
			//separate two cases and store into different files.
			if(srcPorts[0] == srcPorts[1] && dstPorts[0] == dstPorts[1]){
				//file1 first case
				bw1.append(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1] + "\n");
			}else if(srcPorts[0] == srcPorts[1] && dstPorts[0] == 0 && dstPorts[1] == 65535){
				//file1 second case
				bw1.append(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1] + "\n");
			}else if(dstPorts[0] == dstPorts[1] && srcPorts[0] == 0 && srcPorts[1] == 65535){
				//file1 second case
				bw1.append(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1] + "\n");
			}else if(srcPorts[0] == 0 && srcPorts[1] == 65535 && dstPorts[0] == 0 && dstPorts[1] == 65535){
				//file1 third case
				bw1.append(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1] + "\n");
			}else{//file2
				bw2.append(srcPorts[0] + " " + srcPorts[1] + " " + dstPorts[0] + " " + dstPorts[1] + "\n");
			}
		}
		br.close();
		bw1.close();
		bw2.close();
		System.out.println("end parse...");
	}
	
	public static int norangeStat(String filename) throws IOException{
		//return the stat result from exactPrefix.txt which only contains one-entry for each line
		//simply count the line amount of exactPrefix.txt
		int count = 0;
		
		BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		String line = "";
		System.out.println("start exact&prefix match file stat...");
		while((line = br.readLine())!= null)
		{
			line.charAt(0);  //nonsense
			count++;						
		}
		br.close();
		System.out.println("end exact&prefix match file stat...");
		System.out.println("exact&prefix num for both ports: " + count);
		
		return count;
	}
	
	public static ArrayList<ArrayList<Integer>> stat(String filename) throws IOException{
		//line format: ** (src port start) ** (src port end) ** (dst start) ** (dst end)
		//return a list containing two sub-lists with source/destination port statistic
		//even items inside are from ordinary range expansion and odd for our scheme.
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> src = new ArrayList<Integer>();  //hold the range expansion results in source port
		ArrayList<Integer> dest = new ArrayList<Integer>();  //hold the range expansion results in destination port
		ArrayList<Integer> temp1;
		ArrayList<Integer> temp2;
		int count = 0;  //counter for the total range number
		int countSingleSrc = 0, countSingleDst = 0;  //single exact-prefix src ports and dst ports. 
		
		BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream(filename)));
		String line = "";
		System.out.println("start stat...");
		while((line = br.readLine())!= null)
		{
			count++;
			String[] t = line.split(" ");
			/*for(String s: t)
				System.out.print(s + " ");
			System.out.println();*/
			
			int[] ranges = new int[t.length];
			for(int i = 0; i < ranges.length; i++)
				ranges[i] = Integer.parseInt(t[i]);
			
			/*for(int i: ranges)
				System.out.print(i + " ");
			System.out.println();*/
			
			//define file2's cases: 
			//(1) exact src && dst range; (2) (0,65535) src && dst range;
			//(3) src range && exact dst; (4) src range && (0,65535) dst;
			//(5) src range && dst range.
			if(ranges[0] == ranges[1]){//case 1
				src.add(1);
				src.add(1);
				temp2 = CommonMethods.getEntryNum(ranges[2], ranges[3]);
				dest.addAll(temp2);	
				countSingleSrc++;
			}else if(ranges[0] == 0 && ranges[1] == 65535){//case 2
				src.add(1);
				src.add(1);
				temp2 = CommonMethods.getEntryNum(ranges[2], ranges[3]);
				dest.addAll(temp2);
				countSingleSrc++;
			}else if(ranges[2] == ranges[3]){//case 3
				dest.add(1);
				dest.add(1);
				temp1 = CommonMethods.getEntryNum(ranges[0], ranges[1]);
				src.addAll(temp1);
				countSingleDst++;
			}else if(ranges[2] == 0 && ranges[3] == 65535){//case 4
				dest.add(1);
				dest.add(1);
				temp1 = CommonMethods.getEntryNum(ranges[0], ranges[1]);
				src.addAll(temp1);
				countSingleDst++;
			}else{//double ranges, case 5
				temp1 = CommonMethods.getEntryNum(ranges[0], ranges[1]);
				temp2 = CommonMethods.getEntryNum(ranges[2], ranges[3]);
				src.addAll(temp1);
				dest.addAll(temp2);
			}			
		}
		br.close();
		System.out.println("end stat...");
		System.out.println("single dst range count: " + countSingleSrc);
		System.out.println("single src range count: " + countSingleDst);
		System.out.println("double range count: " + (count-countSingleDst-countSingleSrc));
		System.out.println("total no-exact-prefix range count: " + count);
		
		result.add(src);
		result.add(dest);
		return result;
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//test old stat() without exact match or prefix match in single port field. 
		/*String filename = "test.txt";
		ArrayList<ArrayList<Integer>> results = stat(filename);
		for(ArrayList<Integer> list: results){
			for(int i = 0; i < list.size(); i=i+2)
				System.out.println("ordinary: " + list.get(i) + " with ours: " + list.get(i+1));
			System.out.println();
			System.out.println();
		}*/
		
		//special test for one line in classifier
		String l = "@128.0.0.0/1	0.0.0.0/0	1024 : 65535	33434 : 33600	0x11/0xFF	0x0000/0x0000";
		String[] t = l.split("	");
		//System.out.println(t.length);
		/*for(int i = 0; i < t.length; i++)
			System.out.println(t[i]);*/
		String[] s = t[2].split(" ");
		int[] srcPorts = new int[2];
		srcPorts[0] = Integer.parseInt(s[0]);
		srcPorts[1] = Integer.parseInt(s[2]);
		
		/*for(String str: s)
			System.out.println(str);*/
		s = t[3].split(" ");
		int[] dstPorts = new int[2];
		dstPorts[0] = Integer.parseInt(s[0]);
		dstPorts[1] = Integer.parseInt(s[2]);
		/*for(String str: s)
			System.out.println(str);*/
		System.out.println(srcPorts[0]);
		System.out.println(srcPorts[1]);
		System.out.println(dstPorts[0]);
		System.out.println(dstPorts[1]);
		
		//test parseClassifier().
		/*String filename1 = "testClassifier1-10k";
		parseClassifier(filename1);*/
		
		//test new stat().
		String filename = "range.txt";
		ArrayList<ArrayList<Integer>> results = stat(filename);
		for(ArrayList<Integer> list: results){
			for(int i = 0; i < list.size(); i=i+2)
				System.out.println("ordinary: " + list.get(i) + " with ours: " + list.get(i+1));
			System.out.println();
			System.out.println();
		}
		
		//test norangeStat().
		String filename2 = "exactPrefix.txt";
		norangeStat(filename2);
	
		
	}

}
