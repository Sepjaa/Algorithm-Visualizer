package fi.sepja.sorting.algorithms;

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
	void sort(short[] listToBeSorted);

	/**
	 * Identifier for algorithm.
	 */
	AlgorithmType getType();

	public static enum AlgorithmType {

		INSERTION_SORT("Insertion Sort");

		private final String value;

		AlgorithmType(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}
