package fi.sepjaa.visualizer.ui.pathfinding;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.common.AlgorithmExecutor;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.NodeUtilities;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.ui.common.UiConstants;

/**
 * Handles the selection of target nodes for pathfind.
 *
 * @author Jaakko
 *
 */
@Component
public class NodeSelectionManager implements PathfindingVisualizerMouseListener, PathfindingVisualizerSizeListener,
		PathfindingDataListener, NodeSelectionUpdateDispatcher {
	private static final Logger LOG = LoggerFactory.getLogger(NodeSelectionManager.class);

	private final List<PathfindingNodeSelectionListener> listeners = new CopyOnWriteArrayList<>();
	private final AlgorithmExecutor executor;

	@Autowired
	public NodeSelectionManager(AlgorithmExecutor executor) {
		this.executor = executor;
	}

	private int width;
	private int height;
	private PathfindingData data;

	@Override
	public synchronized void bind(PathfindingData data) {
		this.data = data;
		onSelectionChange();
	}

	@Override
	public synchronized void unBind() {
		this.data = null;
	}

	@Override
	public synchronized void mouseClicked(MouseEvent e) {
		// NOP
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// NOP
	}

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		if (executor.isExecuting()) {
			executor.stop();
		}
		Point jCoordinates = e.getPoint();
		float glX = jCoordinates.x / (float) width;
		float glY = (height - jCoordinates.y) / (float) height;
		LOG.info("Clicked point {} resulting in x {}, y {}", e.getPoint(), glX, glY);
		if (data != null) {
			Optional<Node> nodeCloseEnough = NodeUtilities
					.getClosestNode(glX, glY, this.data.getCopy().getNodes().values())
					.filter(n -> new Node(-1, glX, glY).distanceTo(n) <= UiConstants.SELECTION_THRESHOLD);
			LOG.info("Node close enough {}", nodeCloseEnough);
			if (nodeCloseEnough.isPresent()) {
				this.data.setSelected(nodeCloseEnough.get().getId());
				onSelectionChange();
			} else {
				this.data.clearSelections();
				onSelectionChange();
			}
		}
	}

	private synchronized void onSelectionChange() {
		this.listeners.forEach(l -> l.selectionChanged(data.getCopy()));
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// NOP
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// NOP
	}

	@Override
	public synchronized void sizeUpdated(int width, int height) {
		LOG.info("Updated width {}, height h {}", width, height);
		this.height = height;
		this.width = width;
	}

	@Override
	public void addListener(PathfindingNodeSelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(PathfindingNodeSelectionListener listener) {
		if (!listeners.remove(listener)) {
			LOG.warn("Tried to remove listener {} but it was not found.");
		}

	}

}
