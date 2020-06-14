package fi.sepjaa.visualizer.pathfinding;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class ImmutablePathfindingData {

	private final ImmutableMap<Long, Node> nodes;
	private final long start, end, measurementSleep, evaluationSleep;
	private final ImmutableList<Long> path;
	private final Optional<ConnectedNodePair> measurement;
	private final ImmutableList<Long> evaluated;

	public ImmutablePathfindingData(Map<Long, Node> nodes, long start, long end, long measurementSleep,
			long evaluationSleep, List<Long> path, Optional<ConnectedNodePair> measurement, List<Long> evaluated) {
		this.nodes = ImmutableMap.copyOf(nodes);
		this.start = start;
		this.end = end;
		this.measurementSleep = measurementSleep;
		this.evaluationSleep = evaluationSleep;
		this.path = ImmutableList.copyOf(path);
		this.measurement = measurement;
		this.evaluated = ImmutableList.copyOf(evaluated);
	}

	public ImmutableMap<Long, Node> getNodes() {
		return nodes;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getMeasurementSleep() {
		return measurementSleep;
	}

	public long getEvaluationSleep() {
		return evaluationSleep;
	}

	public ImmutableList<Long> getPath() {
		return path;
	}

	public Optional<ConnectedNodePair> getMeasurement() {
		return measurement;
	}

	public ImmutableList<Long> getEvaluated() {
		return evaluated;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + ((evaluated == null) ? 0 : evaluated.hashCode());
		result = prime * result + (int) (evaluationSleep ^ (evaluationSleep >>> 32));
		result = prime * result + ((measurement == null) ? 0 : measurement.hashCode());
		result = prime * result + (int) (measurementSleep ^ (measurementSleep >>> 32));
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
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
		if (evaluationSleep != other.evaluationSleep) {
			return false;
		}
		if (measurement == null) {
			if (other.measurement != null) {
				return false;
			}
		} else if (!measurement.equals(other.measurement)) {
			return false;
		}
		if (measurementSleep != other.measurementSleep) {
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
		return "ImmutablePathfindingData [nodes=" + nodes + ", start=" + start + ", end=" + end + ", measurementSleep="
				+ measurementSleep + ", evaluationSleep=" + evaluationSleep + ", path=" + path + ", measurement="
				+ measurement + ", evaluated=" + evaluated + "]";
	}

}
