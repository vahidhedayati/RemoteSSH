package grails.plugin.remotessh

import ch.ethz.ssh2.*


class RsshService extends RsshConfService{

	static transactional = false



	def runCommand(Map params) {
		StringBuilder output = new StringBuilder()

		Map pm = validateParams(params)
		String host = pm.host
		int sshport = pm.sshport
		File keyfile = pm.keyfile
		String sshkeypass = pm.sshkeypass
		String sshuser = pm.sshuser
		String sshpass = pm.sshpass
		String usercommand = pm.usercommand
		String sudo = pm.sudo
		String filter = pm.filter
		String splitter = pm.splitter
		// Do initial connection
		Connection conn = connect(host,sshport)
		// If we have a connection
		if (conn) {
			// grab the ssh session
			Session sess = openSession(conn, keyfile,sshkeypass,sshuser,sshpass)
			if (sess) {
				// if we have session run the command
				// this is where you could keep connected and have a session
				// in your own calls and run variety of commands if you wish
				// to call this service in segments
				output = executeCommand(sess, conn, usercommand, splitter, sudo, filter)
				closeConnection(conn,sess)
			}else{
				output << "No session $keyfile $sshkeypass $sshuser $sshpass"
			}
		}else{
			output << "No connection $host $sshport"
		}
		return output.toString()
	}

	public String scpDir(Map params){
		String output = ''
		Map pm = validateParams(params)
		String host = pm.host
		int sshport = pm.sshport
		File keyfile = pm.keyfile
		String sshkeypass = pm.sshkeypass
		String sshuser = pm.sshuser
		String sshpass = pm.sshpass
		String remotedir = pm.remotedir
		String localdir = pm.localdir
		String splitter = pm.splitter
		// Do initial connection
		Connection conn = connect(host,sshport)
		// If we have a connection
		if (conn) {
			Session sess = openSession(conn, keyfile,sshkeypass,sshuser,sshpass)
			if (sess) {
				scpDir(conn, sess, localdir, remotedir, "0600")
				closeConnection(conn,sess)
				output = "$localdir should now be copied to $host:$remotedir${splitter}"
			}else{
				output = "No session $keyfile $sshkeypass $sshuser $sshpass"
			}

		}else{
			output = "No connection $host $sshport"
		}
		return output
	}

	public String scpFile(Map params) {
		String output = ''
		Map pm = validateParams(params)
		String host = pm.host
		int sshport = pm.sshport
		File keyfile = pm.keyfile
		String sshkeypass = pm.sshkeypass
		String sshuser = pm.sshuser
		String sshpass = pm.sshpass
		String file = pm.file
		String remotedir = pm.remotedir
		String splitter = pm.splitter
		// Do initial connection
		Connection conn = connect(host,sshport)
		if (conn) {
			Session sess = openSession(conn, keyfile,sshkeypass,sshuser,sshpass)
			if (sess) {
				scpFile(conn, file, remotedir)
				closeConnection(conn,sess)
			}else{
				output = "No session $keyfile $sshkeypass $sshuser $sshpass"
			}
			output = "File $file should now be copied from $host to $remotedir: $file${splitter}"
		}else{
			log.error "No connection $host $sshport"
		}
		return output
	}

	public String scpGet(Map params) {
		String output = ''
		Map pm = validateParams(params)
		String host = pm.host
		int sshport = pm.sshport
		File keyfile = pm.keyfile
		String sshkeypass = pm.sshkeypass
		String sshuser = pm.sshuser
		String sshpass = pm.sshpass
		String file = pm.file
		String localdir = pm.localdir
		String splitter = pm.splitter
		// Do initial connection
		Connection conn = connect(host,sshport)
		if (conn) {
			Session sess = openSession(conn, keyfile,sshkeypass,sshuser,sshpass)
			if (sess) {
				scpGet(conn, file, localdir)
				closeConnection(conn,sess)
			}else{
				output = "No session $keyfile $sshkeypass $sshuser $sshpass"
			}
			output = "File $file should now be copied from $host to ${localdir}:${file}${splitter}"
		}else{
			log.error "No connection $host $sshport"
		}
		return output
	}

	//Simply just connect
	public Connection connect(String host, int sshport) {
		Connection conn = new Connection(host,sshport)
		conn.connect()
		return conn
	}

	//Once we have above connection we should attempt to get ssh session
	public Session openSession(Connection conn, File keyfile,String keyfilePass,String sshuser,String sshpass=null){
		Session sess
		try {
			boolean isAuthenticated=false
			if (!sshpass) {
				isAuthenticated = conn.authenticateWithPublicKey(sshuser,
						keyfile, keyfilePass)
			}else{
				isAuthenticated = conn.authenticateWithPassword(sshuser,sshpass)
			}

			if (!isAuthenticated)
				throw new IOException("Authentication failed.")

			sess = conn.openSession()
		}catch(Exception e) {
			log.error e
		}
		return sess
	}

	// Now that we have session and connection lets attempt to execute actual usercommand
	public StringBuilder executeCommand(Session sess,Connection conn, String usercommand, String splitter,
		String sudo=null, String filter=null, boolean hasConnection=null) {
		
		StringBuilder output = new StringBuilder()

		if (!hasConnection){
			sess.requestPTY("vt220")
		}else{
			sess = conn.openSession()
		}
		if (sudo) {
			if (sudo == "sudo") {
				sess.execCommand("sudo bash")
			}else{
				sess.execCommand(sudo)
			}
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
				output.append(line).append(splitter)
			} else {
				if (line.startsWith(filter)) {
					output.append(line).append(splitter)
				}
			}
		}
		return output
	}


	void scpDir(Connection conn, Session sess, String localDirectory,
			String remoteTargetDirectory, String mode) throws IOException {

		File curDir = new File(localDirectory)
		final String[] fileList = curDir.list()
		for (String file : fileList) {
			final String fullFileName = "$localDirectory/$file"
			if (new File(fullFileName).isDirectory()) {
				final String subDir = "$remoteTargetDirectory/$file"
				sess = conn.openSession()
				sess.execCommand("mkdir $subDir")
				sess.waitForCondition(ChannelCondition.EOF, 0)
				scpDir(conn, sess, fullFileName, subDir, mode)
				sleep(200)
			} else {
				SCPClient scpc = conn.createSCPClient()
				scpc.put(fullFileName, remoteTargetDirectory, mode)
			}
		}
	}

	public void scpFile(Connection conn, String file, String remotedir) {
		SCPClient scp = conn.createSCPClient()
		scp.put(file, remotedir)
	}

	public void scpGet(Connection conn, String file, String localdir) {
		SCPClient scp = conn.createSCPClient()
		scp.get(file, localdir)
	}


	public def closeConnection( Connection conn,Session sess=null) {
		if (sess) {
			closeSession(sess)
		}
		if (conn) {
			conn.close()
		}
	}

	public def closeSession( Session sess) {
		if (sess) {
			sess.close()
		}
	}

	public Map validateParams(Map params) {

		//generic params
		String sshuser = params.user ?: config.USER
		String sshpass = params.pass ?: config.PASS
		String sshkey = params.sshkey ?: config.KEY
		String sshkeypass = params.sshkeypass ?: config.KEYPASS
		int sshport = (params.port ?: config.PORT as int) ?: 22
		String host = params.host ?: 'localhost'
		String splitter = params.splitter ?: '<br>'
		File keyfile = new File(sshkey.toString())

		//runCommand params
		String usercommand = params.usercommand
		String sudo = params.sudo
		String filter = params.filter

		//scpDir commands
		String localDirectory = params.localDirectory
		String remoteTargetDirectory = params.remoteTargetDirectory
		String mode = params.mode ?: "0600"

		//scpGet commands and scp
		String file = params.file
		String localdir = params.localdir
		String remotedir = params.remotedir

		return [sshuser:sshuser,sshpass:sshpass,sshkey:sshkey,sshkeypass:sshkeypass,sshport:sshport,host:host,keyfile:keyfile,
			usercommand:usercommand,sudo:sudo,filter:filter,
			localDirectory:localDirectory,remoteTargetDirectory:remoteTargetDirectory,mode:mode,
			file:file, localdir:localdir, remotedir:remotedir, splitter:splitter]
	}

}
