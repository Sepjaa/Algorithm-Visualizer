package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class SelectionSort extends AbstractAlgorithm {

	SelectionSort() {
		super();
	}

	private SelectionSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new SelectionSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.SELECTION_SORT;
	}

	@Override
	protected void sort(short[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			int jMin = i;
			for (int j = i + 1; j < array.length; j++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (lessThan(array, j, jMin, false)) {
					jMin = j;
				}
			}
			swap(array, i, jMin);
		}
	}
}
