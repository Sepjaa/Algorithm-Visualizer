package fi.sepjaa.visualizer.ui.common;

import java.awt.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

@SuppressWarnings("serial")
public abstract class GLCanvasVisualizer extends GLCanvas implements GLEventListener, AlgorithmVisualizer {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private float ratio = 1;

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
		GL2 gl = drawable.getGL().getGL2();
		if (height == 0) {
			height = 1;
		}
		// -Dsun.java2d.uiScale=1 because display scaling messes up the openGL area.
		gl.glViewport(x, y, width, height);
		this.ratio = (float) width / height;
		LOG.info("Reshaped to width {},height {}, ratio {}", width, height, this.ratio);
	}

	public float getRatio() {
		return ratio;
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
