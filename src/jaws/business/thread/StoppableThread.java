package jaws.business.thread;

public class StoppableThread extends Thread {

	private Stoppable stoppable;

	public <T extends Runnable & Stoppable>StoppableThread(T runnable) {

		super(runnable);
	}

	@Override
	public void interrupt() {

		stoppable.stop();
		super.interrupt();
	}
}
