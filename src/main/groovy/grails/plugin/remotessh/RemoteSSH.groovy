package grails.plugin.remotessh

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.Session
import ch.ethz.ssh2.StreamGobbler

class RemoteSSH {

    String host = ''
	String user = ''
	String sudo = ''
	Integer port=0
	String userpass = ''
	String usercommand = ''
	String filter = ''

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
			Session sess = conn.openSession()
			sess.requestPTY("vt220")
			if (sudo == "sudo") {
				sess.execCommand("sudo -i")
				// sess.execCommand("sudo bash")
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
					output.append(line).append("<br>")
				} else {
					if (line.startsWith(filter)) {
						output.append(line).append("<br>")
					}
				}
			}
			/* Close this session */
			sess.close()
			/* Close the connection */
			conn.close()

		} catch (IOException e) {
			output.append(e)
		}
		return output.toString()
	}
}
