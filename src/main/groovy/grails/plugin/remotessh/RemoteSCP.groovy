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

    String Result(ConfigObject ac) throws InterruptedException {

        Object sshuser = ac?.USER ?: ''
        Object sshpass = ac?.PASS ?: ''
        Object sshkey = ac?.KEY ?: ''
        Object sshkeypass = ac?.KEYPASS ?: ''
        Object sshport = ac?.PORT

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
			scp.put(file, remotedir)
			conn.close()

			output = "File $file should now be copied to $host:$remotedir<br>"

		} catch (IOException e) {
			output = e.toString()
		}
		return output
	}
}
