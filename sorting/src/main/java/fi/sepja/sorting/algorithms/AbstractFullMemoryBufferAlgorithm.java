package fi.sepja.sorting.algorithms;

import java.util.Optional;

/**
 * Base class for algorithms that utilise a separate memory array.
 *
 * @author Jaakko
 *
 */
public abstract class AbstractFullMemoryBufferAlgorithm extends AbstractAlgorithm {

	protected boolean swap = false;

	public AbstractFullMemoryBufferAlgorithm() {
		super();
	}

	public AbstractFullMemoryBufferAlgorithm(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public boolean isFullMemoryBufferAlgorithm() {
		return true;
	}

	@Override
	protected void sort(short[] array) {
		// NOP, sorting is done with memory array
	}

	protected abstract void sort(short[] array, short[] memoryArray);

	@Override
	public void sort(short[] array, Optional<short[]> memoryArray, int[] lastComparisonIndexes, int[] lastSwapIndexes) {
		if (!memoryArray.isPresent()) {
			LOG.error("Full memory buffer algotrithm {} type {} called without memory array!", this, getType());
			return;
		}
		this.lastComparisonIndexes = lastComparisonIndexes;
		this.lastSwapIndexes = lastSwapIndexes;
		sort(array, memoryArray.get());
	}

	protected void copyArray(short[] array, short[] memoryArray) {
		for (int i = 0; i < array.length; i++) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			memoryArray[i] = array[i];
			lastSwap(i, i);
			sleep(swapSleep);
		}
	}

	/**
	 * Copies index value from array to memoryArray.
	 */
	protected void copyIndexValue(short[] array, short[] memoryArray, int arrayIndex, int memoryIndex) {
		memoryArray[memoryIndex] = array[arrayIndex];
		lastSwap(memoryIndex, memoryIndex);
		sleep(swapSleep);
	}

	@Override
	protected void lastSwap(int index1, int index2) {
		if (swap) {
			lastSwapIndexes[0] = -1;
			lastSwapIndexes[1] = -1;
			lastSwapIndexes[2] = index1;
			lastSwapIndexes[3] = index2;
		} else {
			lastSwapIndexes[0] = index1;
			lastSwapIndexes[1] = index2;
			lastSwapIndexes[2] = -1;
			lastSwapIndexes[3] = -1;
		}
		lastComparisonIndexes[0] = -1;
		lastComparisonIndexes[1] = -1;
		lastComparisonIndexes[2] = -1;
		lastComparisonIndexes[3] = -1;
	}

	@Override
	protected void lastComparison(int index1, int index2) {
		if (swap) {
			lastComparisonIndexes[0] = -1;
			lastComparisonIndexes[1] = -1;
			lastComparisonIndexes[2] = index1;
			lastComparisonIndexes[3] = index2;
		} else {
			lastComparisonIndexes[0] = index1;
			lastComparisonIndexes[1] = index2;
			lastComparisonIndexes[2] = -1;
			lastComparisonIndexes[3] = -1;
		}
		lastSwapIndexes[0] = -1;
		lastSwapIndexes[1] = -1;
		lastSwapIndexes[2] = -1;
		lastSwapIndexes[3] = -1;
	}
}
