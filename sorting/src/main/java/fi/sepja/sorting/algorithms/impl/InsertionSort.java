package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class InsertionSort extends AbstractAlgorithm {

	InsertionSort() {
		super();
	}

	private InsertionSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public void sort(short[] array) {
		int i = 1;
		while (i < array.length && !Thread.currentThread().isInterrupted()) {
			int j = i;
			while (j > 0 && biggerThan(array, j - 1, j, false) && !Thread.currentThread().isInterrupted()) {
				swap(array, j, j - 1);
				j--;
			}
			i++;
		}
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.INSERTION_SORT;
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new InsertionSort(comparisonSleepMicros, swapSleepMicros);
	}

}
