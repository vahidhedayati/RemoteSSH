package ssh;

import grails.plugin.remotessh.SshConfig;

import java.io.File;
import java.io.IOException;

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
	String characterSet="";
	String permission = "";
	
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
	
	public RemoteSCP(String host, String user, String userpass,  String file, String remotedir, Integer port,String charSet,String permission) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.remotedir = remotedir;
		this.port=port;
		this.characterSet=charSet;
		this.permission=permission;
	}

	public RemoteSCP(String host, String user, String userpass,  String file, String remotedir, Integer port,String charSet) {
		this.host = host;
		this.user = user;
		this.userpass=userpass;
		this.file = file;
		this.remotedir = remotedir;
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
		String perm=ac.getConfig("PERMISSION").toString();
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
		if (this.permission.length()==0) {
			if (perm.length()>0) {
				this.permission=perm;
			} else {
				this.permission="0600";
			}
		}
		//String characterSet = (charsetName ?: (charSet ? charSet.toString() : null))
			//	String permission =(mode ?: (perm ?perm.toString() :"0600"))
				
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
			if (this.characterSet!=null) {
				System.out.println(" >>>"+this.characterSet);
				scp.setCharset(this.characterSet);
			}
			System.out.println(" >>> p"+this.permission);
			scp.put(file,new Long(file.length()), remotedir,this.permission);
			
			conn.close();

			output = "File " + file + " should now be copied to " + host + ":"
					+ remotedir + "<br>";

		return output;
	}
}