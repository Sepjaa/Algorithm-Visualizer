package fi.sepja.sorting.algorithms.impl;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class MergeSort extends AbstractAlgorithm {

	MergeSort() {
		super();
	}

	private MergeSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new MergeSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.MERGE_SORT;
	}

	@Override
	protected void sort(short[] array, short[] memoryArray) {
		swap = true;
		copyArray(array, memoryArray);
		swap = !swap;
		topDownSplitMerge(array, memoryArray, 0, array.length);
	}

	private void topDownSplitMerge(short[] a, short[] b, int begin, int end) {
		if (end - begin < 2) {
			return;
		}
		swap = !swap;
		int middle = (end + begin) / 2;
		topDownSplitMerge(b, a, begin, middle);
		topDownSplitMerge(b, a, middle, end);
		topDownMerge(a, b, begin, middle, end);
	}

	private void topDownMerge(short[] a, short[] b, int begin, int middle, int end) {
		int i = begin;
		int j = middle;
		swap = !swap;
		for (int k = begin; k < end; k++) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			if (i < middle && (j >= end || lessThan(a, i, j, true))) {
				swap = !swap;
				copyIndexValue(a, b, i, k);
				swap = !swap;
				i++;
			} else {
				swap = !swap;
				copyIndexValue(a, b, j, k);
				swap = !swap;
				j++;
			}
		}
	}

}
