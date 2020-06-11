package fi.sepjaa.visualizer.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepjaa.visualizer.common.CommonConstants;

/**
 * Handles all the pathfinding execution and creation/distribution of nodes.
 *
 * @author Jaakko
 *
 */
public class PathfindingData {
	private static final Logger LOG = LoggerFactory.getLogger(PathfindingData.class);

	private final Object lock = new Object();
	@GuardedBy("lock")
	private Map<Integer, Node> nodes;

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
		this.nodes = new HashMap<>();
		for (int i = 0; i < nodeCount; i++) {
			this.nodes.put(i, new Node(i));
		}
		for (int i = 0; i < connectionsCount; i++) {
			Node node = this.nodes.get(i % nodeCount);
			List<Node> nodeList = new ArrayList<>(this.nodes.values());
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
	}

	public ImmutablePathfindingData getCopy() {
		synchronized (lock) {
			return new ImmutablePathfindingData(nodes);
		}
	}
}