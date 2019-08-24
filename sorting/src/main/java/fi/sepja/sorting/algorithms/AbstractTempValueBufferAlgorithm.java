package fi.sepja.sorting.algorithms;

/**
 * Base class for algorithms that utilise a temporary value storage.
 *
 * @author Jaakko
 *
 */
public abstract class AbstractTempValueBufferAlgorithm extends AbstractAlgorithm {

	public AbstractTempValueBufferAlgorithm() {
		super();
	}

	public AbstractTempValueBufferAlgorithm(int comparisonSleepMicros, int swapSleepMicros) {
		super(comparisonSleepMicros, swapSleepMicros);
	}

	protected void storeTemp(short[] array, int arrayIndex) {
		array[array.length - 1] = array[arrayIndex];
		lastSwap(array.length, arrayIndex);
		sleep(swapSleep);
	}

	protected void assignFromTemp(short[] array, int arrayIndex) {
		array[arrayIndex] = array[array.length - 1];
		lastSwap(array.length, arrayIndex);
		sleep(swapSleep);
	}

	protected void clearTemp(short[] array) {
		array[array.length - 1] = 0;
	}

	protected boolean lessThanTemp(short[] array, int arrayIndex, boolean allowEqual) {
		return lessThan(array, arrayIndex, array.length - 1, allowEqual);
	}

	protected boolean biggerThanTemp(short[] array, int arrayIndex, boolean allowEqual) {
		return biggerThan(array, arrayIndex, array.length - 1, allowEqual);
	}

	@Override
	public boolean isTempValueBufferAlgorithm() {
		return true;
	}
}
