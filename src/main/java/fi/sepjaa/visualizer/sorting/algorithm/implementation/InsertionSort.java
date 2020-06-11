package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractAlgorithm;

@Component
public class InsertionSort extends AbstractAlgorithm {

	@Override
	public Type getType() {
		return Type.INSERTION_SORT;
	}

	@Override
	public void sort(SortingData data) {
		int i = 1;
		while (i < data.getLength() && !Thread.currentThread().isInterrupted()) {
			int j = i;
			while (j > 0 && data.biggerThan(j - 1, j, false) && !Thread.currentThread().isInterrupted()) {
				data.swap(j, j - 1);
				j--;
			}
			i++;
		}
	}

}
