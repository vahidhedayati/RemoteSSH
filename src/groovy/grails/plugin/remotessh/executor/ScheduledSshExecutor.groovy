package grails.plugin.remotessh.executor

import grails.util.Holders

import java.util.concurrent.*
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
class ScheduledSshExecutor  extends ScheduledThreadPoolExecutor {
	private boolean isPaused = false;
	private static int corePoolSize = Holders.grailsApplication.config.remotessh?.corePoolSize ?: 1
	private static int maxQueue = Holders.grailsApplication.config.remotessh.maxQueue?:100
	
		private ReentrantLock pauseLock = new ReentrantLock();
		private Condition unpaused = pauseLock.newCondition();
	
	public RunnableScheduledFuture schedule(Runnable command, Long timeout=10l, TimeUnit timeUnit=TimeUnit.SECONDS) {
		super.schedule(command,timeout,timeUnit)
	}
	public ScheduledSshExecutor() {
		super(corePoolSize) 
	
	}
	public ScheduledSshExecutor(int corePoolSize) {
		super(corePoolSize);
		// TODO Auto-generated constructor stub

	}
	/*

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		// TODO Auto-generated method stub
		super.beforeExecute(t, r);
		pauseLock.lock();
		try {
			while (isPaused)
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
	}

	*/
	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
			
		} finally {
			pauseLock.unlock();
		}
	}
}

