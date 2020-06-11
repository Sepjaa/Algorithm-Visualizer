package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractAlgorithm;

@Component
public class CombSort extends AbstractAlgorithm {

	@Override
	public Type getType() {
		return Type.COMB_SORT;
	}

	@Override
	public void sort(SortingData data) {
		int gap = data.getLength();
		double shrink = 1.3;
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			gap = (int) (gap / shrink);
			if (gap <= 1) {
				gap = 1;
			}
			int i = 0;
			while (i + gap < data.getLength()) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (data.biggerThan(i, i + gap, false)) {
					data.swap(i, i + gap);
					sorted = false;
				}
				i++;
			}
		}
	}
}
