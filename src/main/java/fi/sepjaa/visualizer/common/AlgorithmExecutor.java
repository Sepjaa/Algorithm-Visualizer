package fi.sepjaa.visualizer.common;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Class to handle one algorithm execution in the application. All the algorithm
 * executions will be ran in the single "AlgorithmExecutorThread".
 *
 * @author Jaakko
 *
 */
@Component
public class AlgorithmExecutor implements AlgorithmExecutionEventDispatcher {
	private static final Logger LOG = LoggerFactory.getLogger(AlgorithmExecutor.class);
	private static final String ALGORITHM_EXECUTOR_THREAD_NAME = "AlgorithmExecutorThread";
	private final ExecutorService executor = Executors.newSingleThreadExecutor(new AlgorithmThreadFactory());

	private final List<AlgorithmExecutionListener> listeners = new CopyOnWriteArrayList<>();

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
	public void start(Runnable runnable) {
		synchronized (lock) {
			LOG.info("{} starting", this);
			this.execution = executor.submit(() -> {
				dispatchStart();
				runnable.run();
				dispatchEnd(false);
			});
		}
	}

	private void dispatchStart() {
		LOG.info("Dispatching on start event");
		listeners.forEach(l -> l.onStart());
	}

	private void dispatchEnd(boolean canceled) {
		LOG.info("Dispatching on end event, canceled {}", canceled);
		listeners.forEach(l -> l.onEnd(canceled));
	}

	@Override
	public void addListener(AlgorithmExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(AlgorithmExecutionListener listener) {
		if (!listeners.remove(listener)) {
			LOG.warn("Tried to remove listener {} from listeners but it was not registered to {}", listener, listeners);
		}
	}

	private class AlgorithmThreadFactory implements ThreadFactory {
		@Override
		public Thread newThread(Runnable r) {
			LOG.info("Constructing new thread");
			Thread thread = new Thread(r, ALGORITHM_EXECUTOR_THREAD_NAME);
			thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					LOG.error("****************************************************\nThread {} uncaught exception.", t,
							e);
				}
			});
			return thread;
		}
	}
}
