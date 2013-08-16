package grails.plugin.remotessh

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient

/**
 * Used by AddFTP and AddServerRemote classes This uses
 * SSHConfig Interface Gets files from remote hosts
 */
class RemoteSCPGet  {

	String host = ""
	String user = ""
	Integer port=0
	String userpass=""
	String file = ""
	String localdir = ""
	String output = ""

	String Result(SshConfig ac) {
		Object sshuser=ac.getConfig("USER")
		Object sshpass=ac.getConfig("PASS")
		Object sshkey=ac.getConfig("KEY")
		Object sshkeypass=ac.getConfig("KEYPASS")
		Object sshport=ac.getConfig("PORT")
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
			Connection conn = new Connection(host,scpPort ?: 22)
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

			/* Create a session */
			SCPClient scp = conn.createSCPClient()
			scp.get(file, localdir)
			conn.close()
			output = "File $file should now be copied from $host to localdir: $localdir<br>"

		} catch (IOException e) {
			output += e.toString()
		}
		return output
	}
}
