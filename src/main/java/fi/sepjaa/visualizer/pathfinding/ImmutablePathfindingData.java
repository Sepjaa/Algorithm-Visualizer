package fi.sepjaa.visualizer.pathfinding;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ImmutablePathfindingData {

	private final ImmutableMap<Integer, Node> nodes;
	private final int start;
	private final int end;
	private final ImmutableList<Integer> path;
	private final Optional<ConnectedNodePair> measurement;
	private final ImmutableList<Integer> evaluated;

	public ImmutablePathfindingData(Map<Integer, Node> nodes, int start, int end, List<Integer> path,
			Optional<ConnectedNodePair> measurement, List<Integer> evaluated) {
		this.nodes = ImmutableMap.copyOf(nodes);
		this.start = start;
		this.end = end;
		this.path = ImmutableList.copyOf(path);
		this.measurement = measurement;
		this.evaluated = ImmutableList.copyOf(evaluated);
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

	public ImmutableList<Integer> getPath() {
		return path;
	}

	public Optional<ConnectedNodePair> getMeasurement() {
		return measurement;
	}

	public ImmutableList<Integer> getEvaluated() {
		return evaluated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + ((evaluated == null) ? 0 : evaluated.hashCode());
		result = prime * result + ((measurement == null) ? 0 : measurement.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		if (evaluated == null) {
			if (other.evaluated != null) {
				return false;
			}
		} else if (!evaluated.equals(other.evaluated)) {
			return false;
		}
		if (measurement == null) {
			if (other.measurement != null) {
				return false;
			}
		} else if (!measurement.equals(other.measurement)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (start != other.start) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ImmutablePathfindingData [nodes=" + nodes + ", start=" + start + ", end=" + end + ", path=" + path
				+ ", measurement=" + measurement + ", evaluated=" + evaluated + "]";
	}

}
