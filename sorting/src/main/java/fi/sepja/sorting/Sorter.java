package fi.sepja.sorting;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.sepja.sorting.algorithms.Algorithm;

/**
 * Handles all the sorting execution.
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

	public Sorter(Visualizer visualizer, int elementCount) {
		if (elementCount <= 0) {
			LOG.error("Invalid element count {}, set to default {}", elementCount, DEFAULT_ELEMENT_AMOUNT);
			elementCount = DEFAULT_ELEMENT_AMOUNT;
		}
		elements = new short[elementCount];
		for (int i = 0; i < elementCount; i++) {
			elements[i] = (short) random.nextInt(1001);
		}
		visualizer.bindToArray(elements);
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
	 * Starts sorting in the executor thread.
	 */
	public void startSorting(Algorithm algorithm) {
		LOG.info("{} starting sorting with {} elements", this, elements.length);
		executor.submit(() -> algorithm.sort(elements));
	}
}
