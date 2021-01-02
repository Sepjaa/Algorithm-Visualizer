package fi.sepjaa.visualizer.ui.common;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JViewport;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

//@Component
public class TestWindow extends JFrame implements GLEventListener {
	public TestWindow() {
		super("Kek");
		setLayout(new FlowLayout());
		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		final GLCanvas glcanvas = new GLCanvas(new GLCapabilities(profile));
		glcanvas.addGLEventListener(this);
		glcanvas.setSize(1000, 1000);

		JViewport port = new JViewport();
		port.setView(glcanvas);
		// port.setExtentSize(new Dimension(350, 350));
		port.setViewPosition(new Point(0, 200));
		// port.
		port.setPreferredSize(new Dimension(800, 800));
		add(port);
		// add(glcanvas);
		// setSize(400, 400);
		pack();
		setVisible(true);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();

		gl.glBegin(GL2.GL_LINES);// static field
		gl.glVertex3f(-0.99f, -0.99f, 0);
		gl.glVertex3f(0.99f, 0.99f, 0);

		gl.glVertex3f(0.99f, -0.99f, 0);
		gl.glVertex3f(-0.99f, 0.99f, 0);

		gl.glVertex3f(-0.99f, 0.99f, 0);
		gl.glVertex3f(-0.99f, -0.99f, 0);

		gl.glVertex3f(0.99f, 0.99f, 0);
		gl.glVertex3f(0.99f, -0.99f, 0);

		gl.glVertex3f(-0.99f, -0.99f, 0);
		gl.glVertex3f(0.99f, -0.99f, 0);

		gl.glVertex3f(-0.99f, 0.99f, 0);
		gl.glVertex3f(0.99f, 0.99f, 0);

		gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// TODO Auto-generated method stub

	}
}
