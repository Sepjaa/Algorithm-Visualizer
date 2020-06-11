package fi.sepjaa.visualizer.sorting.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for algorithms.
 *
 *
 * @author Jaakko
 *
 */
public abstract class AbstractAlgorithm implements SortingAlgorithm {
	protected final Logger LOG = LoggerFactory.getLogger(AbstractAlgorithm.class);

	@Override
	public boolean isFullMemoryBufferAlgorithm() {
		return false;
	}

	@Override
	public boolean isTempValueBufferAlgorithm() {
		return false;
	}
}
