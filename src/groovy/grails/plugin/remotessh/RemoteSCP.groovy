package grails.plugin.remotessh

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileHandle;

/**
 * Copies a file to remote server.
 */
class RemoteSCP {

	String host = ""
	String user = ""
	Integer port=0
	String userpass=""
	String file = ""
	String remotedir = ""
	String usercommand = ""
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
		String characterSet = (charsetName ?: (charSet ? charSet.toString() : null))
		String permission =(mode ?: (perm ?perm.toString() :"0600"))
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

			File actualFile = new File(file)
			FileOutputStream out =  null
			try {
				out = new FileOutputStream(remotedir+File.separator+actualFile.getName())
				SFTPv3Client sFTPv3Client = new SFTPv3Client(conn)
				SFTPv3FileHandle handle = sFTPv3Client.openFileRO(file)
				byte[] cache = new byte[1024]
				int i = 0
				int offset = 0
				while((i = sFTPv3Client.read(handle, offset, cache, 0, 1024)) != -1){
					out.write(cache, 0, i)
					offset += i
				}
				sFTPv3Client.closeFile(handle)
		        if (handle.isClosed()){
				    sFTPv3Client.close()
		        } 
			} catch (IOException e) {
				e.printStackTrace()
			} finally {
				try {
					if (out) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace()
				}
			}
			conn.close()
			output = "File $file should now be copied to $host:$remotedir<br>"

		} catch (IOException e) {
			output = e.toString()
		}
		return output
	}
}
