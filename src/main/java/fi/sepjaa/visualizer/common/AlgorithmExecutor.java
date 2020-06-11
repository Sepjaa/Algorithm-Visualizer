package fi.sepjaa.visualizer.common;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class to handle one algorithm execution in the application.
 *
 * @author Jaakko
 *
 */
@Component
public class AlgorithmExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmExecutor.class);

	private final ExecutorService executor = Executors
			.newSingleThreadExecutor(r -> new Thread(r, "AlgorithmExecutorThread"));

	private final Object lock = new Object();
	@GuardedBy("lock")
	private Future<?> execution = CompletableFuture.completedFuture(true);

	public void stop() {
		synchronized (lock) {
			if (!this.execution.isDone() && !this.execution.isCancelled()) {
				LOG.info("Stopping execution");
				this.execution.cancel(true);
			}
		}
	}

	public boolean isExecuting() {
		synchronized (lock) {
			boolean executing = !this.execution.isDone();
			LOG.info("Is executing returning {}", executing);
			return executing;
		}
	}

	@PreDestroy
	public void destroy() {
		LOG.info("Destroying {}", this);
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
	 * Starts algorithm in the executor thread and returns a future for it.
	 */
	public void start(Runnable runnable, Runnable callbackOnCompletion) {
		synchronized (lock) {
			LOG.info("{} starting", this);
			this.execution = executor.submit(() -> {
				LOG.info("Running new runnable");
				runnable.run();
				LOG.info("Running callback");
				callbackOnCompletion.run();
			});
		}
	}
}
