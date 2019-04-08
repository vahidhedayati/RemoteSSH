package grails.plugin.remotessh.executor

import grails.plugin.remotessh.SSHUtil
import ch.ethz.ssh2.Connection

/**
 * 
 * @author Vahid Hedayati April 2019
 *
 */
class SshRunnable implements Runnable {


	private SSHUtil sshUtil
	private Connection connection
	private  Closure closure
	
	
	public SshRunnable( Closure closure){
		this.closure=closure
	}
	
	/**
	 * 
	 * @param sshUtil : SSHUtil currently in use the class passed in
	 * 
	 * @param connection : SshUtil's existing connection  
	 * can be swapped to other connections if there are existing multiple connection

	 * @param closure : carrying out actual work
	 */
	public SshRunnable(SSHUtil sshUtil, Connection connection,  Closure closure){
		this.sshUtil=sshUtil
		this.connection=connection
		this.closure=closure
	}

	@Override
	public void run() {
		try {
			if (this.sshUtil && this.connection) {
				sshUtil.connection=this.connection
				this.closure.call()
			} else if (!this.sshUtil && !this.connection) {
				this.closure.call()
			}
			
		} catch (Exception e) {
			e.printStackTrace()
		}
	}
}
