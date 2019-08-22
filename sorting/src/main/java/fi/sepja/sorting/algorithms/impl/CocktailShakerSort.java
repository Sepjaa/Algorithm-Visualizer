package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class CocktailShakerSort extends AbstractAlgorithm {

	CocktailShakerSort() {
		super();
	}

	private CocktailShakerSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public void sort(short[] array, short[] memoryArray) {
		int begin = 0;
		int end = array.length - 2;
		while (begin <= end && !Thread.currentThread().isInterrupted()) {
			int newBegin = end;
			int newEnd = begin;
			for (int i = begin; i < end + 1; i++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (biggerThan(array, i, i + 1, false)) {
					swap(array, i, i + 1);
					newEnd = i;
				}
			}
			end = newEnd;
			for (int i = end - 1; i > begin - 1; i--) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (biggerThan(array, i, i + 1, false)) {
					swap(array, i, i + 1);
					newBegin = i;
				}
			}
			begin = newBegin;
		}
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new CocktailShakerSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.COCKTAIL_SHAKER_SORT;
	}

}
