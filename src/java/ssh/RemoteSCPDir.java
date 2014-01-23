package ssh;

import grails.plugin.remotessh.SshConfig;

import java.io.File;
import java.io.IOException;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

/**
 * Called by IncludeUpdate class scps a folder to specified end server.
 */
public class RemoteSCPDir  {

	String hostname = "";
	String user = "";
	Integer port=0;
	String userpass="";
	String localdir = "";
	String remotedir = "";
	String usercommand = "";
	String output = "";

	public RemoteSCPDir(String hostname, String localdir,String remotedir) {
		this.hostname = hostname;
		this.localdir = localdir;
		this.remotedir = remotedir;
	}
	public RemoteSCPDir(String hostname, String user, String localdir,String remotedir) {
		this.hostname = hostname;
		this.user = user;
		this.localdir = localdir;
		this.remotedir = remotedir;
	}
	public RemoteSCPDir(String hostname, String user,String userpass, String localdir,String remotedir) {
		this.hostname = hostname;
		this.user = user;
		this.userpass=userpass;
		this.localdir = localdir;
		this.remotedir = remotedir;
	}
	public RemoteSCPDir(String hostname, String localdir,String remotedir, Integer port) {
		this.hostname = hostname;
		this.localdir = localdir;
		this.remotedir = remotedir;
		this.port=port;
	}
	public RemoteSCPDir(String hostname, String user, String localdir,String remotedir, Integer port) {
		this.hostname = hostname;
		this.user = user;
		this.localdir = localdir;
		this.remotedir = remotedir;
		this.port=port;
	}
	public RemoteSCPDir(String hostname, String user, String userpass, String localdir,String remotedir, Integer port) {
		this.hostname = hostname;
		this.user = user;
		this.userpass=userpass;
		this.localdir = localdir;
		this.remotedir = remotedir;
		this.port=port;
	}

	public String Result(SshConfig ac) throws IOException{
		Object sshuser=ac.getConfig("USER");
		Object sshpass=ac.getConfig("PASS");
		Object sshkey=ac.getConfig("KEY");
		Object sshkeypass=ac.getConfig("KEYPASS");
		Object sshport=ac.getConfig("PORT");
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
			// Session sess = conn.openSession();
			// sess.execCommand("mkdir -p " + remotedir);
			putDir(conn, localdir, remotedir, "0600");
			conn.close();
			output = "" + localdir + " should now be copied to " + hostname
					+ ":" + remotedir + "<br>";
		return output;
	}

	private static void putDir(Connection conn, String localDirectory,
			String remoteTargetDirectory, String mode) throws IOException {

		File curDir = new File(localDirectory);
		final String[] fileList = curDir.list();
		for (String file : fileList) {
			final String fullFileName = localDirectory + "/" + file;
			if (new File(fullFileName).isDirectory()) {
				final String subDir = remoteTargetDirectory + "/" + file;
				Session sess = conn.openSession();
				sess.execCommand("mkdir " + subDir);
				sess.waitForCondition(ChannelCondition.EOF, 0);
				putDir(conn, fullFileName, subDir, mode);
			} else {
				SCPClient scpc = conn.createSCPClient();
				scpc.put(fullFileName, remoteTargetDirectory, mode);
			}
		}
	}
}
