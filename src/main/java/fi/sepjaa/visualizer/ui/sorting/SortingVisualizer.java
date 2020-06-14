package fi.sepjaa.visualizer.ui.sorting;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

import javax.annotation.concurrent.GuardedBy;

import org.springframework.stereotype.Component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.SortingOperationIndexes;
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
public class SortingVisualizer extends GLCanvasVisualizer implements SortingDataListener {
	private static final float BAR_MAX_HEIGHT = 0.002f;
	private final Object lock = new Object();
	@GuardedBy("lock")
	private SortingData data;
	@GuardedBy("lock")
	private boolean drawMemory = false;

	@Override
	public void bind(SortingData data) {
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

		SortingOperationIndexes swaps = copy.getSwapIndexes();
		SortingOperationIndexes comparisons = copy.getComparisonIndexes();

		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate into the screen
		gl.glBegin(GL_QUADS);
		gl.glColor3f(0f, 0f, 0f);
		float width = 2f / copy.getLength();
		for (int i = 0; i < copy.getLength(); i++) {
			float arrayHeight = BAR_MAX_HEIGHT;
			if (drawMemoryCopy) {
				arrayHeight /= 2;
			}
			if (swaps.inArray(i)) {
				gl.glColor3f(1f, 0f, 0f);
			} else if (comparisons.inArray(i)) {
				gl.glColor3f(0f, 0f, 1f);
			}
			gl.glVertex2f(i * width, copy.getElements()[i] * arrayHeight);
			gl.glVertex2f(i * width, 0);
			gl.glVertex2f((i + 1) * width, 0);
			gl.glVertex2f((i + 1) * width, copy.getElements()[i] * arrayHeight);
			gl.glColor3f(0f, 0f, 0f);

			if (drawMemoryCopy) {
				// Draw two arrays
				if (swaps.inArray(i)) {
					gl.glColor3f(1f, 0f, 0f);
				} else if (comparisons.inArray(i)) {
					gl.glColor3f(0f, 0f, 1f);
				}
				gl.glVertex2f(i * width, copy.getElements()[i] * arrayHeight);
				gl.glVertex2f(i * width, 0);
				gl.glVertex2f((i + 1) * width, 0);
				gl.glVertex2f((i + 1) * width, copy.getElements()[i] * arrayHeight);
				gl.glColor3f(0f, 0f, 0f);

				if (swaps.inMemory(i)) {
					gl.glColor3f(1f, 0f, 0f);
				} else if (comparisons.inMemory(i)) {
					gl.glColor3f(0f, 0f, 1f);
				}

				gl.glVertex2f(i * width, 1 + copy.getElementsMemory()[i] * arrayHeight);
				gl.glVertex2f(i * width, 1);
				gl.glVertex2f((i + 1) * width, 1);
				gl.glVertex2f((i + 1) * width, 1 + copy.getElementsMemory()[i] * arrayHeight);
				gl.glColor3f(0f, 0f, 0f);
			}

		}
		gl.glEnd();
	}

}
