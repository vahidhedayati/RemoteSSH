package grails.plugin.remotessh

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.SFTPv3Client
import ch.ethz.ssh2.SFTPv3FileHandle

/**
 * Used by AddFTP and AddServerRemote classes This uses
 * SSHConfig Interface Gets files from remote hosts
 */
class RemoteSCPGet  {

	String host = ''
	String user = ''
	Integer port = 0
	String userpass = ''
	String file = ''
	String localdir = ''
	String output = ''
	private static final int BUFSIZE = 1024

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

			String fileName,localFile
			if (file) {
				fileName=(new File(file)).getName()
				if (fileName) {
					localFile = localdir + File.separator + fileName
				}
			}
			if (localFile) {
				SFTPv3Client client = new SFTPv3Client(conn)
				File actualFile = new File(localFile)
				SFTPv3FileHandle handle = client.openFileRO(file)
				BufferedOutputStream bfout = new BufferedOutputStream(new FileOutputStream(actualFile))
				byte[] buf = new byte[BUFSIZE]
				int count = 0
				int bufsiz = 0
				while ((bufsiz = client.read(handle, count, buf, 0, BUFSIZE)) != -1) {
					bfout.write(buf, 0, bufsiz)
					count += bufsiz
				}
				bfout.close()
				client.closeFile(handle)
				if (handle.isClosed()){
					client.close()
				}
				output = "File $file should now be copied from $host to localdir: $localdir<br>"
			}
			conn.close()
		} catch (IOException e) {
			output += e.toString()
		}
		return output
	}
}
