package ssh;

import grails.plugin.remotessh.SshConfig;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

/**
 * Used by AddFTP and AddServerRemote classes This uses
 * SSHConfig Interface Gets files from remote hosts
 */
public class RemoteSCPGet  {

	String host = "";
	String user = "";
	Integer port=0;
	String userpass="";
	String file = "";
	String localdir = "";
	String usercommand = "";
	String output = "";
	String characterSet="";
	

	public RemoteSCPGet(String host, String file, String localdir) {
		this.host = host;
		this.file = file;
		this.localdir = localdir;
	}
	public RemoteSCPGet(String host, String user, String file, String localdir) {
		this.host = host;
		this.user = user;
		this.file = file;
		this.localdir = localdir;
	}
	public RemoteSCPGet(String host, String user, String userpass, String file, String localdir) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.localdir = localdir;
	}
	public RemoteSCPGet(String host,  String file, String localdir,Integer port) {
		this.host = host;
		this.file = file;
		this.localdir = localdir;
		this.port=port;
	}
	public RemoteSCPGet(String host, String user, String file, String localdir,Integer port) {
		this.host = host;
		this.user = user;
		this.file = file;
		this.localdir = localdir;
		this.port=port;
	}

	public RemoteSCPGet(String host, String user,  String userpass, String file, String localdir,Integer port) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.localdir = localdir;
		this.port=port;
	}

	public RemoteSCPGet(String host, String user,  String userpass, String file, String localdir,Integer port,String charSet) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.localdir = localdir;
		this.port=port;
		this.characterSet=charSet;
	}

	public String Result(SshConfig ac) throws IOException{
		Object sshuser=ac.getConfig("USER");
		Object sshpass=ac.getConfig("PASS");
		Object sshkey=ac.getConfig("KEY");
		Object sshkeypass=ac.getConfig("KEYPASS");
		Object sshport=ac.getConfig("PORT");
		String charSet=ac.getConfig("CHARACTERSET").toString();
		
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
		if (this.characterSet.length()==0) {
			if (charSet.length()>0) {
				this.characterSet=charSet;
			} else {
				this.characterSet="UTF-8";
			}
		}

		String hostname = host;
		String username = user;
		File keyfile = new File(sshkey.toString());
		String keyfilePass = sshkeypass.toString();
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
			if (characterSet!=null) {
				scp.setCharset(characterSet);
			}
			scp.get(file);
			conn.close();
			output = "File " + file + " should now be copied from " + host
					+ " to localdir: " + localdir + "<br>";

		return output;
	}
}