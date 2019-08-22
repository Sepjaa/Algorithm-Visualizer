package fi.sepja.sorting.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for algorithm containing the logic operations used in algorithm.
 * Logic operations are compare and swap. Operations use/manipulate the array of
 * shorts using two indexes.
 *
 *
 * @author Jaakko
 *
 */
public abstract class AbstractAlgorithm implements Algorithm {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractAlgorithm.class);
	private final int compareSleep;
	private final int swapSleep;

	private int[] lastSwapIndexes;
	private int[] lastComparisonIndexes;

	protected boolean swap = false;

	protected boolean memoryAndArraySwapped = false;

	protected AbstractAlgorithm() {
		this.compareSleep = 0;
		this.swapSleep = 0;
	}

	protected AbstractAlgorithm(int comparisonSleepMicros, int swapSleepMicros) {
		if (comparisonSleepMicros < 0) {
			LOG.error("Invalid compare sleep time {}, setting to 0", comparisonSleepMicros);
			this.compareSleep = 0;
		} else {
			this.compareSleep = comparisonSleepMicros;
		}
		if (swapSleepMicros < 0) {
			LOG.error("Invalid swap sleep time {}, setting to 0", swapSleepMicros);
			this.swapSleep = 0;
		} else {
			this.swapSleep = swapSleepMicros;
		}
	}

	protected abstract void sort(short[] array, short[] memoryArray);

	@Override
	public void sort(short[] array, short[] memoryArray, int[] lastComparisonIndexes, int[] lastSwapIndexes) {
		this.lastComparisonIndexes = lastComparisonIndexes;
		this.lastSwapIndexes = lastSwapIndexes;
		sort(array, memoryArray);
	}

	private void sleep(int micros) {
		if (micros == 0) {

		} else if (micros > 0) {
			final long start = System.nanoTime();
			if (micros >= 10000) {
				try {
					Thread.sleep(micros / 1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// Busy sleep to get accuracy over millisecond
			while (System.nanoTime() - start < micros * 1000) {

			}
		}
	}

	protected boolean lessThan(short[] array, int index1, int index2, boolean allowEqual) {
		lastComparison(index1, index2);
		sleep(compareSleep);
		if (allowEqual) {
			return array[index1] <= array[index2];
		}
		return array[index1] < array[index2];
	}

	protected boolean biggerThan(short[] array, int index1, int index2, boolean allowEqual) {
		lastComparison(index1, index2);
		sleep(compareSleep);
		if (allowEqual) {
			return array[index1] >= array[index2];
		}
		return array[index1] > array[index2];
	}

	protected void swap(short[] array, int index1, int index2) {
		if (index1 == index2) {
			return;
		}
		short temp = array[index1];
		array[index1] = array[index2];
		array[index2] = temp;
		lastSwap(index1, index2);
		sleep(swapSleep);
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

	protected void storeTemp(short[] array, int arrayIndex) {
		array[array.length - 1] = array[arrayIndex];
		lastSwap(array.length, arrayIndex);
		sleep(swapSleep);
	}

	protected void assignFromTemp(short[] array, int arrayIndex) {
		array[arrayIndex] = array[array.length - 1];
		lastSwap(array.length, arrayIndex);
		sleep(swapSleep);
	}

	protected void clearTemp(short[] array) {
		array[array.length - 1] = 0;
	}

	protected boolean lessThanTemp(short[] array, int arrayIndex, boolean allowEqual) {
		return lessThan(array, arrayIndex, array.length - 1, allowEqual);
	}

	protected boolean biggerThanTemp(short[] array, int arrayIndex, boolean allowEqual) {
		return biggerThan(array, arrayIndex, array.length - 1, allowEqual);
	}

	private void lastSwap(int index1, int index2) {
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

	private void lastComparison(int index1, int index2) {
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
