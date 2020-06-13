package fi.sepjaa.visualizer.ui.pathfinding.algorithm.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepjaa.visualizer.pathfinding.algorithm.PathfindingAlgorithm;

public abstract class AbstractPathfindingAlgorithm implements PathfindingAlgorithm {
	protected final Logger LOG;

	public AbstractPathfindingAlgorithm() {
		this.LOG = LoggerFactory.getLogger(getClass());
	}
}
