package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class CombSort extends AbstractAlgorithm {

	CombSort() {
		super();
	}

	private CombSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public void sort(short[] array) {
		int gap = array.length;
		double shrink = 1.3;
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			gap = (int) (gap / shrink);
			if (gap <= 1) {
				gap = 1;
			}
			int i = 0;
			while (i + gap < array.length) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (biggerThan(array, i, i + gap, false)) {
					swap(array, i, i + gap);
					sorted = false;
				}
				i++;
			}
		}
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new CombSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.COMB_SORT;
	}

}
