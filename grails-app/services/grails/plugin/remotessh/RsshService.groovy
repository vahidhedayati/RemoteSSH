package grails.plugin.remotessh

import ch.ethz.ssh2.ChannelCondition
import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.Session
import ch.ethz.ssh2.StreamGobbler

/*
 * @author  Vahid Hedayati - 21st June 2015
 * 
 * Broken up ganymed calls into execute which carries out the rest of the segments
 * Segments can be used by calling each bit as required if you wish to reuse the code
 *  
 */

class RsshService {

	static transactional = false

	String runCommand(RsshValidate pm) {
		StringBuilder output = new StringBuilder()
		try { 
			// Do initial connection
			Connection conn = connect(pm.host,pm.sshport as int)
			// If we have a connection
			if (conn) {
				// grab the ssh session
				Session sess = openSession(conn, pm.keyfile,pm.sshkeypass,pm.sshuser,pm.sshpass)
				if (sess) {
					// if we have session run the command
					// this is where you could keep connected and have a session
					// in your own calls and run variety of commands if you wish
					// to call this service in segments
					output = executeCommand(sess, conn, pm.usercommand, pm.splitter, pm.sudo, pm.filter)
					closeConnection(conn,sess)
				}else{
					output << "No session $pm.keyfile $pm.sshkeypass $pm.sshuser $pm.sshpass"
				}
			}else{
				output << "No connection $pm.host $pm.sshport"
			}
		}catch(Exception e) {
			output << e.message
		}
		return output.toString()
	}

	String scpDir(RsshValidate pm){
		String output = ''
		try {
			// Do initial connection
			Connection conn = connect(pm.host,pm.sshport as int)
			// If we have a connection
			if (conn) {
				Session sess = openSession(conn, pm.keyfile,pm.sshkeypass,pm.sshuser,pm.sshpass)
				if (sess) {
					scpDir(conn, sess, pm.localdir, pm.remotedir, pm.permission,pm.charSet)
					closeConnection(conn,sess)
					output = "$pm.localdir should now be copied to $pm.host:$pm.remotedir${pm.splitter}"
				}else{
					output = "No session $pm.keyfile $pm.sshkeypass $pm.sshuser $pm.sshpass"
				}
	
			}else{
				output = "No connection $pm.host $pm.sshport"
			}
		}catch(Exception e) {
			output=e.message
		}
		return output
	}

	String scpFile(RsshValidate pm) {
		String output = ''
		try {
			// Do initial connection
			Connection conn = connect(pm.host,pm.sshport as int)
			if (conn) {
				Session sess = openSession(conn, pm.keyfile,pm.sshkeypass,pm.sshuser,pm.sshpass)
				if (sess) {
					scpFile(conn, pm.file, pm.remotedir,pm.permission, pm.charSet)
					closeConnection(conn,sess)
				}else{
					output = "No session $pm.keyfile $pm.sshkeypass $pm.sshuser $pm.sshpass"
				}
				output = "File $pm.file should now be copied from $pm.host to $pm.remotedir: $pm.file${pm.splitter}"
			}else{
				log.error "No connection $pm.host $pm.sshport"
			}
		}catch(Exception e) {
			output=e.message
		}
		return output
	}

	String scpGet(RsshValidate pm) {
		String output = ''
		try {
			// Do initial connection
			Connection conn = connect(pm.host,pm.sshport as int)
			if (conn) {
				Session sess = openSession(conn, pm.keyfile,pm.sshkeypass,pm.sshuser,pm.sshpass)
				if (sess) {
					scpGet(conn, pm.file, pm.localdir,pm.charSet)
					closeConnection(conn,sess)
				}else{
					output = "No session $pm.keyfile $pm.sshkeypass $pm.sshuser $pm.sshpass"
				}
				output = "File $pm.file should now be copied from $pm.host to ${pm.localdir}:${pm.file}${pm.splitter}"
			}else{
				log.error "No connection $pm.host $pm.sshport"
			}
		}catch(Exception e) {
			output=e.message
		}
		return output
	}

	//Simply just connect
	Connection connect(String host, int sshport)  throws Exception {
		Connection conn
		try {
			conn = new Connection(host,sshport)
			conn.connect()
		} catch (Exception e) {
			throw new Exception(e)
		}
		return conn
	}

	//Once we have above connection we should attempt to get ssh session
	Session openSession(Connection conn, File keyfile,String keyfilePass,String sshuser,String sshpass=null){
		Session sess
		try {
			boolean isAuthenticated = false
			if (!sshpass) {
				if (keyfile) {
					isAuthenticated = conn.authenticateWithPublicKey(sshuser,keyfile, keyfilePass)
				}
			}else{
				isAuthenticated = conn.authenticateWithPassword(sshuser,sshpass)
			}
			if (!isAuthenticated) {
				if (keyfile) {
					throw new IOException("Key Authentication failed.")
				}else{
					throw new IOException("Password Authentication failed.")
				}	
			}
			sess = conn.openSession()
		}catch(Exception e) {
			throw new Exception(e)
		}
		return sess
	}

	// 0.6 / 0.7 override resolves issue caused by nulls introduced pre 0.5		
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter) {
		executeCommand(sess,conn,usercommand,splitter,'','',false)
	}
	
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter,String sudo) {
		executeCommand(sess,conn,usercommand,splitter,sudo,'',false)
	}
	
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter,String sudo,boolean hasConnection) {
		executeCommand(sess,conn,usercommand,splitter,sudo,'',hasConnection)
	}
	
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter,boolean hasConnection) {
		executeCommand(sess,conn,usercommand,splitter,'','',hasConnection)
	}
	
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter,String sudo, String filter) {
		executeCommand(sess,conn,usercommand,splitter,sudo,filter,false)
	}
	
	// To save rewriting / messing around with end user's setup introduced above overrides
	// Now that we have session and connection lets attempt to execute actual usercommand
	StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter, 
		String sudo, String filter, boolean hasConnection) {
		
		StringBuilder output = new StringBuilder()

		if (!hasConnection && sess){
			sess.requestPTY("vt220")
		}else{
			sess = conn.openSession()
		}
		if (sudo) {
			if (sudo == "sudo") {
				sess.execCommand("sudo bash")
			}else{
				sess.execCommand(sudo)
			}
		} else {
			sess.execCommand("/bin/bash")
		}
		sleep(10)
		sess.getStdin().write((usercommand + "\n").getBytes())
		sess.getStdin().write("exit\n".getBytes())
		sess.getStdin().write("exit\n".getBytes())
		sleep(10)
		InputStream stdout = new StreamGobbler(sess.getStdout())
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout))
		while (true) {
			String line = br.readLine()
			if (line == null)
				break
			if (!filter) {
				output.append(line).append(splitter)
			} else {
				if (line.startsWith(filter)) {
					output.append(line).append(splitter)
				}
			}
		}
		return output
	}

	void scpDir(Connection conn, Session sess, String localDirectory,
			String remoteTargetDirectory, String mode,String charSet=null) throws Exception {
		try {
			File curDir = new File(localDirectory)
			final String[] fileList = curDir.list()
			for (String file : fileList) {
				final String fullFileName = "$localDirectory/$file"
				if (new File(fullFileName).isDirectory()) {
					final String subDir = "$remoteTargetDirectory/$file"
					sess = conn.openSession()
					sess.execCommand("mkdir $subDir")
					sess.waitForCondition(ChannelCondition.EOF, 0)
					scpDir(conn, sess, fullFileName, subDir, mode,charSet)
					sleep(200)
				} else {
					SCPClient scpc = conn.createSCPClient()
					if (charSet) {
						scpc.setCharset(charSet)
					}
					scpc.put(fullFileName, 1L, remoteTargetDirectory, mode)
				}
			}
		} catch (Exception e) {
			throw new Exception(e)
		}
	}

	void scpFile(Connection conn, String file, String remotedir,String permission, String charSet=null) throws Exception {
		try {
			SCPClient scp = conn.createSCPClient()
			if (charSet) {
				scp.setCharset(charSet)
			}
			scp.put(file, 1L, remotedir, permission)
		} catch (Exception e) {
			throw new Exception(e)
		}
	}

	void scpGet(Connection conn, String file, String localdir,String charSet=null) throws Exception {
		try {
			SCPClient scp = conn.createSCPClient()
			if (charSet) {
				scp.setCharset(charSet)
			}
			scp.get(file, localdir)
		} catch (Exception e) {
			throw new Exception(e)
		}
	}


	def closeConnection(Connection conn,Session sess=null) {
		if (sess) {
			closeSession(sess)
		}
		if (conn) {
			conn.close()
		}
	}

	def closeSession( Session sess) {
		if (sess) {
			sess.close()
		}
	}
}
