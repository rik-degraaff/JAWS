package jaws.util.thread;

/**
 * A special thread that takes a Runnable-Stoppable and stops it when interrupted.
 * 
 * @author Roy
 *
 */
public class StoppableThread extends Thread {

	private Stoppable stoppable;

	public <T extends Runnable & Stoppable>StoppableThread(T runnable) {
		
		super(runnable);
		stoppable = runnable;
	}

	@Override
	public void interrupt() {

		if (stoppable != null) stoppable.stop();
		super.interrupt();
	}
}
