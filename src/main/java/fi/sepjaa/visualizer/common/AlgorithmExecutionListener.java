package fi.sepjaa.visualizer.common;

/**
 * Interface for dispatching events related to algorithm execution. Should
 * always be called from the single "AlgorithmExecutorThread".
 *
 * @author Jaakko
 *
 */
public interface AlgorithmExecutionListener {

	/**
	 * Algorithm execution was started.
	 */
	void onStart();

	/**
	 * Algorithm execution finished or stopped (canceled).
	 */
	void onEnd(boolean canceled);

}
