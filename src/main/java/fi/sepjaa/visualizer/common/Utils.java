package fi.sepjaa.visualizer.common;

public class Utils {
	/**
	 * Semi-accurate sleep for algorithms to use.
	 *
	 * @param micros sleep duration in microseconds.
	 */
	public static void sleep(int micros) {
		if (micros > 0) {
			final long start = System.nanoTime();
			if (micros >= 5000) {
				try {
					Thread.sleep(micros / 1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// Busy sleep to get accuracy over millisecond
			while (System.nanoTime() - start < micros * 1000) {

			}
		}
	}
}
