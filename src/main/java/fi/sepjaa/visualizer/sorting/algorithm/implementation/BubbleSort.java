package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractAlgorithm;

@Component
public class BubbleSort extends AbstractAlgorithm {

	@Override
	public Type getType() {
		return Type.BUBBLE_SORT;
	}

	@Override
	public void sort(SortingData data) {
		int n = data.getLength();
		while (n >= 1) {
			int newn = 0;
			for (int i = 1; i < n; i++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (data.biggerThan(i - 1, i, false)) {
					data.swap(i - 1, i);
					newn = i;
				}
			}
			n = newn;
		}
	}
}
