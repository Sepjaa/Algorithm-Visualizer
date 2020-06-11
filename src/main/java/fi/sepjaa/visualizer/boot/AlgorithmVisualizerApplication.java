package fi.sepjaa.visualizer.boot;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Entry point to the application.
 *
 * @author Jaakko
 *
 */
public class AlgorithmVisualizerApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(DefaultConfig.class);
		// ctx.close();
	}
}
