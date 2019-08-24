package fi.sepja.sorting;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepja.sorting.algorithms.Algorithm;

/**
 * Handles all the sorting execution and creation/distribution of array
 * references.
 *
 * @author Jaakko
 *
 */
public class Sorter {
	public static int DEFAULT_ELEMENT_AMOUNT = 100;
	public static int DEFAULT_COMPARISON_SLEEP = 1000;
	public static int DEFAULT_SWAP_SLEEP = 1000;
	private static final Logger LOG = LoggerFactory.getLogger(Sorter.class);
	private final Random random = new Random();

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final short[] elements;
	private final short[] elementsMemory;
	private final int[] lastSwapIndexes = { -1, -1, -1, -1 };
	private final int[] lastComparisonIndexes = { -1, -1, -1, -1 };

	public Sorter(Visualizer visualizer, int elementCount, Algorithm algorithm) {
		if (elementCount <= 0) {
			LOG.error("Invalid element count {}, set to default {}", elementCount, DEFAULT_ELEMENT_AMOUNT);
			elementCount = DEFAULT_ELEMENT_AMOUNT;
		}
		int n = elementCount + (algorithm.isTempValueBufferAlgorithm() ? 1 : 0);

		elements = new short[n];
		elementsMemory = algorithm.isFullMemoryBufferAlgorithm() ? new short[n] : null;

		for (int i = 0; i < elementCount; i++) {
			elements[i] = (short) random.nextInt(1001);
		}

		visualizer.bindArrays(elements, Optional.ofNullable(elementsMemory));
		LOG.info("Sorter {} created with {} elements", this, elements.length);
	}

	public void destroy() {
		LOG.info("Destroying {}", this);
		// There is never any clean up in sorting executor -> no reason to wait for
		// sorting to be complete
		executor.shutdownNow();
		try {
			boolean shutdown = executor.awaitTermination(1, TimeUnit.SECONDS);
			if (!shutdown) {
				LOG.warn("Timeout occured when shutting down executor for {}", this);
			} else {
				LOG.info("Executor was shutdown in a timely manner for {}", this);
			}
		} catch (InterruptedException e) {
			LOG.warn("Interrupted while waiting for executor to shut down in {}", this);
			Thread.currentThread().interrupt();
		}
		LOG.info("Destroying {} finished", this);
	}

	/**
	 * Starts sorting in the executor thread and returns a future for it.
	 */
	public Future<?> startSorting(Algorithm algorithm, Visualizer visualizer) {
		LOG.info("{} starting sorting with {} elements", this, elements.length);
		return executor.submit(() -> {
			visualizer.bindCompareSwapArrays(lastComparisonIndexes, lastSwapIndexes);
			algorithm.sort(elements, Optional.ofNullable(elementsMemory), lastComparisonIndexes, lastSwapIndexes);
			visualizer.unbindSwapAndCompareArray();
		});
	}
}
