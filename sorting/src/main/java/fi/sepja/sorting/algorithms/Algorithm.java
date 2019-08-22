package fi.sepja.sorting.algorithms;

import fi.sepja.sorting.algorithms.impl.AlgorithmDeployment;

/**
 * Interface to access algorithm functions.
 *
 * @author Jaakko
 *
 */
public interface Algorithm {

	/**
	 * Sorts the byte array in the caller thread.
	 */
	void sort(short[] listToBeSorted, short[] memoryArray, int[] lastComparisonIndexes, int[] lastSwapIndexes);

	/**
	 * Sets the algorithm operation delays.
	 *
	 * @param comparisonSleepMicros Time in microseconds to sleep after every
	 *                              comparison.
	 * @param swapSleepMicros       Time in microseconds to sleep after every swap.
	 * @return New instance of the algorithm to maintain Immutability.
	 */
	Algorithm setDelays(int comparisonSleepMicros, int swapSleepMicros);

	/**
	 * Identifier for algorithm.
	 */
	AlgorithmType getType();

	/**
	 * Enum containing all the available algorithm types. Algorithms can be added by
	 * adding values to this and providing an implementation for them in
	 * {@link AlgorithmDeployment}.
	 *
	 * @author Jaakko
	 *
	 */
	public enum AlgorithmType {

		INSERTION_SORT("Insertion Sort"), COCKTAIL_SHAKER_SORT("Cocktail Shaker Sort"), QUICK_SORT("Quick Sort"),
		HEAP_SORT("Heap Sort"), MERGE_SORT("Merge Sort"), SELECTION_SORT("Selection Sort"), SHELL_SORT("Shell Sort");

		private String value;

		AlgorithmType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
