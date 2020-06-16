package fi.sepjaa.visualizer.ui.pathfinding;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLE_FAN;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLAutoDrawable;

import fi.sepjaa.visualizer.pathfinding.ConnectedNodePair;
import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.ui.common.GLCanvasVisualizer;
import fi.sepjaa.visualizer.ui.common.UiConstants;

@SuppressWarnings("serial")
@Component
public class PathfindingVisualizer extends GLCanvasVisualizer implements PathfindingDataListener {

	private static final Logger LOG = LoggerFactory.getLogger(PathfindingVisualizer.class);
	private final ImmutableList<PathfindingVisualizerMouseListener> mouseListeners;
	private final ImmutableList<PathfindingVisualizerSizeListener> sizeListeners;

	private final Object lock = new Object();
	@GuardedBy("lock")
	private PathfindingData data;

	@Autowired
	public PathfindingVisualizer(List<PathfindingVisualizerMouseListener> mouseListeners,
			List<PathfindingVisualizerSizeListener> sizeListeners) {
		super();
		this.mouseListeners = ImmutableList.copyOf(mouseListeners);
		this.sizeListeners = ImmutableList.copyOf(sizeListeners);
	}

	@PostConstruct
	public void init() {
		this.mouseListeners.forEach(listener -> this.addMouseListener(listener));
	}

	@Override
	public void bind(PathfindingData data) {
		synchronized (lock) {
			this.data = data;
		}
	}

	@Override
	public void unBind() {
		synchronized (lock) {
			this.data = null;
		}
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		super.reshape(drawable, x, y, width, height);
		this.sizeListeners.forEach(sizeListener -> sizeListener.sizeUpdated(width, height));
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		ImmutablePathfindingData copy;
		synchronized (lock) {
			if (this.data == null) {
				return;
			}
			copy = this.data.getCopy();
		}

		Map<Long, Node> nodes = copy.getNodes();
		List<Long> path = copy.getPath();
		Optional<ConnectedNodePair> measurement = copy.getMeasurement();
		List<Long> evaluated = copy.getEvaluated();

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate so (0,0) is in the left bottom
		// corner and (2,2) in right top corner
		gl.glColor3f(0f, 0f, 0f);

		for (Node it : nodes.values()) {
			it.getConnections().forEach(connectionId -> {
				Node connectionTarget = nodes.get(connectionId);
				drawLine(gl, 2 * it.getX(), 2 * connectionTarget.getX(), 2 * it.getY(), 2 * connectionTarget.getY(),
						measurement.isPresent() && (measurement.get().is(it.getId(), connectionId)),
						path.contains(it.getId()) && path.contains(connectionId),
						evaluated.contains(it.getId()) && evaluated.contains(connectionId));
			});
		}
		for (Node it : nodes.values()) {
			drawNode(gl, it.getX() * 2, it.getY() * 2, it.getId() == copy.getStart(), it.getId() == copy.getEnd(),
					path.contains(it.getId()), evaluated.contains(it.getId()));
		}
	}

	/**
	 * Open GL default lines suck so draw connections as a rectangle.
	 */
	private void drawLine(GL2 gl, float x1, float x2, float y1, float y2, boolean measured, boolean onPath,
			boolean evaluated) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float[] vectorised = new float[2];
		float[] perpendicular = new float[2];
		vectorised[0] = dx;
		vectorised[1] = dy;
		perpendicular[0] = vectorised[1];
		perpendicular[1] = -vectorised[0];
		float length = perpendicular[0] * perpendicular[0] + perpendicular[1] * perpendicular[1];
		length = (float) Math.sqrt(length);
		perpendicular[0] /= length;
		perpendicular[1] /= length;

		gl.glBegin(GL2ES3.GL_QUADS);
		gl.glPushMatrix();
		if (measured) {
			gl.glColor3f(0f, 0f, 1f);
		} else if (onPath) {
			gl.glColor3f(0f, 1f, 0f);
		} else if (evaluated) {
			gl.glColor3f(1f, 0f, 0f);
		} else {
			gl.glColor3f(0f, 0f, 0f);
		}
		// TODO: ratio/viewport fixes?
		if (getRatio() > 1) {
			gl.glVertex2f(x1 + perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH / getRatio(),
					y1 + perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH);
			gl.glVertex2f(x1 - perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH / getRatio(),
					y1 - perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH);
			gl.glVertex2f(x1 + vectorised[0] - perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH / getRatio(),
					y1 + vectorised[1] - perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH);
			gl.glVertex2f(x1 + vectorised[0] + perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH / getRatio(),
					y1 + vectorised[1] + perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH);
		} else {
			gl.glVertex2f(x1 + perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH,
					y1 + perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH * getRatio());
			gl.glVertex2f(x1 - perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH,
					y1 - perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH * getRatio());
			gl.glVertex2f(x1 + vectorised[0] - perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH,
					y1 + vectorised[1] - perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH * getRatio());
			gl.glVertex2f(x1 + vectorised[0] + perpendicular[0] * UiConstants.NODE_CONNECTION_WIDTH,
					y1 + vectorised[1] + perpendicular[1] * UiConstants.NODE_CONNECTION_WIDTH * getRatio());
		}
		gl.glPopMatrix();
		gl.glEnd();
	}

	/**
	 * Open GL default points suck so draw nodes as a triangle fan forming a circle.
	 */
	private void drawNode(GL2 gl, float x, float y, boolean start, boolean end, boolean onPath, boolean evaluated) {
		gl.glBegin(GL_TRIANGLE_FAN);
		if (onPath) {
			gl.glColor3f(0f, 1f, 0f);
		} else if (start || evaluated) {
			gl.glColor3f(1f, 0f, 0f);
		} else if (end) {
			gl.glColor3f(0f, 0f, 1f);
		} else {
			gl.glColor3f(0f, 0f, 0f);
		}
		gl.glVertex2f(x, y);
		for (int i = 0; i < UiConstants.NODE_DRAW_SEGMENTS + 1; i++) {
			float angle = 2f * (float) Math.PI * (float) i / (float) UiConstants.NODE_DRAW_SEGMENTS;
			// TODO: ratio/viewport fixes?
			if (getRatio() > 1) {
				gl.glVertex2f(x + UiConstants.NODE_RADIUS * (float) Math.cos(angle) / getRatio(),
						y + UiConstants.NODE_RADIUS * (float) Math.sin(angle));
			} else {
				gl.glVertex2f(x + UiConstants.NODE_RADIUS * (float) Math.cos(angle),
						y + UiConstants.NODE_RADIUS * (float) Math.sin(angle) * getRatio());
			}
		}
		gl.glEnd();
	}

}
