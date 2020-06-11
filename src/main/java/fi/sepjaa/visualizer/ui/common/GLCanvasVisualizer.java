package fi.sepjaa.visualizer.ui.common;

import java.awt.Component;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

@SuppressWarnings("serial")
public abstract class GLCanvasVisualizer extends GLCanvas implements GLEventListener, AlgorithmVisualizer {

	public GLCanvasVisualizer() {
		super(CapabilitiesProvider.instance());
		this.addGLEventListener(this);
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

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// NOP, cleanup on destroy method
	}

	@Override
	public Component getComponent() {
		return this;
	}
}
