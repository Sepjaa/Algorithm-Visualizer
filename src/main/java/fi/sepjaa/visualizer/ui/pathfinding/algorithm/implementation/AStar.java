package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.NodeDistance;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;

@Component
public class AStar extends Dijkstra {

	@Override
	public Type getType() {
		return Type.A_STAR;
	}

	@Override
	protected float evaluateNeighbour(PathfindingData data, ImmutablePathfindingData copy, int nodeId, int neighbourId,
			Map<Integer, NodeDistance> distances) {
		return super.evaluateNeighbour(data, copy, nodeId, neighbourId, distances) + distanceToEnd(copy, neighbourId);
	}

	private float distanceToEnd(ImmutablePathfindingData copy, int neighbourId) {
		ImmutableMap<Integer, Node> nodes = copy.getNodes();
		Node neighbour = nodes.get(neighbourId);
		Node end = nodes.get(copy.getEnd());
		return neighbour.distanceTo(end);
	}

}
