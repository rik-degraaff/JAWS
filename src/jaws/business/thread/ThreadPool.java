package jaws.business.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	
	public synchronized void execute(Runnable task) throws InterruptedException {
		
		if(isStopped)
			throw new IllegalStateException("ThreadPool is stopped");
		
		taskQueue.put(task);
	}
	
	public synchronized void stop() {
		
		isStopped = true;
		for(PoolThread thread : threads) {
			
			thread.doStop();
		}
	}
}
