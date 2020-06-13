package fi.sepjaa.visualizer.common;

public interface AlgorithmExecutionEventDispatcher {

	void addListener(AlgorithmExecutionListener listener);

	void removeListener(AlgorithmExecutionListener listener);

}
