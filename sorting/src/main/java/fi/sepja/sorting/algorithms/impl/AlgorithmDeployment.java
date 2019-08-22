package fi.sepja.sorting.algorithms.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepja.sorting.algorithms.Algorithm;
import fi.sepja.sorting.algorithms.Algorithm.AlgorithmType;

/**
 * Static provider for all the {@link Algorithm}s. Validates all
 * {@link AlgorithmType} enums and makes sure that and implementation is found
 * in ::getImplementationFor().
 *
 * @author Jaakko
 *
 */
public class AlgorithmDeployment {
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmDeployment.class);

	public static AlgorithmType[] getAlgorithms() {
		// Validate that all enums have an implementation
		List<Algorithm> result = new ArrayList<>();
		for (AlgorithmType type : AlgorithmType.values()) {
			result.add(getImplementationFor(type));
		}

		AlgorithmType[] resultArray = new AlgorithmType[result.size()];
		for (int i = 0; i < result.size(); i++) {
			resultArray[i] = result.get(i).getType();
		}
		return resultArray;
	}

	public static Algorithm getImplementationFor(AlgorithmType type) {
		switch (type) {
		case INSERTION_SORT: {
			return new InsertionSort();
		}
		case COCKTAIL_SHAKER_SORT: {
			return new CocktailShakerSort();
		}
		case QUICK_SORT: {
			return new QuickSort();
		}
		case HEAP_SORT: {
			return new HeapSort();
		}
		case MERGE_SORT: {
			return new MergeSort();
		}
		}
		LOG.error("Add implementation for {}", type);
		throw new UnsupportedOperationException("No implementation for algorithm type " + type);
	}

}
