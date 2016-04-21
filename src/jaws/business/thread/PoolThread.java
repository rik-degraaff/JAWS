package jaws.business.thread;

import static trycrash.Try.tryCatch;

import java.util.concurrent.BlockingQueue;

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
			
			Runnable task = tryCatch(() -> taskQueue.take()).orElseGet(() -> () -> {});
			task.run();
		}
	}
	
	public synchronized void doStop() {
		
		isStopped = true;
		interrupt();
	}
	
	public synchronized boolean isStopped() {
		
		return isStopped;
	}
}
