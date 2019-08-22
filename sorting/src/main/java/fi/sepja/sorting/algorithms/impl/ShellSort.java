package fi.sepja.sorting.algorithms.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.sepja.sorting.algorithms.AbstractAlgorithm;
import fi.sepja.sorting.algorithms.Algorithm;

public class ShellSort extends AbstractAlgorithm {

	ShellSort() {
		super();
	}

	private ShellSort(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros) {
		return new ShellSort(comparisonSleepMicros, swapSleepMicros);
	}

	@Override
	public AlgorithmType getType() {
		return AlgorithmType.SHELL_SORT;
	}

	@Override
	protected void sort(short[] array, short[] memoryArray) {
		List<Integer> gaps = new ArrayList<>();
		int k = 1;
		int result = 0;
		while (result < array.length / 3) {
			result = (int) ((Math.pow(3, k) - 1) / 2);
			gaps.add(result);
			k++;
		}
		Collections.reverse(gaps);
		System.out.println("Gaps " + gaps);
		for (int gap : gaps) {
			for (int i = gap; i < array.length; i++) {
				storeTemp(array, i);
				int j;
				for (j = i; j >= gap && biggerThanTemp(array, j - gap, false); j -= gap) {
					swap(array, j, j - gap);
				}
				assignFromTemp(array, j);
			}
		}
		clearTemp(array);
	}
}
