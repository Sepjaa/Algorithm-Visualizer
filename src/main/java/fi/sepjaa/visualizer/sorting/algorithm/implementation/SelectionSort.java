package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractAlgorithm;

@Component
public class SelectionSort extends AbstractAlgorithm {

	@Override
	public Type getType() {
		return Type.SELECTION_SORT;
	}

	@Override
	public void sort(SortingData data) {
		for (int i = 0; i < data.getLength() - 1; i++) {
			int jMin = i;
			for (int j = i + 1; j < data.getLength(); j++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (data.lessThan(j, jMin, false)) {
					jMin = j;
				}
			}
			data.swap(i, jMin);
		}
	}
}
