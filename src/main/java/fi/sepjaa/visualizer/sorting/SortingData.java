package fi.sepjaa.visualizer.sorting;

import java.util.Random;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.sorting.algorithm.ImmutableSortingData;
import fi.sepjaa.visualizer.sorting.algorithm.SortingAlgorithm;

/**
 * Handles all the sorting data operations.
 *
 * @author Jaakko
 *
 */
public class SortingData {
	private static final Logger LOG = LoggerFactory.getLogger(SortingData.class);
	private final Random random = new Random();

	private final Object lock = new Object();
	@GuardedBy("lock")
	private final short[] elements;
	@GuardedBy("lock")
	private final short[] elementsMemory;
	@GuardedBy("lock")
	private final int[] swapIndexes = { -1, -1, -1, -1 };
	@GuardedBy("lock")
	private final int[] comparisonIndexes = { -1, -1, -1, -1 };
	@GuardedBy("lock")
	private int swapSleep = CommonConstants.DEFAULT_SWAP_SLEEP;
	@GuardedBy("lock")
	private int compareSleep = CommonConstants.DEFAULT_COMPARISON_SLEEP;

	public SortingData(int elementCount, SortingAlgorithm algorithm) {
		if (elementCount <= 0) {
			LOG.error("Invalid element count {}, set to default {}", elementCount,
					CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT);
			elementCount = CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT;
		}
		int n = elementCount + (algorithm.isTempValueBufferAlgorithm() ? 1 : 0);

		elements = new short[n];
		elementsMemory = new short[n];

		for (int i = 0; i < elementCount; i++) {
			elements[i] = (short) random.nextInt(1001);
		}
		LOG.info("SortingData {} created with {} elements", this, elements.length);
	}

	public void setSwapSleep(int swapSleep) {
		synchronized (lock) {
			this.swapSleep = swapSleep;
		}
	}

	public void setCompareSleep(int compareSleep) {
		synchronized (lock) {
			this.compareSleep = compareSleep;
		}
	}

	public int getLength() {
		synchronized (lock) {
			return elements.length;
		}
	}

	public ImmutableSortingData getCopy() {
		synchronized (lock) {
			return new ImmutableSortingData(elements, elementsMemory, swapIndexes, comparisonIndexes);
		}
	}

	private void sleep(int micros) {
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

	// Operations
	public boolean lessThan(int index1, int index2, boolean allowEqual) {
		compareNext(index1, index2);
		synchronized (lock) {
			if (allowEqual) {
				return elements[index1] <= elements[index2];
			}
			return elements[index1] < elements[index2];
		}
	}

	public boolean biggerThan(int index1, int index2, boolean allowEqual) {
		return !lessThan(index1, index2, !allowEqual);
	}

	public boolean lessThanInMemory(int index1, int index2, boolean allowEqual) {
		compareNextInMemory(index1, index2);
		synchronized (lock) {
			if (allowEqual) {
				return elementsMemory[index1] <= elementsMemory[index2];
			}
			return elementsMemory[index1] < elementsMemory[index2];
		}
	}

	private void compareNext(int index1, int index2) {
		int compareSleep;
		synchronized (lock) {
			setNextComparison(index1, index2, -1, -1);
			compareSleep = this.compareSleep;
		}
		sleep(compareSleep);
	}

	private void compareNextInMemory(int index1, int index2) {
		int compareSleep;
		synchronized (lock) {
			setNextComparison(-1, -1, index1, index2);
			compareSleep = this.compareSleep;
		}
		sleep(compareSleep);
	}

	public void swap(int index1, int index2) {
		int swapSleep;
		synchronized (lock) {
			setNextSwap(index1, index2, -1, -1);
			swapSleep = this.swapSleep;
		}
		sleep(swapSleep);
		synchronized (lock) {
			if (index1 == index2) {
				return;
			}
			short temp = elements[index1];
			elements[index1] = elements[index2];
			elements[index2] = temp;
		}
	}

	public void clearNexts() {
		synchronized (lock) {
			swapIndexes[0] = -1;
			swapIndexes[1] = -1;
			swapIndexes[2] = -1;
			swapIndexes[3] = -1;
			comparisonIndexes[0] = -1;
			comparisonIndexes[1] = -1;
			comparisonIndexes[2] = -1;
			comparisonIndexes[3] = -1;
		}
	}

	private void setNextSwap(int index0, int index1, int index2, int index3) {
		swapIndexes[0] = index0;
		swapIndexes[1] = index1;
		swapIndexes[2] = index2;
		swapIndexes[3] = index3;
		comparisonIndexes[0] = -1;
		comparisonIndexes[1] = -1;
		comparisonIndexes[2] = -1;
		comparisonIndexes[3] = -1;
	}

	private void setNextComparison(int index0, int index1, int index2, int index3) {
		comparisonIndexes[0] = index0;
		comparisonIndexes[1] = index1;
		comparisonIndexes[2] = index2;
		comparisonIndexes[3] = index3;
		swapIndexes[0] = -1;
		swapIndexes[1] = -1;
		swapIndexes[2] = -1;
		swapIndexes[3] = -1;
	}

	public void copyArrayToMemory() {
		for (int i = 0; i < elements.length; i++) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			int swapSleep;
			synchronized (lock) {
				swapSleep = this.swapSleep;
				setNextSwap(i, -1, i, -1);
			}
			sleep(swapSleep);
			synchronized (lock) {
				elementsMemory[i] = elements[i];
			}
		}
	}

	public void copyArrayValueToMemory(int index, int memoryIndex) {
		memoryArraySwapNext(index, memoryIndex);
		synchronized (lock) {
			elementsMemory[memoryIndex] = elements[index];
		}
	}

	public void copyMemoryValueToArray(int index, int memoryIndex) {
		memoryArraySwapNext(index, memoryIndex);
		synchronized (lock) {
			elements[index] = elementsMemory[memoryIndex];
		}
	}

	private void memoryArraySwapNext(int index, int memoryIndex) {
		int swapSleep;
		synchronized (lock) {
			swapSleep = this.swapSleep;
			setNextSwap(index, -1, memoryIndex, -1);
		}
		sleep(swapSleep);
	}

	public void storeTemp(int index) {
		tempSwap(index);
		synchronized (lock) {
			elements[elements.length - 1] = elements[index];
		}
	}

	public void assignFromTemp(int index) {
		tempSwap(index);
		synchronized (lock) {
			elements[index] = elements[elements.length - 1];
		}
	}

	public void clearTemp() {
		synchronized (lock) {
			elements[elements.length - 1] = 0;
		}
	}

	private void tempSwap(int index) {
		int swapSleep;
		synchronized (lock) {
			swapSleep = this.swapSleep;
			setNextSwap(index, elements.length - 1, -1, -1);
		}
		sleep(swapSleep);
	}

	public boolean lessThanTemp(int index, boolean allowEqual) {
		tempCompare(index);
		synchronized (lock) {
			return lessThan(index, elements.length - 1, allowEqual);
		}
	}

	public boolean biggerThanTemp(int index, boolean allowEqual) {
		tempCompare(index);
		synchronized (lock) {
			return biggerThan(index, elements.length - 1, allowEqual);
		}
	}

	private void tempCompare(int index) {
		int comparisonSleep;
		synchronized (lock) {
			comparisonSleep = this.swapSleep;
			setNextComparison(index, elements.length - 1, -1, -1);
		}
		sleep(comparisonSleep);
	}

}
