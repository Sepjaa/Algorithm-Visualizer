package fi.sepjaa.visualizer.pathfinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.CommonConstants;

/**
 * Static utility tool for creating {@link Node}s randomly but actually not
 * "that randomly" and other {@link Node} ease of access methods.
 *
 * @author Jaakko
 *
 */
public class NodeUtilities {
	public static final Logger LOG = LoggerFactory.getLogger(NodeUtilities.class);
	private static final float ACCEPTABLE_LOWER_THAN_AVERAGE_FACTOR = 0.80f;
	private static final int ACCEPTABLE_SEARCH_ITERATIONS = 100;
	public static final int INT_FLOAT_COMPARATOR_ACCURACY = 10000;

	public static ImmutableMap<Long, Node> spawnNodes(long nodeCount, long connectionsCount) {
		Map<Long, Node> builder = new HashMap<>();
		float averageDistance = 0.5f;
		int refreshDistanceAt = 2;
		for (long i = 0; i < nodeCount; i++) {
			if (i > refreshDistanceAt) {
				refreshDistanceAt *= 2;
				averageDistance = calcAverageClosestDistance(builder.values());
			}
			builder.put(i, spawnAcceptableNode(i, builder, averageDistance));
		}
		for (int i = 0; i < connectionsCount; i++) {
			Node node = builder.get(i % nodeCount);
			List<Node> nodeList = new ArrayList<>(builder.values());
			nodeList.stream().sorted(getNodeComparator(node)).filter(n -> {
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

	public static Optional<Node> getClosestNode(float x, float y, Collection<Node> nodes) {
		Node test = new Node(CommonConstants.NO_STATEMENT, x, y);
		return nodes.stream().sorted(getNodeComparator(test)).findFirst();
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

	private static Node spawnAcceptableNode(long id, Map<Long, Node> currentNodes, float avgDistance) {
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
		LOG.debug("Exhausted acceptable iterations avg {} mostDistant {}", avgDistance, mostDistant);
		return candidate;
	}

	public static Comparator<Node> getNodeComparator(Node testNode) {
		return (Node n1,
				Node n2) -> (int) (INT_FLOAT_COMPARATOR_ACCURACY * (testNode.distanceTo(n1) - testNode.distanceTo(n2)));
	}

	public static Comparator<NodeDistance> getNodeDistanceComparator() {
		return (NodeDistance n1,
				NodeDistance n2) -> (int) ((n1.getDistance() - n2.getDistance()) * INT_FLOAT_COMPARATOR_ACCURACY);
	}
}
