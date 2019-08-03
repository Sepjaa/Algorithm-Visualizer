package fi.sepja.sorting.algorithms;

/**
 * Base class for algorithm containing the logic operations used in algorithm.
 * Logic operations are compare and swap. Operations use/manipulate the array of
 * bytes using two indexes. Indexes are used to possibly record a history of
 * algorithm operations in future.
 *
 * TODO: variable sleeps
 * 
 * @author Jaakko
 *
 */
public abstract class AbstractAlgorithm implements Algorithm {

	protected boolean lessThan(short[] list, int index1, int index2, boolean allowEqual) {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (allowEqual) {
			return list[index1] <= list[index2];
		}
		return list[index1] < list[index2];
	}

	protected boolean biggerThan(short[] list, int index1, int index2, boolean equal) {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (equal) {
			return list[index1] >= list[index2];
		}
		return list[index1] > list[index2];
	}

	protected void swap(short[] listToBeSorted, int i1, int i2) {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		short temp = listToBeSorted[i1];
		listToBeSorted[i1] = listToBeSorted[i2];
		listToBeSorted[i2] = temp;
	}
}
