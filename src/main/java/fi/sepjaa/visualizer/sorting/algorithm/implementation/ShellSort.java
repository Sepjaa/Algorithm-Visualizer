package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractTempValueBufferAlgorithm;

@Component
public class ShellSort extends AbstractTempValueBufferAlgorithm {

	@Override
	public Type getType() {
		return Type.SHELL_SORT;
	}

	@Override
	public void sort(SortingData data) {
		List<Integer> gaps = new ArrayList<>();
		int k = 1;
		int result = 0;
		while (result < data.getLength() / 3) {
			result = (int) ((Math.pow(3, k) - 1) / 2);
			gaps.add(result);
			k++;
		}
		Collections.reverse(gaps);
		for (int gap : gaps) {
			for (int i = gap; i < data.getLength(); i++) {
				data.storeTemp(i);
				int j;
				for (j = i; j >= gap && data.biggerThanTemp(j - gap, false); j -= gap) {
					if (Thread.currentThread().isInterrupted()) {
						return;
					}
					data.swap(j, j - gap);
				}
				data.assignFromTemp(j);
			}
		}
		data.clearTemp();
	}
}
