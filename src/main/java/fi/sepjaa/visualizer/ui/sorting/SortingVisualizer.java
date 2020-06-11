package fi.sepjaa.visualizer.ui.sorting;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

import javax.annotation.concurrent.GuardedBy;

import org.springframework.stereotype.Component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.ImmutableSortingData;
import fi.sepjaa.visualizer.ui.common.GLCanvasVisualizer;

/**
 * GLCanvas for drawing the array state, draws based on the currently binded
 * {@link SortingData}.
 *
 * @author Jaakko
 *
 */
@SuppressWarnings("serial")
@Component
public class SortingVisualizer extends GLCanvasVisualizer {

	private final Object lock = new Object();
	@GuardedBy("lock")
	private SortingData data;
	@GuardedBy("lock")
	private boolean drawMemory = false;

	public void bind(SortingData data) {
		synchronized (lock) {
			this.data = data;
		}
	}

	public void unBind() {
		synchronized (lock) {
			this.data = null;
		}
	}

	public void setDrawMemory(boolean drawMemory) {
		synchronized (lock) {
			this.drawMemory = drawMemory;
		}
	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		ImmutableSortingData copy;
		boolean drawMemoryCopy;
		synchronized (lock) {
			if (this.data == null) {
				return;
			}
			copy = this.data.getCopy();
			drawMemoryCopy = this.drawMemory;
		}
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate into the screen
		gl.glBegin(GL_QUADS);
		gl.glColor3f(0f, 0f, 0f);
		float width = 2f / copy.getLength();
		for (int i = 0; i < copy.getLength(); i++) {
			if (!drawMemoryCopy) {
				// Draw single array

				if (i == copy.getSwapIndexes()[0] || i == copy.getSwapIndexes()[1]) {
					gl.glColor3f(1f, 0f, 0f);
				}
				if (i == copy.getComparisonIndexes()[0] || i == copy.getComparisonIndexes()[1]) {
					gl.glColor3f(0f, 0f, 1f);
				}
				gl.glVertex2f(i * width, copy.getElements()[i] * 0.002f);
				gl.glVertex2f(i * width, 0);
				gl.glVertex2f((i + 1) * width, 0);
				gl.glVertex2f((i + 1) * width, copy.getElements()[i] * 0.002f);
				gl.glColor3f(0f, 0f, 0f);
			} else {
				// Draw two arrays

				if (i == copy.getSwapIndexes()[0] || i == copy.getSwapIndexes()[1]) {
					gl.glColor3f(1f, 0f, 0f);
				}
				if (i == copy.getComparisonIndexes()[0] || i == copy.getComparisonIndexes()[1]) {
					gl.glColor3f(0f, 0f, 1f);
				}
				gl.glVertex2f(i * width, copy.getElements()[i] * 0.001f);
				gl.glVertex2f(i * width, 0);
				gl.glVertex2f((i + 1) * width, 0);
				gl.glVertex2f((i + 1) * width, copy.getElements()[i] * 0.001f);
				gl.glColor3f(0f, 0f, 0f);

				if (i == copy.getSwapIndexes()[2] || i == copy.getSwapIndexes()[3]) {
					gl.glColor3f(1f, 0f, 0f);
				}
				if (i == copy.getComparisonIndexes()[2] || i == copy.getComparisonIndexes()[3]) {
					gl.glColor3f(0f, 0f, 1f);
				}

				gl.glVertex2f(i * width, 1 + copy.getElementsMemory()[i] * 0.001f);
				gl.glVertex2f(i * width, 1);
				gl.glVertex2f((i + 1) * width, 1);
				gl.glVertex2f((i + 1) * width, 1 + copy.getElementsMemory()[i] * 0.001f);
				gl.glColor3f(0f, 0f, 0f);
			}
		}
		gl.glEnd();
	}

}
