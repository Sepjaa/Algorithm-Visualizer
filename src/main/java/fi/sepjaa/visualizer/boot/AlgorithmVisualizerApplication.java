package fi.sepjaa.visualizer.boot;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point to the application.
 *
 * @author Jaakko
 *
 */
public class AlgorithmVisualizerApplication {

	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmVisualizerApplication.class);

	public static void main(String[] args) {
		System.setProperty("sun.java2d.uiScale", "1");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			LOG.warn("Look and feel setting failed", e);
		}
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DefaultConfig.class);
		// ctx.close();
	}
}
