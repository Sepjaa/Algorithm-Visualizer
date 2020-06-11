package fi.sepjaa.visualizer.pathfinding;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ImmutablePathfindingData {

	private final ImmutableMap<Integer, Node> nodes;

	public ImmutablePathfindingData(Map<Integer, Node> nodes) {
		this.nodes = ImmutableMap.copyOf(nodes);
	}

	public ImmutableMap<Integer, Node> getNodes() {
		return nodes;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
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
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ImmutablePathfindingData [nodes=" + nodes + "]";
	}

}
