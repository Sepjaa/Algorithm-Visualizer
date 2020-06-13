package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractSortingAlgorithm;

@Component
public class HeapSort extends AbstractSortingAlgorithm {

	@Override
	public Type getType() {
		return Type.HEAP_SORT;
	}

	@Override
	public void sort(SortingData data) {
		int length = data.getLength();
		buildMaxHeap(data, length);
		for (int i = length - 1; i > 0; i--) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			data.swap(0, i);
			maxHeapify(data, 1, i);
		}
	}

	private void buildMaxHeap(SortingData data, int heapSize) {
		if (heapSize > data.getLength()) {
			heapSize = data.getLength();
		}
		for (int i = heapSize / 2; i > 0; i--) {
			maxHeapify(data, i, heapSize);
		}
	}

	private void maxHeapify(SortingData data, int index, int heapSize) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		int l = index * 2;
		int r = l + 1;
		int largest;
		if (l <= heapSize && data.biggerThan(l - 1, index - 1, false)) {
			largest = l;
		} else {
			largest = index;
		}
		if (r <= heapSize && data.biggerThan(r - 1, largest - 1, false)) {
			largest = r;
		}
		if (largest != index) {
			data.swap(index - 1, largest - 1);
			maxHeapify(data, largest, heapSize);
		}
	}
}
