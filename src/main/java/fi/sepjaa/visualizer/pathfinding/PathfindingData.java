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
 * Handles all the pathfinding sleep/visualization operations and contains all
 * the necessary data.
 *
 * @author Jaakko
 *
 */
public class PathfindingData {
	private static final Logger LOG = LoggerFactory.getLogger(PathfindingData.class);

	private final Object lock = new Object();
	@GuardedBy("lock")
	private ImmutableMap<Long, Node> nodes;
	@GuardedBy("lock")
	private long start = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private long end = CommonConstants.NO_STATEMENT;
	@GuardedBy("lock")
	private List<Long> path = ImmutableList.of();
	@GuardedBy("lock")
	private long measurementSleep;
	@GuardedBy("lock")
	private long evaluationSleep;
	@GuardedBy("lock")
	private ConnectedNodePair nextMeasurement;
	@GuardedBy("lock")
	private List<Long> evaluatedNodes = new ArrayList<>();

	public PathfindingData(long nodeCount, long connectionsCount) {
		this(nodeCount, connectionsCount, CommonConstants.DEFAULT_MEASUREMENT_SLEEP,
				CommonConstants.DEFAULT_EVALUATION_SLEEP);
	}

	public PathfindingData(long nodeCount, long connectionsCount, long measurementSleep, long evaluationSleep) {
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
		this.measurementSleep = measurementSleep;
		this.evaluationSleep = evaluationSleep;
		LOG.info("Constructed {}", this);
	}

	public ImmutablePathfindingData getCopy() {
		synchronized (lock) {
			return new ImmutablePathfindingData(nodes, start, end, measurementSleep, evaluationSleep, path,
					Optional.ofNullable(nextMeasurement), evaluatedNodes);
		}
	}

	private void setStart(long start) {
		synchronized (lock) {
			this.start = start;
		}
	}

	private void setEnd(long end) {
		synchronized (lock) {
			this.end = end;
		}
	}

	public void setSelected(long id) {
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

	public void setPath(List<Long> path) {
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

	public void setMeasurementSleep(long amount) {
		LOG.info("Updating measurement sleep to {}", amount);
		synchronized (lock) {
			this.measurementSleep = amount;
		}
	}

	public void setEvaluationSleep(long amount) {
		LOG.info("Updating evaluation sleep to {}", amount);
		synchronized (lock) {
			this.evaluationSleep = amount;
		}
	}

	public float measure(ConnectedNodePair pair) {
		long measurementSleep;
		synchronized (lock) {
			this.nextMeasurement = pair;
			measurementSleep = this.measurementSleep;
		}
		Utils.sleep(measurementSleep);
		return pair.getDistance();
	}

	public ImmutableList<Long> addAndReturnEvaluated(long id, Map<Long, NodeDistance> distances) {
		long evaluationSleep;
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

	public void extractPath(Map<Long, NodeDistance> distances, long start, long end) {
		List<Long> result = new LinkedList<>();
		long current = end;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + ((evaluatedNodes == null) ? 0 : evaluatedNodes.hashCode());
		result = prime * result + (int) (evaluationSleep ^ (evaluationSleep >>> 32));
		result = prime * result + ((lock == null) ? 0 : lock.hashCode());
		result = prime * result + (int) (measurementSleep ^ (measurementSleep >>> 32));
		result = prime * result + ((nextMeasurement == null) ? 0 : nextMeasurement.hashCode());
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
		PathfindingData other = (PathfindingData) obj;
		if (end != other.end) {
			return false;
		}
		if (evaluatedNodes == null) {
			if (other.evaluatedNodes != null) {
				return false;
			}
		} else if (!evaluatedNodes.equals(other.evaluatedNodes)) {
			return false;
		}
		if (evaluationSleep != other.evaluationSleep) {
			return false;
		}
		if (lock == null) {
			if (other.lock != null) {
				return false;
			}
		} else if (!lock.equals(other.lock)) {
			return false;
		}
		if (measurementSleep != other.measurementSleep) {
			return false;
		}
		if (nextMeasurement == null) {
			if (other.nextMeasurement != null) {
				return false;
			}
		} else if (!nextMeasurement.equals(other.nextMeasurement)) {
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
		return "PathfindingData [lock=" + lock + ", nodes=" + nodes + ", start=" + start + ", end=" + end + ", path="
				+ path + ", measurementSleep=" + measurementSleep + ", evaluationSleep=" + evaluationSleep
				+ ", nextMeasurement=" + nextMeasurement + ", evaluatedNodes=" + evaluatedNodes + "]";
	}

}