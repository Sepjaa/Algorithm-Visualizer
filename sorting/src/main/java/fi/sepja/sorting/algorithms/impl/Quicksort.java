package fi.sepja.sorting.algorithms.impl;

import java.util.Random;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class Quicksort extends AbstractAlgorithm {
	private final Random random = new Random();

	Quicksort() {
		super();
	}

	private Quicksort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new Quicksort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.QUICKSORT;
	}

	@Override
	protected void sort(short[] array) {
		quicksort(array, 0, array.length);
	}

	public void quicksort(short[] array, int left, int right) {
		if (Thread.currentThread().isInterrupted()) {
			return;
		}
		if (left < right) {
			int partition = qP(array, left, right);
			quicksort(array, left, partition);
			quicksort(array, partition + 1, right);
		}
	}

	private int qP(short[] array, int left, int right) {
		int i = left;
		int j;
		int pivot = left + random.nextInt(right - left);
		swap(array, left, pivot);
		for (j = left + 1; j < right; j++) {
			if (Thread.currentThread().isInterrupted()) {
				return i;
			}
			if (lessThan(array, j, left, true)) {
				i++;
				swap(array, i, j);
			}
		}
		swap(array, i, left);
		return i;
	}
}
