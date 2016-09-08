package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InputReader {
	public static Map<Long, MyNodeInfo> nodeHashMap;
	public static HashMap<Long, LinkedList<String>> adjListHashMap;
	public static Map<Integer, Map<Integer,Integer>> partitionHashMap;
	public static HashMap<Long, LinkedList<String>> adjReverseListHashMap;
	
	public static void readJNodeFile() throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(new File("C:\\Users\\PIYUSH\\Downloads\\IMSC\\IMSC\\data\\j-Nodes.csv")));
			String data = "";
			nodeHashMap = new HashMap<Long, MyNodeInfo>();
			while ((data = br.readLine()) != null) {
				String[] info = data.split(",");
				MyNodeInfo nf = new MyNodeInfo();
				nf.setNodeId(Long.parseLong(info[0].substring(1)));
				nf.setLat(Double.parseDouble(info[1]));
				nf.setLon(Double.parseDouble(info[2]));
				nodeHashMap.put(Long.parseLong(info[0].substring(1)), nf);
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			br.close();
		}

	}
	
	public static void readIntraFile() throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(new File("C:\\Users\\PIYUSH\\Downloads\\IMSC\\IMSC\\data\\intra.csv")));
			String data = "";
			data = br.readLine();
			while ((data = br.readLine()) != null) {
				String[] info = data.split(",");
				MyNodeInfo nf = nodeHashMap.get(Long.parseLong(info[0]));
				nf.setPartition(Integer.parseInt(info[1]));
				nf.setBoundary(Boolean.parseBoolean(info[2]));
				nf.setFromBoundaryMS(Integer.parseInt(info[3]));
				nf.setToBoundaryMS(Integer.parseInt(info[4]));
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			br.close();
		}

	}
	
	public static void readInterFile() throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new FileReader(new File("C:\\Users\\PIYUSH\\Downloads\\IMSC\\IMSC\\data\\inter.csv")));
			String data = "";
			partitionHashMap = new HashMap<>();
			data = br.readLine();
			while ((data = br.readLine()) != null) {
				String[] info = data.split(",");
				if(partitionHashMap.containsKey(Integer.parseInt(info[0]))){
					Map<Integer, Integer> map = partitionHashMap.get(Integer.parseInt(info[0]));
					map.put(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
				} else {
					Map<Integer, Integer> map = new HashMap<>();
					map.put(Integer.parseInt(info[1]), Integer.parseInt(info[2]));
					partitionHashMap.put(Integer.parseInt(info[0]), map);
				}
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			br.close();
		}

	}
	
	public static void readAdjList() throws Exception {
		BufferedReader br = null;
		int counter=0;
		try {
			br = new BufferedReader(
					new FileReader(new File("C:\\Users\\PIYUSH\\Downloads\\IMSC\\IMSC\\data\\j-AdjList_Thursday.txt")));
			String data = "";
			adjListHashMap = new HashMap<Long, LinkedList<String>>();
			
			while ((data = br.readLine()) != null) {
				long startNode = Long.parseLong(data.substring(1,data.indexOf("|")));
				if(adjListHashMap.containsKey(startNode)){
					LinkedList<String> list = adjListHashMap.get(startNode);
					list.add(data.substring(data.indexOf("|")+1));
				} else {
					LinkedList<String> list = new LinkedList<>();
					list.add(data.substring(data.indexOf("|")+1));
					adjListHashMap.put(startNode, list);
				}
				counter++;
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		} finally {
			br.close();
			System.out.println("JVM total memory usage: " + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " megabytes");
			System.out.println(counter);
		}
	}
}
