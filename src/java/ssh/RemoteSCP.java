package ssh;

import java.io.File;
import java.io.IOException;

import remotessh.SSHController;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

/**
 * Copies a file to remote server.
 */
public class RemoteSCP {

	String host = "";
	String user = "";
	Integer port=0;
	String userpass="";
	String file = "";
	String remotedir = "";
	String usercommand = "";
	String output = "";

	public RemoteSCP(String host, String file, String remotedir) {
		this.host = host;
		this.file = file;
		this.remotedir = remotedir;
	}
	public RemoteSCP(String host, String user, String file, String remotedir) {
		this.host = host;
		this.user = user;
		this.file = file;
		this.remotedir = remotedir;
	}
	public RemoteSCP(String host, String user, String userpass, String file, String remotedir) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.remotedir = remotedir;
	}
	public RemoteSCP(String host, String file, String remotedir, Integer port) {
		this.host = host;
		this.file = file;
		this.remotedir = remotedir;
		this.port=port;
	}
	public RemoteSCP(String host, String user, String file, String remotedir, Integer port) {
		this.host = host;
		this.user = user;
		this.file = file;
		this.remotedir = remotedir;
		this.port=port;
	}
	public RemoteSCP(String host, String user, String userpass,  String file, String remotedir, Integer port) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.remotedir = remotedir;
		this.port=port;
	}

	public String Result() {
		SSHController ac=new SSHController();
		Object sshuser=ac.getConfig("ssh.USER");
		Object sshpass=ac.getConfig("ssh.PASS");
		Object sshkey=ac.getConfig("ssh.KEY");
		Object sshkeypass=ac.getConfig("ssh.KEYPASS");
		Object sshport=ac.getConfig("ssh.PORT");
		//System.out.println("----"+sshuser.toString());
		if (user.equals("")) {
			user = sshuser.toString();
		}
		if (userpass.equals("")) {
			userpass = sshpass.toString();
		}
		if (port==0) {
			String sps=sshport.toString();
			if (sps.matches("[0-9]+")) {
				port=Integer.parseInt(sps);
			}
		}
		String hostname = host;
		String username = user;
		File keyfile = new File(sshkey.toString());
		String keyfilePass = sshkeypass.toString();
		try {
		if (port==0){port=22; }
			Connection conn = new Connection(hostname,port);
			/* Now connect */
			conn.connect();
			/* Authenticate */
			boolean isAuthenticated=false;
			if (userpass.equals("")) {
				isAuthenticated = conn.authenticateWithPublicKey(username,
						keyfile, keyfilePass);
			}else{
				isAuthenticated = conn.authenticateWithPassword(username,userpass);
			}
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			/* Create a session */
			SCPClient scp = conn.createSCPClient();
			scp.put(file, remotedir);
			conn.close();

			output = "File " + file + " should now be copied to " + host + ":"
					+ remotedir + "<br>";

		} catch (IOException e) {
			output = e.toString();
		}
		return output;
	}
}
