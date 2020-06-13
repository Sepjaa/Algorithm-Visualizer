package fi.sepjaa.visualizer.pathfinding;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ImmutablePathfindingData {

	private final ImmutableMap<Integer, Node> nodes;
	private final int start;
	private final int end;

	public ImmutablePathfindingData(Map<Integer, Node> nodes, int start, int end) {
		this.nodes = ImmutableMap.copyOf(nodes);
		this.start = start;
		this.end = end;
	}

	public ImmutableMap<Integer, Node> getNodes() {
		return nodes;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + start;
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
		ImmutablePathfindingData other = (ImmutablePathfindingData) obj;
		if (end != other.end) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ImmutablePathfindingData [nodes=" + nodes + ", start=" + start + ", end=" + end + "]";
	}

}
