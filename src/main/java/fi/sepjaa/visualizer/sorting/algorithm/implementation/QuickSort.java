package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import java.util.Random;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractSortingAlgorithm;

@Component
public class QuickSort extends AbstractSortingAlgorithm {
	private final Random random = new Random();

	@Override
	public Type getType() {
		return Type.QUICK_SORT;
	}

	@Override
	public void sort(SortingData data) {
		quicksort(data, 0, data.getLength());
	}

	public void quicksort(SortingData data, int left, int right) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		if (left < right) {
			int partition = qP(data, left, right);
			quicksort(data, left, partition);
			quicksort(data, partition + 1, right);
		}
	}

	private int qP(SortingData data, int left, int right) {
		int i = left;
		int j;
		int pivot = left + random.nextInt(right - left);
		data.swap(left, pivot);
		for (j = left + 1; j < right; j++) {
			if (Thread.currentThread().isInterrupted()) {
				return i;
			}
			if (data.lessThan(j, left, true)) {
				i++;
				data.swap(i, j);
			}
		}
		data.swap(i, left);
		return i;
	}

}
