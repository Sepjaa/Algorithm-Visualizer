package fi.sepjaa.visualizer.sorting.algorithm;

import java.util.Arrays;

import fi.sepjaa.visualizer.sorting.SortingData;

/**
 * Immutable copy of a {@link SortingData}'s state.
 *
 * @author Jaakko
 *
 */
public class ImmutableSortingData {

	private final short[] elements;
	private final short[] elementsMemory;
	private final int[] swapIndexes;
	private final int[] comparisonIndexes;
	private final boolean memoryArray;

	public ImmutableSortingData(short[] elements, short[] elementsMemory, int[] swapIndexes, int[] comparisonIndexes) {
		super();
		this.elements = Arrays.copyOf(elements, elements.length);
		this.elementsMemory = Arrays.copyOf(elementsMemory, elementsMemory.length);
		this.swapIndexes = Arrays.copyOf(swapIndexes, swapIndexes.length);
		this.comparisonIndexes = Arrays.copyOf(comparisonIndexes, comparisonIndexes.length);
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

	public int[] getSwapIndexes() {
		return swapIndexes;
	}

	public int[] getComparisonIndexes() {
		return comparisonIndexes;
	}

	public boolean isMemoryArray() {
		return memoryArray;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(comparisonIndexes);
		result = prime * result + Arrays.hashCode(elements);
		result = prime * result + Arrays.hashCode(elementsMemory);
		result = prime * result + Arrays.hashCode(swapIndexes);
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
		if (!Arrays.equals(comparisonIndexes, other.comparisonIndexes)) {
			return false;
		}
		if (!Arrays.equals(elements, other.elements)) {
			return false;
		}
		if (!Arrays.equals(elementsMemory, other.elementsMemory)) {
			return false;
		}
		if (!Arrays.equals(swapIndexes, other.swapIndexes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ImmutableSortingData [elements=" + Arrays.toString(elements) + ", elementsMemory="
				+ Arrays.toString(elementsMemory) + ", swapIndexes=" + Arrays.toString(swapIndexes)
				+ ", comparisonIndexes=" + Arrays.toString(comparisonIndexes) + "]";
	}

}
