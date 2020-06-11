package fi.sepjaa.visualizer.ui.pathfinding;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_LINES;
import static com.jogamp.opengl.GL.GL_TRIANGLE_FAN;

import java.util.Map;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import fi.sepjaa.visualizer.pathfinding.ImmutablePathfindingData;
import fi.sepjaa.visualizer.pathfinding.Node;
import fi.sepjaa.visualizer.pathfinding.PathfindingData;
import fi.sepjaa.visualizer.ui.common.GLCanvasVisualizer;
import fi.sepjaa.visualizer.ui.common.UiConstants;

@SuppressWarnings("serial")
@Component
public class PathfindingVisualizer extends GLCanvasVisualizer {

	private static final Logger LOG = LoggerFactory.getLogger(PathfindingVisualizer.class);
	private float ratio = 1;

	private final Object lock = new Object();
	@GuardedBy("lock")
	private PathfindingData data;

	public void bind(PathfindingData data) {
		synchronized (lock) {
			this.data = data;
		}
	}

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

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate into the screen
		gl.glColor3f(0f, 0f, 0f);

		gl.glBegin(GL_LINES);
		for (Node n : nodes.values()) {
			n.getConnections().forEach(id -> {
				gl.glVertex2f(n.getX() * 2, n.getY() * 2);
				Node connection = nodes.get(id);
				gl.glVertex2f(connection.getX() * 2, connection.getY() * 2);
			});
		}
		gl.glEnd();
		for (Node n : nodes.values()) {
			drawNode(gl, n.getX() * 2, n.getY() * 2);
		}
	}

	void drawNode(GL2 gl, float x, float y) {
		gl.glBegin(GL_TRIANGLE_FAN);
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
