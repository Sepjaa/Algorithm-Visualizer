package fi.sepjaa.visualizer.ui.common;

import java.awt.Component;

import com.jogamp.opengl.GLAutoDrawable;

/**
 * Common interface for components that visualize algorithms.
 *
 * @author Jaakko
 *
 */
public interface AlgorithmVisualizer extends GLAutoDrawable {

	Component getComponent();

}
