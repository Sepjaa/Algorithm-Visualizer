package fi.sepjaa.visualizer.ui.pathfinding;

import fi.sepjaa.visualizer.pathfinding.PathfindingData;

/**
 * Interface for registering objects that are interested in the current
 * {@link PathfindingData}.
 *
 * @author Jaakko
 *
 */
public interface PathfindingDataListener {

	void bind(PathfindingData data);

	void unBind();

}
