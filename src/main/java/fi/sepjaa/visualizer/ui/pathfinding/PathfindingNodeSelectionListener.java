package fi.sepjaa.visualizer.ui.pathfinding;

import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;

/**
 * Interface for objects interested in current pathfinding node selections.
 * 
 * @author Jaakko
 *
 */
public interface PathfindingNodeSelectionListener {

	void selectionChanged(ImmutablePathfindingData newData);

}
