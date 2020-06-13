package fi.sepjaa.visualizer.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
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
	private static final int ACCEPTABLE_SEARCH_ITERATIONS = 100;

	private final Object lock = new Object();
	@GuardedBy("lock")
	private ImmutableMap<Integer, Node> nodes;
	@GuardedBy("lock")
	private int start = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private int end = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private List<Integer> path = ImmutableList.of();

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

	public static Comparator<Node> getNodeDistanceComparator(Node testNode) {
		return (Node n1, Node n2) -> (int) (10000 * (testNode.distanceTo(n1) - testNode.distanceTo(n2)));
	}

	public static Optional<Node> getClosestNode(float x, float y, Collection<Node> nodes) {
		Node test = new Node(CommonConstants.NO_STATEMENT, x, y);
		return nodes.stream().sorted(getNodeDistanceComparator(test)).findFirst();
	}

	private ImmutableMap<Integer, Node> spawnNodes(int nodeCount, int connectionsCount) {
		Map<Integer, Node> builder = new HashMap<>();
		float averageDistance = 0.5f;
		int refreshDistanceAt = 2;
		for (int i = 0; i < nodeCount; i++) {
			if (i > refreshDistanceAt) {
				refreshDistanceAt *= 2;
				averageDistance = calcAverageClosestDistance(builder.values());
			}
			builder.put(i, spawnAcceptableNode(i, builder, averageDistance));
		}
		for (int i = 0; i < connectionsCount; i++) {
			Node node = builder.get(i % nodeCount);
			List<Node> nodeList = new ArrayList<>(builder.values());
			nodeList.stream().sorted(getNodeDistanceComparator(node)).filter(n -> {
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

	private static float calcAverageClosestDistance(Collection<Node> nodes) {
		return (float) nodes.stream().mapToDouble(value -> {
			return nodes.stream().filter(node -> !node.equals(value)).mapToDouble(node -> {
				return (double) node.distanceTo(value);
			}).min().orElse(0);
		}).average().orElse(0);
	}

	private static float findClosestDistance(Node candidate, Collection<Node> nodes) {
		return (float) nodes.stream().mapToDouble(value -> {
			return candidate.distanceTo(value);
		}).min().orElse(Double.MAX_VALUE);
	}

	private static Node spawnAcceptableNode(int id, Map<Integer, Node> currentNodes, float avgDistance) {
		Node candidate = Node.randomInstance(id);
		float mostDistant = 0;
		LOG.trace("Avg distance {} at {}", avgDistance, id);
		for (int i = 0; i < ACCEPTABLE_SEARCH_ITERATIONS; i++) {
			// Create new node with random location
			Node it = Node.randomInstance(id);
			float distance = findClosestDistance(it, currentNodes.values());
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
		synchronized (lock) {
			return new ImmutablePathfindingData(nodes, start, end, path);
		}
	}

	private void setStart(int start) {
		synchronized (lock) {
			this.start = start;
		}
	}

	private void setEnd(int end) {
		synchronized (lock) {
			this.end = end;
		}
	}

	public void setSelected(int id) {
		synchronized (lock) {
			if (start == CommonConstants.NO_STATEMENT) {
				setStart(id);
			} else if (end == CommonConstants.NO_STATEMENT) {
				if (start != id) {
					setEnd(id);
				}
			} else {
				clearSelections();
				setStart(id);
			}
		}
	}

	public void clearSelections() {
		synchronized (lock) {
			setStart(CommonConstants.NO_STATEMENT);
			setEnd(CommonConstants.NO_STATEMENT);
			setPath(ImmutableList.of());
		}
	}

	public void setPath(List<Integer> path) {
		synchronized (lock) {
			this.path = ImmutableList.copyOf(path);
		}
	}
}