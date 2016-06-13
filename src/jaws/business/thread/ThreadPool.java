package jaws.business.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A pool of worker threads that run a queue of tasks.
 * 
 * @author geroy
 *
 */
public class ThreadPool {

	private BlockingQueue<Runnable> taskQueue;
	private List<PoolThread> threads;
	private boolean isStopped;
	
	public ThreadPool(int noOfThreads) {
		
		taskQueue = new LinkedBlockingQueue<>();
		threads = new ArrayList<>();
		isStopped = false;
		
		for(int i=0; i<noOfThreads; i++) {
			threads.add(new PoolThread(taskQueue));
		}
		for(PoolThread thread : threads) {
			thread.start();
		}
	}
	
	/**
	 * Queue a task for the worker threads to run.
	 * 
	 * @param task the runnable task.
	 * @throws InterruptedException if the queue is stopped.
	 */
	public synchronized void execute(Runnable task) throws InterruptedException {
		
		if(isStopped)
			throw new IllegalStateException("ThreadPool is stopped");
		
		taskQueue.put(task);
	}
	
	/**
	 * Stops the pool and all worker threads;
	 */
	public synchronized void stop() {
		
		isStopped = true;
		for(PoolThread thread : threads) {
			
			thread.doStop();
		}
	}
}
