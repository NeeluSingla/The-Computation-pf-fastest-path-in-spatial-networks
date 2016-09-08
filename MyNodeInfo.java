package controller;

public class MyNodeInfo {
	long nodeId;
	double lat;
	double lon;
	int partition;
	boolean boundary;
	int fromBoundaryMS;
	int toBoundaryMS;
	double totalCost = 0.0;
	double finalCost = 0.0;
	int lastValue = 0;
	
	
	public int getLastValue() {
		return lastValue;
	}
	public void setLastValue(int lastValue) {
		this.lastValue = lastValue;
	}
	public double getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	public double getFinalCost() {
		return finalCost;
	}
	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public int getPartition() {
		return partition;
	}
	public void setPartition(int partition) {
		this.partition = partition;
	}
	public boolean isBoundary() {
		return boundary;
	}
	public void setBoundary(boolean boundary) {
		this.boundary = boundary;
	}
	public int getFromBoundaryMS() {
		return fromBoundaryMS;
	}
	public void setFromBoundaryMS(int fromBoundaryMS) {
		this.fromBoundaryMS = fromBoundaryMS;
	}
	public int getToBoundaryMS() {
		return toBoundaryMS;
	}
	public void setToBoundaryMS(int toBoundaryMS) {
		this.toBoundaryMS = toBoundaryMS;
	}
}
