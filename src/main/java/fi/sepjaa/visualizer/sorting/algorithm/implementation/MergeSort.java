package fi.sepjaa.visualizer.sorting.algorithm.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import fi.sepjaa.visualizer.sorting.SortingData;
import fi.sepjaa.visualizer.sorting.algorithm.AbstractFullMemoryBufferAlgorithm;

@Component
public class MergeSort extends AbstractFullMemoryBufferAlgorithm {
	private static final Logger LOG = LoggerFactory.getLogger(MergeSort.class);

	@Override
	public Type getType() {
		return Type.MERGE_SORT;
	}

	@Override
	public void sort(SortingData data) {
		data.copyArrayToMemory();
		topDownSplitMerge(data, 0, data.getLength(), true);
	}

	private void topDownSplitMerge(SortingData data, int begin, int end, boolean swap) {
		if (end - begin >= 2) {
			int middle = (end + begin) / 2;
			topDownSplitMerge(data, begin, middle, !swap);
			topDownSplitMerge(data, middle, end, !swap);
			topDownMerge(data, begin, middle, end, swap);
		}
	}

	private void topDownMerge(SortingData data, int begin, int middle, int end, boolean swap) {
		int i = begin;
		int j = middle;
		for (int k = begin; k < end; k++) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			if (i < middle && (j >= end || (swap ? data.lessThanInMemory(i, j, true) : data.lessThan(i, j, true)))) {
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				if (!swap) {
					data.copyArrayValueToMemory(i, k);
				} else {
					data.copyMemoryValueToArray(k, i);
				}
				i++;
			} else {
				if (!swap) {
					data.copyArrayValueToMemory(j, k);
				} else {
					data.copyMemoryValueToArray(k, j);
				}
				j++;
			}
		}
	}

//	@Override
//	protected void sort(short[] array, short[] memoryArray) {
//		swap = true;
//		copyArray(array, memoryArray);
//		swap = !swap;
//		topDownSplitMerge(array, memoryArray, 0, array.length);
//	}
//
//	private void topDownSplitMerge(short[] a, short[] b, int begin, int end) {
//		if (end - begin < 2) {
//			return;
//		}
//		swap = !swap;
//		int middle = (end + begin) / 2;
//		topDownSplitMerge(b, a, begin, middle);
//		topDownSplitMerge(b, a, middle, end);
//		topDownMerge(a, b, begin, middle, end);
//	}
//
//	private void topDownMerge(short[] a, short[] b, int begin, int middle, int end) {
//		int i = begin;
//		int j = middle;
//		swap = !swap;
//		for (int k = begin; k < end; k++) {
//			if (Thread.currentThread().isInterrupted()) {
//				return;
//			}
//			if (i < middle && (j >= end || lessThan(a, i, j, true))) {
//				swap = !swap;
//				copyIndexValue(a, b, i, k);
//				swap = !swap;
//				i++;
//			} else {
//				swap = !swap;
//				copyIndexValue(a, b, j, k);
//				swap = !swap;
//				j++;
//			}
//		}
//	}

}
