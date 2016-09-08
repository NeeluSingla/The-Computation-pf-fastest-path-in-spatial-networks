package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class ImplementingAStar {
	
	static int topSpeed = 0;

	public static void main(String[] args) throws IOException {
		
		readInputData();
		
		System.out.println("Data Read done");
		
		topSpeed = Integer.parseInt(args[3]);
		
		//List<MyNodeInfo> pointList = routingAStarWithTimeCost(Long.parseLong(args[0]),Long.parseLong(args[1]),args[2]);
		
		List<MyNodeInfo> pointList = routingAStarForwardingWithTimeCost(Long.parseLong(args[0]),Long.parseLong(args[1]),args[2]);
		
		System.out.println(pointList.size());
		
		generateOutputKMLFile(pointList);

	}


	/**
	 * @param pointList
	 * @throws IOException
	 */
	private static void generateOutputKMLFile(List<MyNodeInfo> pointList) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("points.kml")));
		
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> <kml xmlns=\"http://earth.google.com/kml/2.0\"> <Document> <Style id=\"z1\"> <IconStyle><Icon><href>http://www.google.com/intl/en_us/mapfiles/ms/micons/blue-dot.png</href></Icon></IconStyle> </Style>");
		bw.newLine();
		String line="<Style id=\"street\">     <LineStyle>       <color>ff235523</color>       <gx:physicalWidth>100</gx:physicalWidth>     </LineStyle>   </Style>   <Placemark>     <styleUrl>#street</styleUrl>     <LineString>   <coordinates>";
		for(MyNodeInfo nodInfo : pointList){
			bw.write("<Placemark><name>"+ nodInfo.getNodeId()+"</name>");
			bw.newLine();
			bw.write("<styleUrl>#z1</styleUrl><Point><coordinates>"+ nodInfo.getLon()+"," + nodInfo.getLat()+"</coordinates></Point>");
			bw.newLine();
			bw.write("</Placemark>");
			bw.newLine();
			line = line +nodInfo.getLon()+"," + nodInfo.getLat()+",200\n";
		}
		line = line +" </coordinates>     </LineString>   </Placemark>";
		bw.write(line);
		bw.write("</Document></kml>");
		bw.close();
	}

	
	private static List<MyNodeInfo> routingAStarForwardingWithTimeCost(long startNode, long endNode, String time) {
		List<MyNodeInfo> listMyNodeInfo = new ArrayList<>();
		try {
		
		PriorityQueue<MyNodeInfo> openSet = new PriorityQueue<MyNodeInfo>( 10000, new Comparator<MyNodeInfo>() {
			public int compare(MyNodeInfo n1, MyNodeInfo n2) {
					return (int)(n1.getFinalCost() - n2.getFinalCost());
			}
		});
		
		String[] timeParts = time.split(":");
		int hour = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);
		int second = Integer.parseInt(timeParts[2]);
		if(minute%15==0 && second>0){
			minute++;
		}
		
		if(hour<6){
			hour =24;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = sdf.parse(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTime);
		
		openSet.add(InputReader.nodeHashMap.get(startNode));
		
		HashSet<Long> closedHashSet = new HashSet<Long>();
		
		
		
		while(true){
			
			MyNodeInfo currentNode = openSet.poll();
			
			if(currentNode == null){
				return listMyNodeInfo;
				
			}
			
			if(!listMyNodeInfo.contains(currentNode)){
				listMyNodeInfo.add(currentNode);
			}
			//System.out.println(currentNode.getNodeId());
			
			if(currentNode.getLastValue()!=0){
				int secondsToAddToCurrentTime =  currentNode.getLastValue();
				cal.add(Calendar.SECOND, secondsToAddToCurrentTime);
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND);
				if(minute%15==0 && second>0){
					minute++;
				}
				if(hour<6){
					hour =24;
				}
			}
			
			if(currentNode.getNodeId() == endNode){
				return listMyNodeInfo;
			}
			
			HashMap<Long , Integer> nearestNeighourWithDistances = getNearestNeighourWithDistances(currentNode.getNodeId() , hour, minute);
			for(Entry<Long , Integer> entry : nearestNeighourWithDistances.entrySet()){
				if(!closedHashSet.contains(entry.getKey())){
					MyNodeInfo myNodeInfo = InputReader.nodeHashMap.get(entry.getKey());
					double heuristicValue = getEstimatedHuristicForwarding(myNodeInfo, InputReader.nodeHashMap.get(endNode))*15;
					//System.out.println("heuristicValue:-"+heuristicValue);
					myNodeInfo.setFinalCost(currentNode.getTotalCost()+entry.getValue()/1000+heuristicValue);
					myNodeInfo.setTotalCost(currentNode.getTotalCost()+entry.getValue()/1000);
					myNodeInfo.setLastValue(entry.getValue()/1000);
					//System.out.println("final cost:-"+myNodeInfo.getFinalCost());
				//	System.out.println("total cost:-"+myNodeInfo.getTotalCost());
					openSet.add(myNodeInfo);
				} 
			}
			
			closedHashSet.add(currentNode.getNodeId());
			
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return listMyNodeInfo;
	}
	
	
	
	private static double getEstimatedHuristicForwarding(MyNodeInfo startNode, MyNodeInfo destinationNode) {
		int startNodeToBorder=0;
		int endNodeToBorder=0;
		int startNodeBorderToEndNodeBorder=0;
		if(!startNode.isBoundary()){
			startNodeToBorder = startNode.toBoundaryMS;
		}
		if(!destinationNode.isBoundary()){
			endNodeToBorder = destinationNode.fromBoundaryMS;
		}
		if(startNode.getPartition()!=destinationNode.getPartition()){
			startNodeBorderToEndNodeBorder = InputReader.partitionHashMap.get(startNode.getPartition()).get(destinationNode.getPartition());
		}
		return startNodeToBorder/1000 + endNodeToBorder/1000 + startNodeBorderToEndNodeBorder/1000;
	}


	private static List<MyNodeInfo> routingAStarWithTimeCost(long startNode, long endNode, String time) {
		List<MyNodeInfo> listMyNodeInfo = new ArrayList<>();
		try {
		
		PriorityQueue<MyNodeInfo> openSet = new PriorityQueue<MyNodeInfo>( 10000, new Comparator<MyNodeInfo>() {
			public int compare(MyNodeInfo n1, MyNodeInfo n2) {
					return (int)(n1.getFinalCost() - n2.getFinalCost());
			}
		});
		
		String[] timeParts = time.split(":");
		int hour = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);
		int second = Integer.parseInt(timeParts[2]);
		if(minute%15==0 && second>0){
			minute++;
		}
		
		if(hour<6){
			hour =24;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = sdf.parse(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentTime);
		
		openSet.add(InputReader.nodeHashMap.get(startNode));
		
		HashSet<Long> closedHashSet = new HashSet<Long>();
		
		
		
		while(true){
			
			MyNodeInfo currentNode = openSet.poll();
			
			if(currentNode == null){
				return listMyNodeInfo;
				
			}
			
			if(!listMyNodeInfo.contains(currentNode)){
				listMyNodeInfo.add(currentNode);
			}
			//System.out.println(currentNode.getNodeId());
			
			if(currentNode.getLastValue()!=0){
				int secondsToAddToCurrentTime =  currentNode.getLastValue();
				cal.add(Calendar.SECOND, secondsToAddToCurrentTime);
				hour = cal.get(Calendar.HOUR_OF_DAY);
				minute = cal.get(Calendar.MINUTE);
				second = cal.get(Calendar.SECOND);
				if(minute%15==0 && second>0){
					minute++;
				}
				if(hour<6){
					hour =24;
				}
			}
			
			if(currentNode.getNodeId() == endNode){
				return listMyNodeInfo;
			}
			
			HashMap<Long , Integer> nearestNeighourWithDistances = getNearestNeighourWithDistances(currentNode.getNodeId() , hour, minute);
			for(Entry<Long , Integer> entry : nearestNeighourWithDistances.entrySet()){
				if(!closedHashSet.contains(entry.getKey())){
					MyNodeInfo myNodeInfo = InputReader.nodeHashMap.get(entry.getKey());
					double heuristicValue = getEstimatedHuristic(myNodeInfo, InputReader.nodeHashMap.get(endNode))/80*3600;
					//System.out.println("heuristicValue:-"+heuristicValue);
					myNodeInfo.setFinalCost(currentNode.getTotalCost()+entry.getValue()/1000+heuristicValue);
					myNodeInfo.setTotalCost(currentNode.getTotalCost()+entry.getValue()/1000);
					myNodeInfo.setLastValue(entry.getValue()/1000);
					//System.out.println("final cost:-"+myNodeInfo.getFinalCost());
					//System.out.println("total cost:-"+myNodeInfo.getTotalCost());
					openSet.add(myNodeInfo);
				} 
			}
			
			closedHashSet.add(currentNode.getNodeId());
			
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return listMyNodeInfo;
	}

	private static double getEstimatedHuristic(MyNodeInfo currentNode, MyNodeInfo destination) {
		double dlon = currentNode.getLon() - destination.getLon(); 
		double dlat = currentNode.getLat() - destination.getLat(); 
		double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(destination.getLat()) * Math.cos(currentNode.getLat()) * Math.pow(Math.sin(dlon/2),2); 
		return 6371 *2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a)); 
	}

	/**
	 * //n100006|n100007:4270;n490968:9081;n65069:22317;n85736:21573
	 * @param currentNode
	 * @param time 
	 * @return
	 */
	private static HashMap<Long, Integer> getNearestNeighourWithDistances(long currentNode, int hour, int minute) {
		HashMap<Long, Integer> map = new HashMap<>();
		LinkedList<String> list = InputReader.adjListHashMap.get(currentNode);
		for(String str : list){
			String[] adjNodes = str.split(";");
			for(String adjNode : adjNodes){
				String[] info = adjNode.split(":");
				if(info[0].contains("(V)")){
					long node = Long.parseLong(info[0].substring(1, info[0].indexOf("(V)")));
					/*if(minute%15==0){
						int position = (hour-6)*4+(minute/15);
						if(position>=60){
							position = 59;
						}
						map.put(node, Integer.parseInt(info[1].split(",")[position]));
					}else {*/
						int startPosition = (hour-6)*4+(minute/15);
						if(startPosition>=60){
							startPosition = 59;
						}
						
						map.put(node, (Integer.parseInt(info[1].split(",")[startPosition])));
					//}
					
				} else {
					long node = Long.parseLong(info[0].substring(1));
					map.put(node, Integer.parseInt(info[1]));
				}
			}
		}
		return map;
	}



	/**
	 * 
	 */
	private static void readInputData() {
		try {
			InputReader.readJNodeFile();
			InputReader.readIntraFile();
			InputReader.readInterFile();
			InputReader.readAdjList();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
