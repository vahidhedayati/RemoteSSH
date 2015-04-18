package grails.plugin.remotessh

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient

/**
 * Copies a file to remote server.
 */
class RemoteSCP {

    String host = ''
    String user = ''
    String sudo = ''
    Integer port = 0
    String userpass = ''
    String usercommand = ''
    String filter = ''
    String file = ""
    String remotedir = ""
    StringBuilder output = new StringBuilder()

    String Result(SshConfig ac) throws InterruptedException {

        Object sshuser = ac.config.USER ?: ''
        Object sshpass = ac.config.PASS ?: ''
        Object sshkey = ac.config.KEY ?: ''
        Object sshkeypass = ac.config.KEYPASS ?: ''
        Object sshport = ac.config.PORT ?: ''

        int scpPort
        scpPort = port ?: sshport.toString().matches("[0-9]+") { scpPort = sshport as int } ?: 22

        String username = user ?: sshuser.toString()
		String password = userpass ?: sshpass.toString()
		File keyfile = new File(sshkey.toString())
		String keyfilePass = sshkeypass.toString()
		try {
			Connection conn = new Connection(host,scpPort)
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
			scp.put(file, remotedir)
			conn.close()

			output = "File $file should now be copied to $host:$remotedir<br>"

		} catch (IOException e) {
			output = e.toString()
		}
		return output
	}
}
