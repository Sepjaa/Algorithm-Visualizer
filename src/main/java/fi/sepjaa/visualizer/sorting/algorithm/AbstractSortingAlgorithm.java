package fi.sepjaa.visualizer.sorting.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSortingAlgorithm implements SortingAlgorithm {
	protected final Logger LOG;

	public AbstractSortingAlgorithm() {
		this.LOG = LoggerFactory.getLogger(getClass());
	}

	@Override
	public boolean isFullMemoryBufferAlgorithm() {
		return false;
	}

	@Override
	public boolean isTempValueBufferAlgorithm() {
		return false;
	}
}
