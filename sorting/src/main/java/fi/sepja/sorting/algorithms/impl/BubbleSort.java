package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class BubbleSort extends AbstractAlgorithm {

	BubbleSort() {
		super();
	}

	private BubbleSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public void sort(short[] array) {
		int n = array.length;
		while (n >= 1) {
			int newn = 0;
			for (int i = 1; i < n; i++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (biggerThan(array, i - 1, i, false)) {
					swap(array, i - 1, i);
					newn = i;
				}
			}
			n = newn;
		}
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new BubbleSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.BUBBLE_SORT;
	}

}
