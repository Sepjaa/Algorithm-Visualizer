package fi.sepjaa.visualizer.pathfinding.algorithm;

import fi.sepjaa.visualizer.pathfinding.PathfindingData;

/**
 * Interface to access pathfinding algorithm functions.
 *
 * @author Jaakko
 *
 */
public interface PathfindingAlgorithm {

	void find(PathfindingData data);

	Type getType();

	public enum Type {
		DIJKSTRA("Dijkstra"), A_STAR("A-star");

		private String value;

		Type(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
