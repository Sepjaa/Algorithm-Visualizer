package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class HeapSort extends AbstractAlgorithm {

	HeapSort() {
		super();
	}

	private HeapSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new HeapSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.HEAP_SORT;
	}

	@Override
	protected void sort(short[] array, short[] memoryArray) {
		int length = array.length;
		buildMaxHeap(array, length);
		for (int i = length - 1; i > 0; i--) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			swap(array, 0, i);
			maxHeapify(array, 1, i);
		}
	}

	private void buildMaxHeap(short[] array, int heapSize) {
		if (heapSize > array.length) {
			heapSize = array.length;
		}
		for (int i = heapSize / 2; i > 0; i--) {
			maxHeapify(array, i, heapSize);
		}
	}

	private void maxHeapify(short[] array, int index, int heapSize) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		int l = index * 2;
		int r = l + 1;
		int largest;
		if (l <= heapSize && biggerThan(array, l - 1, index - 1, false)) {
			largest = l;
		} else {
			largest = index;
		}
		if (r <= heapSize && biggerThan(array, r - 1, largest - 1, false)) {
			largest = r;
		}
		if (largest != index) {
			swap(array, index - 1, largest - 1);
			maxHeapify(array, largest, heapSize);
		}
	}
}
