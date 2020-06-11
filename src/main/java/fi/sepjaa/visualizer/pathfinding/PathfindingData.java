package fi.sepjaa.visualizer.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.CommonConstants;

/**
 * Handles all the pathfinding execution and creation/distribution of nodes.
 *
 * @author Jaakko
 *
 */
public class PathfindingData {
	private static final Logger LOG = LoggerFactory.getLogger(PathfindingData.class);
	private static final float ACCEPTABLE_LOWER_THAN_AVERAGE_FACTOR = 0.8f;
	private static final int ACCEPTABLE_SEARCH_ITERATIONS = 1000;
	// private final Object lock = new Object();

	private ImmutableMap<Integer, Node> nodes;

	public PathfindingData(int nodeCount, int connectionsCount) {
		if (nodeCount <= 0) {
			LOG.error("Invalid node count {}, set to default {}", nodeCount,
					CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT);
			nodeCount = CommonConstants.DEFAULT_PATHFINDING_NODE_AMOUNT;
		}
		if (connectionsCount < 0) {
			LOG.error("Invalid connection count {}, set to default {}", connectionsCount,
					CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT);
			connectionsCount = CommonConstants.DEFAULT_PATHFINDING_CONNECTIONS_AMOUNT;
		}
		this.nodes = spawnNodes(nodeCount, connectionsCount);

	}

	private ImmutableMap<Integer, Node> spawnNodes(int nodeCount, int connectionsCount) {
		Map<Integer, Node> builder = new HashMap<>();
		for (int i = 0; i < nodeCount; i++) {
			builder.put(i, spawnAcceptableNode(i, builder));
		}
		for (int i = 0; i < connectionsCount; i++) {
			Node node = builder.get(i % nodeCount);
			List<Node> nodeList = new ArrayList<>(builder.values());
			Collections.sort(nodeList, new Comparator<Node>() {
				@Override
				public int compare(Node o1, Node o2) {
					return (int) ((node.distanceTo(o1) - node.distanceTo(o2)) * 10000);
				}
			});
			nodeList.stream().filter(n -> {
				if (n.getId() == node.getId()) {
					return false;
				}
				return !node.getConnections().contains(n.getId());
			}).findFirst().ifPresent(n -> {
				node.addConnection(n.getId());
				n.addConnection(node.getId());
			});
		}
		return ImmutableMap.copyOf(builder);
	}

	private float calcAverageClosestDistance(Map<Integer, Node> currentNodes) {
		return (float) currentNodes.values().stream().mapToDouble(value -> {
			return currentNodes.values().stream().filter(node -> !node.equals(value)).mapToDouble(node -> {
				return (double) node.distanceTo(value);
			}).min().orElse(0);
		}).average().orElse(0);
	}

	private float findClosestDistance(Node candidate, Map<Integer, Node> currentNodes) {
		return (float) currentNodes.values().stream().mapToDouble(value -> {
			return candidate.distanceTo(value);
		}).min().orElse(Double.MAX_VALUE);
	}

	private Node spawnAcceptableNode(int id, Map<Integer, Node> currentNodes) {
		Node candidate = Node.randomInstance(id);
		float mostDistant = 0;
		float avgDistance = calcAverageClosestDistance(currentNodes);
		LOG.trace("Avg distance {} at {}", avgDistance, id);
		for (int i = 0; i < ACCEPTABLE_SEARCH_ITERATIONS; i++) {
			// Create new node with random location
			Node it = Node.randomInstance(id);
			float distance = findClosestDistance(it, currentNodes);
			if (distance > avgDistance * ACCEPTABLE_LOWER_THAN_AVERAGE_FACTOR) {
				// Found a node that is relatively distant from all nodes
				LOG.trace("Found acceptable node with distance {} ", avgDistance, id);
				return it;
			} else if (distance > mostDistant) {
				// Save
				mostDistant = distance;
				candidate = it;
				LOG.trace("Found candidate node with distance {} ", distance, id);
			}
		}
		LOG.info("Exhausted acceptable iterations avg {} mostDistant {}", avgDistance, mostDistant);
		return candidate;
	}

	public ImmutablePathfindingData getCopy() {
		// synchronized (lock) {
		return new ImmutablePathfindingData(nodes);
		// }
	}
}