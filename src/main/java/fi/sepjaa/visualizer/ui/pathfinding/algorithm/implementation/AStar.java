package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.pathfinding.ConnectedNodePair;
import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.NodeDistance;
import fi.sepjaa.visualizer.pathfinding.NodeUtilities;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;

@Component
public class AStar extends AbstractPathfindingAlgorithm {

	@Override
	public void find(PathfindingData data) {
		ImmutablePathfindingData copy = data.getCopy();
		ImmutableMap<Long, Node> nodes = copy.getNodes();
		long startId = copy.getStart();
		long endId = copy.getEnd();
		Map<Long, NodeDistance> distances = initializeDistances(nodes, startId);

		Map<Long, NodeDistance> open = new HashMap<>();
		List<Long> evaluated = new ArrayList<>();
		open.put(copy.getStart(), new NodeDistance(startId));
		while (!open.isEmpty()) {
			if (Thread.currentThread().isInterrupted()) {
				break;
			}
			NodeDistance current = open.values().stream().sorted(NodeUtilities.getNodeDistanceComparator()).findFirst()
					.get();
			LOG.debug("Open: {}", open);
			LOG.debug("Current: {}", current);
			open.remove(current.getNodeId());
			evaluated.add(current.getNodeId());
			data.addAndReturnEvaluated(current.getNodeId(), distances);
			if (endId == current.getNodeId()) {
				data.addAndReturnEvaluated(current.getNodeId(), distances);
				break;
			}
			for (long neighbourId : nodes.get(current.getNodeId()).getConnections()) {
				if (evaluated.contains(neighbourId)) {
					continue;
				}
				LOG.debug("Checking neighbour: {}", neighbourId);
				float distance = evaluateNeighbour(data, copy, current.getNodeId(), neighbourId, distances);
				float heuristicDistance = distanceToEnd(copy, neighbourId);
				if (open.containsKey(neighbourId)) {
					if (distances.get(neighbourId).getDistance() < distance) {
						LOG.debug("Neighbour distance {} was higher than previous distance {}", distance,
								distances.get(neighbourId));
						continue;
					}
				}
				NodeDistance neighbourDistance = new NodeDistance(neighbourId, current.getNodeId(), distance,
						heuristicDistance);
				LOG.debug("Putting neighbour distance: {}", neighbourDistance);
				open.put(neighbourId, neighbourDistance);
				distances.put(neighbourId, neighbourDistance);
			}
		}
	}

	protected float distanceToEnd(ImmutablePathfindingData copy, long neighbourId) {
		ImmutableMap<Long, Node> nodes = copy.getNodes();
		Node neighbour = nodes.get(neighbourId);
		Node end = nodes.get(copy.getEnd());
		return neighbour.distanceTo(end);
	}

	@Override
	public Type getType() {
		return Type.A_STAR;
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

	protected Map<Long, NodeDistance> initializeDistances(ImmutableMap<Long, Node> nodes, long startId) {
		Map<Long, NodeDistance> distances = new HashMap<>();
		for (Node n : nodes.values()) {
			if (n.getId() != startId) {
				distances.put(n.getId(), new NodeDistance(n.getId()));
			} else {
				distances.put(n.getId(), new NodeDistance(n.getId(), n.getId(), 0f));
			}
		}
		return distances;
	}
}
