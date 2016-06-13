package jaws.business.thread;

import static trycrash.Try.tryCatch;

import java.util.concurrent.BlockingQueue;

/**
 * A worker thread that constantly searches for a task in the queue and performs that task if found.
 * 
 * @author Roy
 *
 */
public class PoolThread extends Thread {

	private BlockingQueue<Runnable> taskQueue;
	private boolean isStopped;

	public PoolThread(BlockingQueue<Runnable> taskQueue) {

		this.taskQueue = taskQueue;
		isStopped = false;
	}

	@Override
	public void run() {

		while(!isStopped) {
			tryCatch(() -> taskQueue.take()).ifPresent(Runnable::run);
		}
	}

	/**
	 * Stops the worker thread.
	 */
	public synchronized void doStop() {

		isStopped = true;
		interrupt();
	}

	/**
	 * Returns true if the worker thread was stopped, false otherwise.
	 * 
	 * @return true if the worker thread was stopped.
	 */
	public synchronized boolean isStopped() {

		return isStopped;
	}
}
