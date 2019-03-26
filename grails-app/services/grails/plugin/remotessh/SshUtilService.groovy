package grails.plugin.remotessh

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ch.ethz.ssh2.ChannelCondition
import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.SCPInputStream
import ch.ethz.ssh2.SFTPv3Client
import ch.ethz.ssh2.SFTPv3FileHandle
import ch.ethz.ssh2.Session
import ch.ethz.ssh2.StreamGobbler

class SshUtilService {
	def grailsApplication
	
	private static final Logger log = LoggerFactory.getLogger(this.class);
	
	
	/**
	 * @param host   - optional
	 * @param port   - optional
	 * Returns a default connection bound to local host
	 *
	 * It can be called as
	 *
	 * // this binds to HOST config or localhost
	 * // then binds config ports key/pass password username etc
	 * sshUtilService.openConnection
	 *
	 * or
	 * sshUtilService.getOpenConnection("10.0.0.1") //defaults port to PORT config or 22
	 * or
	 * sshUtilService.getOpenConnection("10.0.0.1",32) //where it connects to 10.0.0.1 on port 32
	 * @returns a ssh2 Connection for reuse
	 */
	Connection getOpenConnection(String host=null, int port=null) {
		if (!host) {
			host=config?.HOST?.toString()?:"127.0.0.1"
		}
		if (!port) {
			port=((config?.PORT as int)?:22)
		}
		return openConnection(host,port)
	}
	/**
	 * Part of above but simplified to simplify end user call to method
	 * @param host
	 * @param port
	 * @return
	 */
	Connection openConnection(String host, int port) {
		String username = config?.user?.toString()?:'root'
		String pass = config?.PASS?.toString()
		String sshkey=config?.KEY?.toString()
		String sshkeypass=config.KEYPASS?.toString()
		if (sshkey && sshkeypass) {
			return openConnection(host,port,username,sshkey,sshkeypass)
		} else if (pass) {
			return openConnection(host,port,username,pass)
		}
	}
	/**
	 * openConnection(host,port,username,keyfile,keyfilepass)
	 * @param host
	 * @param port
	 * @param username
	 * @param keyfile
	 * @param keyfilePass
	 * @return ssh2 Connection
	 */
	Connection openConnection(String host, int port, String username, String keyfile, String keyfilePass) {
		Connection conn = new Connection(host, port)
		try {
			conn.connect()
			if (conn.authenticateWithPublicKey(username,keyfile, keyfilePass)) {
				return conn
			}
			log.warn "SSH Authentication failed.";
			conn.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e)
		}
		return null
	}
	
	/**
	 * openConnection(host,port,username,pass)
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return ssh2 connection
	 */
	Connection openConnection(String host, int port, String username, String password) {
		Connection conn = new Connection(host, port)
		try {
			conn.connect()
			if (conn.authenticateWithPassword(username,password)) {
				return conn
			}
			log.warn "SSH Authentication failed."
			conn.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e)
		}
		return null
	}
	
	String readRemoteFile(String remoteFile, Connection connection=null, boolean closeConnection=false) {
		if (!connection) {
			connection=openConnection
		}
		
		StringBuffer resultBuffer = new StringBuffer()
		try {
			SCPClient client = new SCPClient(connection)
			SCPInputStream scpInputStream = client.get(remoteFile)
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(scpInputStream))
			String data
			while ((data = bufferedReader.readLine()) != null) {
				resultBuffer.append(data + "\n")
			}
			return resultBuffer.toString()
		} catch (IOException e) {
			log.error(e.getMessage(), e)
		} finally {
			if (closeConnection) {
				connClose(connection)
			}
			
		}
	}
	
	void readFile(String remoteFile,  Connection connection=null) {
		if (!connection) {
			connection=openConnection
		}
		try {
			SFTPv3Client sftPv3Client = new SFTPv3Client(connection)
			SFTPv3FileHandle sftPv3FileHandle = sftPv3Client.openFileRO(remoteFile)
			byte[] bs = new byte[11]
			int i = 0
			long offset = 0
			while (i != -1) {
				i = sftPv3Client.read(sftPv3FileHandle, offset, bs, 0, bs.length)
				offset += i
			}
			System.out.println(new String(bs))

		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	void writeFile(String localFile, String remoteDir,  Connection connection=null, String characterSet=null) {
		if (!connection) {
			connection=openConnection
		}
		FileOutputStream out =  null;
		try {
			File actualFile = new File(localFile);
			out = new FileOutputStream(remoteDir+File.separator+actualFile.getName());
			
			SFTPv3Client sFTPv3Client = new SFTPv3Client(connection);
			if (characterSet) {
				sFTPv3Client.setCharset(characterSet)
			}
			SFTPv3FileHandle handle = sFTPv3Client.openFileRO(localFile);
			byte[] cache = new byte[1024];
			int i = 0;
			int offset = 0;
			while((i = sFTPv3Client.read(handle, offset, cache, 0, 1024)) != -1){
				out.write(cache, 0, i);
				offset += i;
			}
			sFTPv3Client.closeFile(handle);
			if (handle.isClosed()){
				sFTPv3Client.close();
			}
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	void writeFileWithName(String localFile, String remoteFile,  Connection connection=null, String characterSet=null) {
		if (!connection) {
			connection=openConnection
		}
		FileOutputStream out =  null;
		try {
			File actualFile = new File(localFile)
			SFTPv3Client sftPv3Client = new SFTPv3Client(connection)
			if (characterSet) {
				sftPv3Client.setCharset(characterSet)
			}
			sftPv3Client.createFile(remoteFile)
			SFTPv3FileHandle sftPv3FileHandle = sftPv3Client.openFileWAppend(remoteFile)
			byte[] bytesArray = new byte[(int) actualFile.length()]
			FileInputStream fileInputStream  = new FileInputStream(actualFile)
			fileInputStream.read(bytesArray)
			fileInputStream.close()
			sftPv3Client.write(sftPv3FileHandle, 0, bytesArray, 0, bytesArray.length)
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	

	String execute(Connection conn=null, String cmd) {
		if (!conn) {
			conn=openConnection
		}
		Session session
		StringBuffer buffer = new StringBuffer()
		if (!cmd||!conn) {
			return null
		}
		InputStream stdOut
		try {
			session = conn.openSession()
			session.execCommand(cmd)
			stdOut = new StreamGobbler(session.getStdout())
			BufferedReader stdOutRead = new BufferedReader(new InputStreamReader(stdOut))
			String line = stdOutRead.readLine()
			while (line != null) {
				buffer.append(line)
				buffer.append(System.getProperty("line.separator"))
				line = stdOutRead.readLine()
			}
			session.waitForCondition(ChannelCondition.EXIT_STATUS, Long.MAX_VALUE)
			int ret = session.getExitStatus()
			if (ret != 0) {
				log.error("exec_error")
			}
			return buffer.toString()
		} catch (Exception e) {
			log.error(e.getMessage(), e)
		} finally {
			session.close()
		}
		return null
	}

	void connClose(Connection conn) {
		if (conn) {
			conn.close()
		}
	}

	void connTest() {
		Connection conn
		try {
			conn = openConnection
		} finally {
			connClose(conn)
		}
	}

	public void executeTest() {
		String cmd = "uname -a;";
		cmd += "netstat -s;";
		cmd += "df -h | awk 'NR==2 {print \$2}';";
		cmd += "free -m | awk 'NR==2 {print \$2}';";
		cmd += "vmstat 1 3 |tail -1 |awk '{printf(\"%s\\n\",\$15)}';";
		BigDecimal b1 = new BigDecimal(361);
		BigDecimal b2 = new BigDecimal(2000);
		double usageMemory = b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
		println(usageMemory * 100 + "%");
		Connection conn = openConnection
		String execute = execute(conn, cmd);
		connClose(conn);
		System.out.println(execute)

	}
	
	public void readFileTest() {
		Connection conn = openConnection
		String remoteFile = "/tmp/test.txt"
				readRemoteFile(remoteFile, conn);
				readFile(remoteFile,conn);
		//writeFile(remoteFile, conn)
		connClose(conn)
	}
	
	private ConfigObject getConfig() {
		grailsApplication.config?.remotessh
	}
}
