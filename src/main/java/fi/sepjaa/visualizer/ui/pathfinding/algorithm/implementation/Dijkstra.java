package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm;

@Component
public class Dijkstra implements PathfindingAlgorithm {
	private static final Logger LOG = LoggerFactory.getLogger(Dijkstra.class);

	@Override
	public void find(PathfindingData data) {
		ImmutablePathfindingData copy = data.getCopy();
		ImmutableMap<Integer, Node> nodes = copy.getNodes();
		int start = copy.getStart();
		int end = copy.getEnd();

		LOG.debug("Starting dijkstra pathfind with start {} end {}", nodes.get(start), nodes.get(end));
		Map<Integer, NodeDistance> distances = initializeDistances(nodes, start);
		List<Integer> evaluated = new ArrayList<>();
		Optional<Integer> current = getNext(distances, evaluated);
		while (current.isPresent()) {
			LOG.trace("Current {}", current);
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			evaluate(current.get(), nodes, distances, evaluated);
			current = getNext(distances, evaluated);
			if (current.isPresent() && current.get() == end) {
				LOG.debug("Reached end node as current with distance {}", distances.get(end));
				List<Integer> path = extractPath(distances, start, end);
				LOG.info("Found path {}", path);
				data.setPath(path);
				break;
			}
		}
		LOG.debug("Stopped dijkstra");
	}

	private Optional<Integer> getNext(Map<Integer, NodeDistance> distances, List<Integer> evaluated) {
		Optional<NodeDistance> next = distances.values().stream().filter(n -> !evaluated.contains(n.getNodeId()))
				.sorted((n1, n2) -> (int) ((n1.getDistance() - n2.getDistance()) * 1000000)).findFirst();
		Integer result = null;
		if (next.isPresent() && next.get().getDistance() < Float.MAX_VALUE) {
			result = next.get().getNodeId();
		}
		return Optional.ofNullable(result);
	}

	private Map<Integer, NodeDistance> initializeDistances(ImmutableMap<Integer, Node> nodes, int start) {
		Map<Integer, NodeDistance> distances = new HashMap<>();
		for (Node n : nodes.values()) {
			if (n.getId() != start) {
				distances.put(n.getId(), new NodeDistance(n.getId()));
			} else {
				distances.put(n.getId(), new NodeDistance(n.getId(), n.getId(), 0f));
			}
		}
		return distances;
	}

	private void evaluate(int nodeId, ImmutableMap<Integer, Node> nodes, Map<Integer, NodeDistance> distances,
			List<Integer> evaluated) {
		Node node = nodes.get(nodeId);
		float nodeDistance = distances.get(nodeId).getDistance();
		for (Integer neighbourId : node.getConnections()) {
			Node neighbour = nodes.get(neighbourId);
			float distance = nodeDistance + node.distanceTo(neighbour);
			NodeDistance current = distances.get(neighbour.getId());
			if (distance < current.getDistance()) {
				NodeDistance neww = new NodeDistance(neighbourId, nodeId, distance);
				LOG.debug("Updated distance for node {} to {}", neighbour, neww);
				distances.put(neighbourId, neww);
			}
		}
		evaluated.add(nodeId);
	}

	private List<Integer> extractPath(Map<Integer, NodeDistance> distances, int start, int end) {
		List<Integer> result = new LinkedList<>();
		int current = end;
		while (current != start) {
			result.add(current);
			current = distances.get(current).getPredecessor();
		}
		result.add(start);
		return result;
	}

	@Override
	public Type getType() {
		return Type.DIJKSTRA;
	}

	private class NodeDistance {
		private final int nodeId;
		private final int predecessor;
		private final float distance;

		private NodeDistance(int nodeId) {
			this(nodeId, CommonConstants.NO_STATEMENT, Float.MAX_VALUE);
		}

		private NodeDistance(int nodeId, int predecessor, float distance) {
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
			result = prime * result + getEnclosingInstance().hashCode();
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
			if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {
				return false;
			}
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

		private Dijkstra getEnclosingInstance() {
			return Dijkstra.this;
		}

		@Override
		public String toString() {
			return "NodeDistance [distance=" + distance + ", nodeId=" + nodeId + ", predecessor=" + predecessor + "]";
		}

	}

}
