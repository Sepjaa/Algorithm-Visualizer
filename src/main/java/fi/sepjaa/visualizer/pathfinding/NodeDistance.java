package fi.sepjaa.visualizer.pathfinding;

import fi.sepjaa.visualizer.common.CommonConstants;

public class NodeDistance {
	private final long nodeId;
	private final long predecessor;
	private final float distance;
	private final float heuristicDistance;

	public NodeDistance(long nodeId) {
		this(nodeId, CommonConstants.NO_STATEMENT, Float.MAX_VALUE, 0);
	}

	public NodeDistance(long nodeId, long predecessor, float distance) {
		this(nodeId, predecessor, distance, 0);
	}

	public NodeDistance(long nodeId, long predecessor, float distance, float heuristicDistance) {
		this.nodeId = nodeId;
		this.predecessor = predecessor;
		this.distance = distance;
		this.heuristicDistance = heuristicDistance;
	}

	public long getNodeId() {
		return nodeId;
	}

	public long getPredecessor() {
		return predecessor;
	}

	public float getDistance() {
		return distance;
	}

	public float getHeuristicDistance() {
		return heuristicDistance;
	}

	public float getTotalDistance() {
		return distance + heuristicDistance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(distance);
		result = prime * result + Float.floatToIntBits(heuristicDistance);
		result = prime * result + (int) (nodeId ^ (nodeId >>> 32));
		result = prime * result + (int) (predecessor ^ (predecessor >>> 32));
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
		if (Float.floatToIntBits(heuristicDistance) != Float.floatToIntBits(other.heuristicDistance)) {
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
		return "NodeDistance [nodeId=" + nodeId + ", predecessor=" + predecessor + ", distance=" + distance
				+ ", heuristicDistance=" + heuristicDistance + "]";
	}

}
