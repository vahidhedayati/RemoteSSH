package grails.plugin.remotessh

import ch.ethz.ssh2.ChannelCondition
import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.Session

/**
 * Called by IncludeUpdate class scps a folder to specified end server.
 */
class RemoteSCPDir  {

	String hostname = ""
	String user = ""
	Integer port=0
	String userpass=""
	String localdir = ""
	String remotedir = ""
	String output = ""
	String charsetName
	String mode
	
	String Result(SshConfig ac) {
		Object sshuser=ac.getConfig("USER")
		Object sshpass=ac.getConfig("PASS")
		Object sshkey=ac.getConfig("KEY")
		Object sshkeypass=ac.getConfig("KEYPASS")
		Object sshport=ac.getConfig("PORT")
		Object charSet=ac.getConfig("CHARACTERSET")
		Object perm=ac.getConfig("PERMISSION")
		String characterSet = (charsetName ?: (charSet ? charSet.toString() : null))
		String permission =(mode ?: (perm ?perm.toString() :"0600"))
		//println "----$sshuser"
		Integer scpPort = port
		if (!scpPort) {
			String sps=sshport.toString()
			if (sps.matches("[0-9]+")) {
				scpPort=Integer.parseInt(sps)
			}
		}
		String username = user ?: sshuser.toString()
		String password = userpass ?: sshpass.toString()
		File keyfile = new File(sshkey.toString())
		String keyfilePass = sshkeypass.toString()
		try {
			Connection conn = new Connection(hostname,scpPort ?: 22)
			/* Now connect */
			conn.connect()
			/* Authenticate */
			boolean isAuthenticated=false
			if (!password) {
				isAuthenticated = conn.authenticateWithPublicKey(username,
						keyfile, keyfilePass)
			}else{
				isAuthenticated = conn.authenticateWithPassword(username,password)
			}
			if (!isAuthenticated)
				throw new IOException("Authentication failed.")
			// Session sess = conn.openSession()
			// sess.execCommand("mkdir -p $remotedir")
			putDir(conn, localdir, remotedir, permission,characterSet)
			conn.close()
			output = "$localdir should now be copied to $hostname:$remotedir<br>"
		} catch (IOException e) {
			output = e.toString()
		}
		return output
	}

	private static void putDir(Connection conn, String localDirectory,
			String remoteTargetDirectory, String mode,String characterSet=null) throws IOException {

		File curDir = new File(localDirectory)
		final String[] fileList = curDir.list()
		for (String file : fileList) {
			final String fullFileName = "$localDirectory/$file"
			if (new File(fullFileName).isDirectory()) {
				final String subDir = "$remoteTargetDirectory/$file"
				Session sess = conn.openSession()
				sess.execCommand("mkdir $subDir")
				sess.waitForCondition(ChannelCondition.EOF, 0)
				putDir(conn, fullFileName, subDir, mode,characterSet)
			} else {
				SCPClient scpc = conn.createSCPClient()
				if (characterSet) {
					scpc.setCharset(characterSet)
				}
				scpc.put(fullFileName, 1L, remoteTargetDirectory, mode)
			}
		}
	}
}
