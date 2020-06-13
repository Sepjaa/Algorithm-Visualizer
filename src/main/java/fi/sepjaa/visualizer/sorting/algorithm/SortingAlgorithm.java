package fi.sepjaa.visualizer.sorting.algorithm;

import fi.sepjaa.visualizer.sorting.SortingData;

/**
 * Interface to access sorting algorithm functions.
 *
 * @author Jaakko
 *
 */
public interface SortingAlgorithm {

	/**
	 * Sorts the byte array in the caller thread.
	 */
	void sort(SortingData data);

	/**
	 * Identifier for algorithm.
	 */
	Type getType();

	public enum Type {

		INSERTION_SORT("Insertion Sort"), COCKTAIL_SHAKER_SORT("Cocktail Shaker Sort"), QUICK_SORT("Quick Sort"),
		HEAP_SORT("Heap Sort"), MERGE_SORT("Merge Sort"), SELECTION_SORT("Selection Sort"), SHELL_SORT("Shell Sort"),
		BUBBLE_SORT("Bubble Sort"), COMB_SORT("Comb Sort");

		private String value;

		Type(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}

	}

	/**
	 * @return Whether the algorithm requires double the memory. Also the memory
	 *         array should be visualized.
	 */
	boolean isFullMemoryBufferAlgorithm();

	/**
	 * @return Whether the algorithm makes use of a temporary value memory value. Is
	 *         visualized with n - 1 elements with the last being the assigned
	 *         temporary value. So for n = 100, 99 values are sorted.
	 */
	boolean isTempValueBufferAlgorithm();
}
