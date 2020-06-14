package fi.sepjaa.visualizer.sorting;

import java.util.Arrays;
import java.util.Random;

import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepjaa.visualizer.common.CommonConstants;
import fi.sepjaa.visualizer.common.Utils;
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
	private SortingOperationIndexes swapIndexes = SortingOperationIndexes.noStatement();
	@GuardedBy("lock")
	private SortingOperationIndexes comparisonIndexes = SortingOperationIndexes.noStatement();
	@GuardedBy("lock")
	private long swapSleep = CommonConstants.DEFAULT_SWAP_SLEEP;
	@GuardedBy("lock")
	private long comparisonSleep = CommonConstants.DEFAULT_COMPARISON_SLEEP;

	public SortingData(long elementCount, SortingAlgorithm algorithm) {
		this(elementCount, algorithm, CommonConstants.DEFAULT_SWAP_SLEEP, CommonConstants.DEFAULT_COMPARISON_SLEEP);
	}

	public SortingData(long elementCount, SortingAlgorithm algorithm, long swapSleep, long comparisonSleep) {
		if (elementCount <= 0) {
			LOG.error("Invalid element count {}, set to default {}", elementCount,
					CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT);
			elementCount = CommonConstants.DEFAULT_SORTING_ELEMENT_AMOUNT;
		}
		int n = (int) elementCount + (algorithm.isTempValueBufferAlgorithm() ? 1 : 0);

		elements = new short[n];
		elementsMemory = new short[n];

		for (int i = 0; i < elementCount; i++) {
			elements[i] = (short) random.nextInt(1001);
		}
		this.swapSleep = swapSleep;
		this.comparisonSleep = comparisonSleep;
		LOG.info("Constructed {}", this);
	}

	public void setSwapSleep(long swapSleep) {
		LOG.info("Updating swap sleep to {}", swapSleep);
		synchronized (lock) {
			this.swapSleep = swapSleep;
		}
	}

	public void setComparisonSleep(long compareSleep) {
		LOG.info("Updating compare sleep to {}", compareSleep);
		synchronized (lock) {
			this.comparisonSleep = compareSleep;
		}
	}

	public int getLength() {
		synchronized (lock) {
			return elements.length;
		}
	}

	public ImmutableSortingData getCopy() {
		synchronized (lock) {
			return new ImmutableSortingData(elements, elementsMemory, swapIndexes, comparisonIndexes, swapSleep,
					comparisonSleep);
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
		long compareSleep;
		synchronized (lock) {
			setNextComparison(index1, index2, CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT);
			compareSleep = this.comparisonSleep;
		}
		Utils.sleep(compareSleep);
	}

	private void compareNextInMemory(int index1, int index2) {
		long compareSleep;
		synchronized (lock) {
			setNextComparison(CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT, index1, index2);
			compareSleep = this.comparisonSleep;
		}
		Utils.sleep(compareSleep);
	}

	public void swap(int index1, int index2) {
		long swapSleep;
		synchronized (lock) {
			setNextSwap(index1, index2, CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT);
			swapSleep = this.swapSleep;
		}
		Utils.sleep(swapSleep);
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
			swapIndexes = SortingOperationIndexes.noStatement();
			comparisonIndexes = SortingOperationIndexes.noStatement();
		}
	}

	private void setNextSwap(int index0, int index1, int index2, int index3) {
		swapIndexes = SortingOperationIndexes.instance(index0, index1, index2, index3);
		comparisonIndexes = SortingOperationIndexes.noStatement();
	}

	private void setNextComparison(int index0, int index1, int index2, int index3) {
		comparisonIndexes = SortingOperationIndexes.instance(index0, index1, index2, index3);
		swapIndexes = SortingOperationIndexes.noStatement();
	}

	public void copyArrayToMemory() {
		for (int i = 0; i < elements.length; i++) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			long swapSleep;
			synchronized (lock) {
				swapSleep = this.swapSleep;
				setNextSwap(i, CommonConstants.NO_STATEMENT, i, CommonConstants.NO_STATEMENT);
			}
			Utils.sleep(swapSleep);
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
		long swapSleep;
		synchronized (lock) {
			swapSleep = this.swapSleep;
			setNextSwap(index, CommonConstants.NO_STATEMENT, memoryIndex, CommonConstants.NO_STATEMENT);
		}
		Utils.sleep(swapSleep);
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
		long swapSleep;
		synchronized (lock) {
			swapSleep = this.swapSleep;
			setNextSwap(index, elements.length - 1, CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT);
		}
		Utils.sleep(swapSleep);
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
		long comparisonSleep;
		synchronized (lock) {
			comparisonSleep = this.swapSleep;
			setNextComparison(index, elements.length - 1, CommonConstants.NO_STATEMENT, CommonConstants.NO_STATEMENT);
		}
		Utils.sleep(comparisonSleep);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (comparisonSleep ^ (comparisonSleep >>> 32));
		result = prime * result + ((comparisonIndexes == null) ? 0 : comparisonIndexes.hashCode());
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Arrays.hashCode(elementsMemory);
		result = prime * result + ((lock == null) ? 0 : lock.hashCode());
		result = prime * result + ((random == null) ? 0 : random.hashCode());
		result = prime * result + ((swapIndexes == null) ? 0 : swapIndexes.hashCode());
		result = prime * result + (int) (swapSleep ^ (swapSleep >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SortingData other = (SortingData) obj;
		if (comparisonSleep != other.comparisonSleep) {
			return false;
		}
		if (comparisonIndexes == null) {
			if (other.comparisonIndexes != null) {
				return false;
			}
		} else if (!comparisonIndexes.equals(other.comparisonIndexes)) {
			return false;
		}
		if (!Arrays.equals(elements, other.elements)) {
			return false;
		}
		if (!Arrays.equals(elementsMemory, other.elementsMemory)) {
			return false;
		}
		if (lock == null) {
			if (other.lock != null) {
				return false;
			}
		} else if (!lock.equals(other.lock)) {
			return false;
		}
		if (random == null) {
			if (other.random != null) {
				return false;
			}
		} else if (!random.equals(other.random)) {
			return false;
		}
		if (swapIndexes == null) {
			if (other.swapIndexes != null) {
				return false;
			}
		} else if (!swapIndexes.equals(other.swapIndexes)) {
			return false;
		}
		if (swapSleep != other.swapSleep) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SortingData [swapSleep=" + swapSleep + ", compareSleep=" + comparisonSleep + ", elements="
				+ Arrays.toString(elements) + ", elementsMemory=" + Arrays.toString(elementsMemory) + ", swapIndexes="
				+ swapIndexes + ", comparisonIndexes=" + comparisonIndexes + "]";
	}

}
