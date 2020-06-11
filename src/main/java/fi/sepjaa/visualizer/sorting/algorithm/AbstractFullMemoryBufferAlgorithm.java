package fi.sepjaa.visualizer.sorting.algorithm;

/**
 * Base class for algorithms that utilise a separate memory array.
 *
 * @author Jaakko
 *
 */
public abstract class AbstractFullMemoryBufferAlgorithm extends AbstractAlgorithm {

	@Override
	public boolean isFullMemoryBufferAlgorithm() {
		return true;
	}
}
