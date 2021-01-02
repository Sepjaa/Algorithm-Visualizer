package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;

@Component
public class Dijkstra extends AStar {

	@Override
	protected float distanceToEnd(ImmutablePathfindingData copy, long neighbourId) {
		return 0;
	}

	@Override
	public Type getType() {
		return Type.DIJKSTRA;
	}
}
