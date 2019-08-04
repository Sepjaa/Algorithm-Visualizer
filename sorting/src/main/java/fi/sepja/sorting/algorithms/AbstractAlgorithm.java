package fi.sepja.sorting.algorithms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for algorithm containing the logic operations used in algorithm.
 * Logic operations are compare and swap. Operations use/manipulate the array of
 * bytes using two indexes.
 *
 * TODO: variable sleeps
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
	public void sort(short[] listToBeSorted, int[] lastComparisonIndexes, int[] lastSwapIndexes) {
		this.lastComparisonIndexes = lastComparisonIndexes;
		this.lastSwapIndexes = lastSwapIndexes;
		sort(listToBeSorted);
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

	protected boolean lessThan(short[] list, int index1, int index2, boolean allowEqual) {
		lastComparisonIndexes[0] = index1;
		lastComparisonIndexes[1] = index2;
		lastSwapIndexes[0] = -1;
		lastSwapIndexes[1] = -1;
		sleep(compareSleep);
		if (allowEqual) {
			return list[index1] <= list[index2];
		}
		return list[index1] < list[index2];
	}

	protected boolean biggerThan(short[] list, int index1, int index2, boolean allowEqual) {
		lastComparisonIndexes[0] = index1;
		lastComparisonIndexes[1] = index2;
		lastSwapIndexes[0] = -1;
		lastSwapIndexes[1] = -1;
		sleep(compareSleep);
		if (allowEqual) {
			return list[index1] >= list[index2];
		}
		return list[index1] > list[index2];
	}

	protected void swap(short[] listToBeSorted, int index1, int index2) {
		short temp = listToBeSorted[index1];
		listToBeSorted[index1] = listToBeSorted[index2];
		listToBeSorted[index2] = temp;
		lastSwapIndexes[0] = index1;
		lastSwapIndexes[1] = index2;
		lastComparisonIndexes[0] = -1;
		lastComparisonIndexes[1] = -1;
		sleep(swapSleep);
	}
}
