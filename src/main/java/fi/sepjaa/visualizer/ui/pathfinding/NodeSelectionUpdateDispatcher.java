package fi.sepjaa.visualizer.ui.pathfinding;

import org.springframework.stereotype.Component;

/**
 * Registers and updates {@link PathfindingNodeSelectionListener}s.
 *
 * @author Jaakko
 *
 */
@Component
public interface NodeSelectionUpdateDispatcher {

	void addListener(PathfindingNodeSelectionListener listener);

	void removeListener(PathfindingNodeSelectionListener listener);

}
