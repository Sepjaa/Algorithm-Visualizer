package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractAlgorithm;

@Component
public class CocktailShakerSort extends AbstractAlgorithm {

	@Override
	public Type getType() {
		return Type.COCKTAIL_SHAKER_SORT;
	}

	@Override
	public void sort(SortingData data) {
		int begin = 0;
		int end = data.getLength() - 2;
		while (begin <= end && !Thread.currentThread().isInterrupted()) {
			int newBegin = end;
			int newEnd = begin;
			for (int i = begin; i < end + 1; i++) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (data.biggerThan(i, i + 1, false)) {
					data.swap(i, i + 1);
					newEnd = i;
				}
			}
			end = newEnd;
			for (int i = end - 1; i > begin - 1; i--) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (data.biggerThan(i, i + 1, false)) {
					data.swap(i, i + 1);
					newBegin = i;
				}
			}
			begin = newBegin;
		}
	}

}
