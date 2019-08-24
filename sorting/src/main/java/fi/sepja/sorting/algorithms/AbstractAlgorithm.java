package fi.sepja.sorting.algorithms;

import java.util.Optional;

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
	protected final Logger LOG = LoggerFactory.getLogger(AbstractAlgorithm.class);
	protected final int compareSleep;
	protected final int swapSleep;

	protected int[] lastSwapIndexes;
	protected int[] lastComparisonIndexes;

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

	protected abstract void sort(short[] array);

	@Override
	public void sort(short[] array, Optional<short[]> memoryArray, int[] lastComparisonIndexes, int[] lastSwapIndexes) {
		this.lastComparisonIndexes = lastComparisonIndexes;
		this.lastSwapIndexes = lastSwapIndexes;
		sort(array);
	}

	protected void sleep(int micros) {
		if (micros > 0) {
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

	protected void lastSwap(int index1, int index2) {
		lastSwapIndexes[0] = index1;
		lastSwapIndexes[1] = index2;
		lastSwapIndexes[2] = -1;
		lastSwapIndexes[3] = -1;
		lastComparisonIndexes[0] = -1;
		lastComparisonIndexes[1] = -1;
		lastComparisonIndexes[2] = -1;
		lastComparisonIndexes[3] = -1;
	}

	protected void lastComparison(int index1, int index2) {
		lastComparisonIndexes[0] = index1;
		lastComparisonIndexes[1] = index2;
		lastComparisonIndexes[2] = -1;
		lastComparisonIndexes[3] = -1;
		lastSwapIndexes[0] = -1;
		lastSwapIndexes[1] = -1;
		lastSwapIndexes[2] = -1;
		lastSwapIndexes[3] = -1;
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
