package fi.sepjaa.visualizer.pathfinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.common.Utils;

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
	private ImmutableMap<Integer, Node> nodes;
	@GuardedBy("lock")
	private int start = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private int end = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private List<Integer> path = ImmutableList.of();
	@GuardedBy("lock")
	private int measurementSleep;
	@GuardedBy("lock")
	private int evaluationSleep;
	@GuardedBy("lock")
	private ConnectedNodePair nextMeasurement;
	@GuardedBy("lock")
	private List<Integer> evaluatedNodes = new ArrayList<>();

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
		this.nodes = NodeUtilities.spawnNodes(nodeCount, connectionsCount);

	}

	public ImmutablePathfindingData getCopy() {
		synchronized (lock) {
			return new ImmutablePathfindingData(nodes, start, end, path, Optional.ofNullable(nextMeasurement),
					evaluatedNodes);
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
			clearPath();
		}
	}

	public void setPath(List<Integer> path) {
		synchronized (lock) {
			this.nextMeasurement = null;
			this.path = ImmutableList.copyOf(path);
		}
	}

	public void clearPath() {
		synchronized (lock) {
			this.path = ImmutableList.of();
		}
	}

	public void setMeasurementSleep(int amount) {
		synchronized (lock) {
			this.measurementSleep = amount;
		}
	}

	public void setEvaluationSleep(int amount) {
		synchronized (lock) {
			this.evaluationSleep = amount;
		}
	}

	public float measure(ConnectedNodePair pair) {
		int measurementSleep;
		synchronized (lock) {
			this.nextMeasurement = pair;
			measurementSleep = this.measurementSleep;
		}
		Utils.sleep(measurementSleep);
		return pair.getDistance();
	}

	public ImmutableList<Integer> addAndReturnEvaluated(Integer id, Map<Integer, NodeDistance> distances) {
		int evaluationSleep;
		synchronized (lock) {
			this.evaluatedNodes.add(id);
			extractPath(distances, this.start, id);
			evaluationSleep = this.evaluationSleep;
		}
		Utils.sleep(evaluationSleep);
		synchronized (lock) {
			return ImmutableList.copyOf(evaluatedNodes);
		}
	}

	public void extractPath(Map<Integer, NodeDistance> distances, int start, int end) {
		List<Integer> result = new LinkedList<>();
		int current = end;
		while (current != start) {
			result.add(current);
			current = distances.get(current).getPredecessor();
		}
		result.add(start);
		setPath(result);
	}

	public void clearMidFindInfo() {
		synchronized (lock) {
			if (!evaluatedNodes.contains(end)) {
				// Path was not found
				clearPath();
			}
			this.nextMeasurement = null;
			this.evaluatedNodes = new ArrayList<>();
		}
	}

}