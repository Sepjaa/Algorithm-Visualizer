package fi.sepjaa.visualizer.sorting.algorithm;

/**
 * Base class for algorithms that utilise a temporary value storage.
 *
 * @author Jaakko
 *
 */
public abstract class AbstractTempValueBufferAlgorithm extends AbstractSortingAlgorithm {

	@Override
	public boolean isTempValueBufferAlgorithm() {
		return true;
	}
}
