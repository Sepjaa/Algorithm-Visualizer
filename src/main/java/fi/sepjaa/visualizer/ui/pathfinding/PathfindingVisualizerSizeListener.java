package fi.sepjaa.visualizer.ui.pathfinding;

/**
 * Interface for registering objects that are interested in
 * {@link PathfindingVisualizer}s canvas size.
 *
 * @author Jaakko
 *
 */
public interface PathfindingVisualizerSizeListener {

	void sizeUpdated(int width, int height);

}
