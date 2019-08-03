package fi.sepja.sorting;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL2ES3.GL_QUADS;

import java.util.Arrays;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * GLCanvas for drawing the array state.
 *
 * @author Jaakko
 *
 */
public class Visualizer extends GLCanvas implements GLEventListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 6146921870949093036L;
	private Object arrayLock = new Object();
	private short[] array;

	public Visualizer() {
		this.addGLEventListener(this);
	}

	public void bindToArray(short[] array) {
		synchronized (arrayLock) {
			this.array = array;
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
		synchronized (arrayLock) {
			drawCopy = Arrays.copyOf(array, array.length);
		}
		float width = 2f / drawCopy.length;
		for (int i = 0; i < drawCopy.length; i++) {
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
