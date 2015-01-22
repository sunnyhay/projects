package edu.wsu.eecs.nrl;

import java.util.ArrayList;
import java.util.UUID;

public class CommonUtil {
	/**
	 * summarize the test and give back result based on integer input 
	 */
	public static ArrayList<Double> sumStatInt(int paraNum, ArrayList<ArrayList<Integer>> testSet){
		ArrayList<Double> result = new ArrayList<Double>();
		int n = testSet.size();
		double[] counts = new double[paraNum];
		
		for(int i = 0; i < n; i++){
			ArrayList<Integer> row = testSet.get(i);
			for(int j = 0; j < counts.length; j++){
				counts[j] += row.get(j);
			}
		}
		for(int i = 0; i < counts.length; i++)
			result.add(counts[i] / n);
		
		return result;
	}
	
	/**
	 * summarize the test and give back result 
	 */
	public static ArrayList<Double> sumStat(int paraNum, ArrayList<ArrayList<Double>> testSet){
		ArrayList<Double> result = new ArrayList<Double>();
		int n = testSet.size();
		double[] counts = new double[paraNum];
		
		for(int i = 0; i < n; i++){
			ArrayList<Double> row = testSet.get(i);
			for(int j = 0; j < counts.length; j++){
				counts[j] += row.get(j);
			}
		}
		for(int i = 0; i < counts.length; i++)
			result.add(counts[i] / n);
		
		return result;
	}
	
	/**
	 * return n random strings capsulated in an array list.
	 */
	public static ArrayList<String> getRandomStrings(int n){
		ArrayList<String> targets = new ArrayList<String>();
		String uuid = null;
		for(int i = 0; i < n; i++){
			uuid = UUID.randomUUID().toString();
			targets.add(uuid);
		}
		return targets;	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*String uuid = UUID.randomUUID().toString();
		System.out.println("uuid = " + uuid);*/
		int n = 100;
		ArrayList<String> targets = getRandomStrings(n);
		for(String str : targets)
			System.out.println(str);
	}

}
