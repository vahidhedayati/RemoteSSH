package grails.plugin.remotessh.executor

import java.util.concurrent.FutureTask
import java.util.concurrent.ScheduledThreadPoolExecutor


class ComparableFutureTask<T> extends FutureTask<T>  {
	
	volatile int priority = 0
	volatile int maxPoolSize = 0
	volatile int minPreserve = 0
	SshExecutor sshExecutor
	volatile ScheduledThreadPoolExecutor timeoutExecutor

	public ComparableFutureTask(Runnable runnable, T result, SshExecutor sshExecutor,ScheduledThreadPoolExecutor timeoutExecutor,
		int priority, int maxPoolSize,int minPreserve) {
		super(runnable, result)
		this.sshExecutor=sshExecutor
		this.timeoutExecutor=timeoutExecutor
		updatePriority(priority,maxPoolSize,minPreserve)
	}


	public ComparableFutureTask(Runnable runnable, T result,SshExecutor sshExecutor,
		int priority,  int maxPoolSize,int minPreserve) {
			super(runnable, result)
			this.sshExecutor=sshExecutor
			updatePriority(priority,maxPoolSize,minPreserve)
		}


	private void updatePriority(int priority, int maxPoolSize,int minPreserve) {
		this.priority = priority
		this.maxPoolSize = maxPoolSize
		this.minPreserve = minPreserve
	}
}

