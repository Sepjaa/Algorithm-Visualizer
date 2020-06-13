package fi.sepjaa.visualizer.pathfinding;

import fi.sepjaa.visualizer.common.CommonConstants;

public class NodeDistance {
	private final int nodeId;
	private final int predecessor;
	private final float distance;

	public NodeDistance(int nodeId) {
		this(nodeId, CommonConstants.NO_STATEMENT, Float.MAX_VALUE);
	}

	public NodeDistance(int nodeId, int predecessor, float distance) {
		this.nodeId = nodeId;
		this.predecessor = predecessor;
		this.distance = distance;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getPredecessor() {
		return predecessor;
	}

	public float getDistance() {
		return distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(distance);
		result = prime * result + nodeId;
		result = prime * result + predecessor;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NodeDistance other = (NodeDistance) obj;
		if (Float.floatToIntBits(distance) != Float.floatToIntBits(other.distance)) {
			return false;
		}
		if (nodeId != other.nodeId) {
			return false;
		}
		if (predecessor != other.predecessor) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NodeDistance [distance=" + distance + ", nodeId=" + nodeId + ", predecessor=" + predecessor + "]";
	}

}
