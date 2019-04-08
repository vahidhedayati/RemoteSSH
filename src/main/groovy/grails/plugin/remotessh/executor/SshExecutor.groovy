package grails.plugin.remotessh.executor
import grails.util.Holders

import java.util.concurrent.*
class SshExecutor  extends ThreadPoolExecutor {

	private static long keepAliveTime=((Holders.grailsApplication.config.remotessh?.keepAliveTime ?: 300) as Long)
	private static TimeUnit timeoutUnit=TimeUnit.SECONDS
	private static int corePoolSize = Holders.grailsApplication.config.remotessh?.corePoolSize ?: 1
	private static final int actualPoolSize = Holders.grailsApplication.config.remotessh?.maximumPoolSize ?: 3
	private static int maximumPoolSize = actualPoolSize
	private static int maxQueue = Holders.grailsApplication.config.remotessh.maxQueue?:100
	private static int minPreserve = Holders.grailsApplication.config.remotessh?.minPreserve ?: 0
	
	public SshExecutor() {
		super(corePoolSize,maximumPoolSize,keepAliveTime,timeoutUnit,
			new PriorityBlockingQueue<Runnable>(maxQueue,new ThreadComparator()) 
		)
	}



	public ComparableFutureTask execute(Runnable command, int priority) {
		ComparableFutureTask task = new ComparableFutureTask(command,null,this,priority, actualPoolSize, minPreserve)
		super.execute(task)
	}
	
	void setMaximumPoolSize(int i) {
		this.maximumPoolSize=i
	}
	void setMaxQueue(int i) {
		this.maxQueue=i
	}
	void setMinPreserve(int i) {
		this.minPreserve=i
	}
	void setDefinedPriority(Priority p) {
		this.definedPriority=p
	}

}

