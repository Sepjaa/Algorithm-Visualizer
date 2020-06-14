package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.pathfinding.ConnectedNodePair;
import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.NodeDistance;
import fi.sepjaa.visualizer.pathfinding.NodeUtilities;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;

@Component
public class Dijkstra extends AbstractPathfindingAlgorithm {

	@Override
	public void find(PathfindingData data) {
		ImmutablePathfindingData copy = data.getCopy();
		ImmutableMap<Long, Node> nodes = copy.getNodes();
		long start = copy.getStart();
		long end = copy.getEnd();

		LOG.debug("Starting dijkstra pathfind with start {} end {}", nodes.get(start), nodes.get(end));
		Map<Long, NodeDistance> distances = initializeDistances(nodes, start);
		List<Long> evaluated = new ArrayList<>();
		Optional<Long> current = getNext(distances, evaluated);
		while (current.isPresent()) {
			LOG.trace("Current {}", current);
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			evaluated = evaluate(data, copy, current.get(), distances);
			current = getNext(distances, evaluated);
			if (current.isPresent() && current.get() == end) {
				LOG.debug("Reached end node as current with distance {}", distances.get(end));
				data.addAndReturnEvaluated(current.get(), distances);
				break;
			}
		}
		LOG.debug("Stopped dijkstra");
	}

	private Optional<Long> getNext(Map<Long, NodeDistance> distances, List<Long> evaluated) {
		Optional<NodeDistance> next = distances.values().stream().filter(n -> !evaluated.contains(n.getNodeId()))
				.sorted(NodeUtilities.getNodeDistanceComparator()).findFirst();
		Long result = null;
		if (next.isPresent() && next.get().getDistance() < Float.MAX_VALUE) {
			result = next.get().getNodeId();
		}
		return Optional.ofNullable(result);
	}

	private Map<Long, NodeDistance> initializeDistances(ImmutableMap<Long, Node> nodes, long start) {
		Map<Long, NodeDistance> distances = new HashMap<>();
		for (Node n : nodes.values()) {
			if (n.getId() != start) {
				distances.put(n.getId(), new NodeDistance(n.getId()));
			} else {
				distances.put(n.getId(), new NodeDistance(n.getId(), n.getId(), 0f));
			}
		}
		return distances;
	}

	private List<Long> evaluate(PathfindingData data, ImmutablePathfindingData copy, long nodeId,
			Map<Long, NodeDistance> distances) {
		List<Long> evaluatedNodes = data.addAndReturnEvaluated(nodeId, distances);
		ImmutableMap<Long, Node> nodes = copy.getNodes();
		Node node = nodes.get(nodeId);
		for (Long neighbourId : node.getConnections()) {
			if (!evaluatedNodes.contains(neighbourId) && !Thread.currentThread().isInterrupted()) {
				float distance = evaluateNeighbour(data, copy, nodeId, neighbourId, distances);
				NodeDistance current = distances.get(neighbourId);
				if (distance < current.getDistance()) {
					NodeDistance neww = new NodeDistance(neighbourId, nodeId, distance);
					LOG.debug("Updated distance for node id {} to {}", neighbourId, neww);
					distances.put(neighbourId, neww);
				}
			}
		}
		return evaluatedNodes;
	}

	protected float evaluateNeighbour(PathfindingData data, ImmutablePathfindingData copy, long nodeId,
			long neighbourId, Map<Long, NodeDistance> distances) {
		ImmutableMap<Long, Node> nodes = copy.getNodes();
		Node evaluatednode = nodes.get(nodeId);
		Node neighbour = nodes.get(neighbourId);
		float nodeDistance = distances.get(nodeId).getDistance();
		ConnectedNodePair pair = new ConnectedNodePair(evaluatednode, neighbour);
		return nodeDistance + data.measure(pair);
	}

	@Override
	public Type getType() {
		return Type.DIJKSTRA;
	}

}
