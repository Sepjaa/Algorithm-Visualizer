package fi.sepjaa.visualizer.ui.pathfinding;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_TRIANGLE_FAN;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.ui.common.GLCanvasVisualizer;
import fi.sepjaa.visualizer.ui.common.UiConstants;

@SuppressWarnings("serial")
@Component
public class PathfindingVisualizer extends GLCanvasVisualizer implements PathfindingDataAware {

	private static final Logger LOG = LoggerFactory.getLogger(PathfindingVisualizer.class);
	private float ratio = 1;
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
		GL2 gl = drawable.getGL().getGL2();

		if (height == 0) {
			height = 1;
		}
		// lambda effectively final blaa..
		final int finalHeight = height;

		this.sizeListeners.forEach(sizeListener -> sizeListener.sizeUpdated(width, finalHeight));

		gl.glViewport(x, y, width, height);
		this.ratio = (float) width / height;
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

		Map<Integer, Node> nodes = copy.getNodes();
		List<Integer> path = copy.getPath();

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate into the screen
		gl.glColor3f(0f, 0f, 0f);

		gl.glBegin(GL_LINES);
		for (Node n : nodes.values()) {
			n.getConnections().forEach(id -> {
				if (path.contains(n.getId()) && path.contains(id)) {
					gl.glColor3f(0f, 1f, 0f);
				} else {
					gl.glColor3f(0f, 0f, 0f);
				}
				gl.glVertex2f(n.getX() * 2, n.getY() * 2);
				Node connection = nodes.get(id);
				gl.glVertex2f(connection.getX() * 2, connection.getY() * 2);
			});
		}
		gl.glEnd();
		for (Node n : nodes.values()) {
			drawNode(gl, n.getX() * 2, n.getY() * 2, n.getId() == copy.getStart(), n.getId() == copy.getEnd(),
					path.contains(n.getId()));
		}
	}

	void drawNode(GL2 gl, float x, float y, boolean start, boolean end, boolean onPath) {
		gl.glBegin(GL_TRIANGLE_FAN);
		if (onPath) {
			gl.glColor3f(0f, 1f, 0f);
		}
		if (start) {
			gl.glColor3f(1f, 0f, 0f);
		} else if (end) {
			gl.glColor3f(0f, 0f, 1f);
		} else if (!onPath) {
			gl.glColor3f(0f, 0f, 0f);
		}
		{
			gl.glVertex2f(x, y);
			for (int i = 0; i < UiConstants.NODE_DRAW_SEGMENTS + 1; i++) {
				float angle = 2f * (float) Math.PI * (float) i / (float) UiConstants.NODE_DRAW_SEGMENTS;
				if (ratio > 1) {
					gl.glVertex2f(x + UiConstants.NODE_RADIUS * (float) Math.cos(angle) / ratio,
							y + UiConstants.NODE_RADIUS * (float) Math.sin(angle));
				} else {
					gl.glVertex2f(x + UiConstants.NODE_RADIUS * (float) Math.cos(angle),
							y + UiConstants.NODE_RADIUS * (float) Math.sin(angle) * ratio);
				}
			}
		}
		gl.glEnd();
	}

}
