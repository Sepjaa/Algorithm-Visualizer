package fi.sepja.sorting;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

import java.util.Arrays;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * GLCanvas for drawing the array state, uses references to diffrent arrays to
 * draw, which are binded with bind-methods.
 *
 * @author Jaakko
 *
 */
public class Visualizer extends GLCanvas implements GLEventListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 6146921870949093036L;

	private Object arrayDrawCopyLock = new Object();
	private short[] array = new short[1];
	private int[] lastSwapIndexes = { -1, -1 };
	private int[] lastComparisonIndexes = { -1, -1 };

	public Visualizer() {
		this.addGLEventListener(this);
	}

	public void bindArray(short[] array) {
		synchronized (arrayDrawCopyLock) {
			this.array = array;
		}
	}

	public void bindSwapAndCompareArray(int[] lastSwapIndexes, int[] lastComparisonIndexes) {
		synchronized (arrayDrawCopyLock) {
			this.lastComparisonIndexes = lastComparisonIndexes;
			this.lastSwapIndexes = lastSwapIndexes;
		}
	}

	public void unbindSwapAndCompareArray() {
		synchronized (arrayDrawCopyLock) {
			this.lastSwapIndexes = new int[2];
			lastSwapIndexes[0] = -1;
			lastSwapIndexes[1] = -1;
			this.lastComparisonIndexes = new int[2];
			lastComparisonIndexes[0] = -1;
			lastComparisonIndexes[1] = -1;
		}
	}

	@Override
	public void destroy() {
		this.removeGLEventListener(this);
		super.destroy();
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(255.0f, 255.0f, 255.0f, 0.0f);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2(); // get the OpenGL 2 graphics context
		gl.glViewport(0, 0, width, height);
	}

	/**
	 * Called back by the animator to perform rendering.
	 */
	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();

		gl.glTranslatef(-1f, -1f, 0f); // translate into the screen
		gl.glBegin(GL_QUADS);
		gl.glColor3f(0f, 0f, 0f);
		short[] drawCopy;
		int[] swapDrawCopy;
		int[] comparisonDrawCopy;
		synchronized (arrayDrawCopyLock) {
			drawCopy = Arrays.copyOf(array, array.length);
			comparisonDrawCopy = Arrays.copyOf(lastComparisonIndexes, 2);
			swapDrawCopy = Arrays.copyOf(lastSwapIndexes, 2);
		}
		float width = 2f / drawCopy.length;
		for (int i = 0; i < drawCopy.length; i++) {
			if (i == swapDrawCopy[0] || i == swapDrawCopy[1]) {
				gl.glColor3f(1f, 0f, 0f);
			}
			if (i == comparisonDrawCopy[0] || i == comparisonDrawCopy[1]) {
				gl.glColor3f(0f, 0f, 1f);
			}
			gl.glVertex2f(i * width, drawCopy[i] * 0.002f);
			gl.glVertex2f(i * width, 0);
			gl.glVertex2f((i + 1) * width, 0);
			gl.glVertex2f((i + 1) * width, drawCopy[i] * 0.002f);
			gl.glColor3f(0f, 0f, 0f);
		}
		gl.glEnd();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP, cleanup on destroy method
	}
}
