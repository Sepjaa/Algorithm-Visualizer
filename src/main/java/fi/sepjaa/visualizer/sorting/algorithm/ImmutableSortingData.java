package fi.sepjaa.visualizer.sorting.algorithm;

import java.util.Arrays;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.SortingOperationIndexes;

/**
 * Immutable copy of a {@link SortingData}'s state.
 *
 * @author Jaakko
 *
 */
public class ImmutableSortingData {

	private final short[] elements;
	private final short[] elementsMemory;
	private final SortingOperationIndexes swapIndexes;
	private final SortingOperationIndexes comparisonIndexes;
	private final boolean memoryArray;
	private final long swapSleep, comparisonSleep;

	public ImmutableSortingData(short[] elements, short[] elementsMemory, SortingOperationIndexes swapIndexes,
			SortingOperationIndexes comparisonIndexes, long swapSleep, long comparisonSleep) {
		super();
		this.elements = Arrays.copyOf(elements, elements.length);
		this.elementsMemory = Arrays.copyOf(elementsMemory, elementsMemory.length);
		this.swapIndexes = swapIndexes;
		this.comparisonIndexes = comparisonIndexes;
		this.swapSleep = swapSleep;
		this.comparisonSleep = comparisonSleep;
		boolean memoryArray = false;
		for (int i = 0; i < elementsMemory.length; i++) {
			if (elementsMemory[i] != 0) {
				memoryArray = true;
				break;
			}
		}
		this.memoryArray = memoryArray;
	}

	public int getLength() {
		return elements.length;
	}

	public short[] getElements() {
		return elements;
	}

	public short[] getElementsMemory() {
		return elementsMemory;
	}

	public SortingOperationIndexes getSwapIndexes() {
		return swapIndexes;
	}

	public SortingOperationIndexes getComparisonIndexes() {
		return comparisonIndexes;
	}

	public long getSwapSleep() {
		return swapSleep;
	}

	public long getComparisonSleep() {
		return comparisonSleep;
	}

	public boolean isMemoryArray() {
		return memoryArray;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comparisonIndexes == null) ? 0 : comparisonIndexes.hashCode());
		result = prime * result + (int) (comparisonSleep ^ (comparisonSleep >>> 32));
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Arrays.hashCode(elementsMemory);
		result = prime * result + (memoryArray ? 1231 : 1237);
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
		ImmutableSortingData other = (ImmutableSortingData) obj;
		if (comparisonIndexes == null) {
			if (other.comparisonIndexes != null) {
				return false;
			}
		} else if (!comparisonIndexes.equals(other.comparisonIndexes)) {
			return false;
		}
		if (comparisonSleep != other.comparisonSleep) {
			return false;
		}
		if (!Arrays.equals(elements, other.elements)) {
			return false;
		}
		if (!Arrays.equals(elementsMemory, other.elementsMemory)) {
			return false;
		}
		if (memoryArray != other.memoryArray) {
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
		return "ImmutableSortingData [elements=" + Arrays.toString(elements) + ", elementsMemory="
				+ Arrays.toString(elementsMemory) + ", swapIndexes=" + swapIndexes + ", comparisonIndexes="
				+ comparisonIndexes + ", memoryArray=" + memoryArray + ", swapSleep=" + swapSleep + ", comparisonSleep="
				+ comparisonSleep + "]";
	}

}
