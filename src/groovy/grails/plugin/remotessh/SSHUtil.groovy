package grails.plugin.remotessh

import grails.util.Holders

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ch.ethz.ssh2.ChannelCondition
import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.SCPInputStream
import ch.ethz.ssh2.SFTPException
import ch.ethz.ssh2.SFTPv3Client
import ch.ethz.ssh2.SFTPv3FileAttributes
import ch.ethz.ssh2.SFTPv3FileHandle
import ch.ethz.ssh2.Session
import ch.ethz.ssh2.StreamGobbler

/**
 * 
 * Utility class that binds into SshUtilService and does all the work
 * 
 * It is based flexible so it can either be instantiated or just called direct through service which instantiates once 
 * and reused if used correctly. Since service left rather flexible in ways things can be called
 * 
 * 
 * @author Vahid Hedayati 27th March 2019
 *
 */

class SSHUtil {
	
	private static final Logger log = LoggerFactory.getLogger(this.class)
	private static final int BUFSIZE = 1024
	
	public static final int S_IFMT = 0170000 // bitmask for the file type bitfields
	public static final int S_IFDIR = 0040000 // directory
	
	Connection connection
	SFTPv3Client client
	SCPClient scpClient
	
	SFTPv3FileHandle handle
	String characterSet
	
	/*When set this means we need to disconnect after running whatever defined as part of original initialisation
	 * within sshUtilService :
	 * where singleInstance is true/false
	 * SSH2Config ssh2Config = sshUtilService.initialise  //this is configured to be non single instance so false
	 * SSH2Config ssh2Config = sshUtilService.initialise(singleInstance)
	 * SSH2Config ssh2Config = sshUtilService.initialise(username, password, host, port, singleInstance)
	 * SSH2Config ssh2Config = sshUtilService.initialise(username, keyFile,keyFilePass, host, port, singleInstance)
	 */
	boolean singleInstance
	
	String remoteFile
	String remoteDir
	String localFile
	String localDir
	File actualFile
	//String remoteFilePath
	String cmd
	
	SSHUtil() {
		
	}
	SSHUtil(Connection connection,boolean singleInstance=false,String characterSet=null) {
		this.connection=connection
		this.client=sftpClient(connection,characterSet)
		this.scpClient=getScpClient(connection,characterSet)
	}
	SSHUtil(Connection connection,SFTPv3Client client,boolean singleInstance=false) {
		this.connection=connection
		this.client=client
		this.singleInstance=singleInstance
	}
	
	SSHUtil(Connection connection,SFTPv3Client client,SFTPv3FileHandle handle,boolean singleInstance=false) {
		this.connection=connection
		this.client=client
		this.handle=handle
		this.singleInstance=singleInstance
	}
	SSHUtil getInitialise() {
		Connection connection = openConnection
		return new SSHUtil(connection)
	}
	SSHUtil initialise(boolean singleInstance=false) {
		Connection connection = openConnection
		return new SSHUtil(connection,singleInstance)
	}
	SSHUtil initialise(String host, int port,boolean singleInstance=false) {
		Connection connection = openConnection(host,port)
		return new SSHUtil(connection,singleInstance)
	}
	SSHUtil initialise(String username, String password, String host, int port, boolean singleInstance=false) {
		Connection connection = openConnection(host,port,username,password)
		return new SSHUtil(connection,singleInstance)
	}
	
	SSHUtil initialise(String username, String keyfile, String keyfilePass,String host, int port, boolean singleInstance=false) {
		Connection connection = openConnection(host,port,username,keyfile,keyfilePass)
		return new SSHUtil(connection,singleInstance)
	}
	
	SSHUtil initialise(String username, String password,String host, int port, String characterSet, boolean singleInstance=false) {
		Connection connection = openConnection(host,port,username,password)
		return new SSHUtil(connection,singleInstance,characterSet)
	}
	
	SSHUtil initialise(String username, String keyfile, String keyfilePass,String host, int port, String characterSet, boolean singleInstance=false) {
		Connection connection = openConnection(host,port,username,keyfile,keyfilePass)
		return new SSHUtil(connection,singleInstance,characterSet)
	}
	

	String readRemoteFile(String givenFile=null) throws Exception {
		StringBuffer resultBuffer = new StringBuffer()
		SCPInputStream scpInputStream = scpClient.get(givenFile?givenFile:remoteFile)
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(scpInputStream))
		String data
		while ((data = bufferedReader.readLine()) != null) {
			resultBuffer.append(data + "\n")
		}
		finaliseConnection()
		return resultBuffer.toString()
	}
	
	String readFile(String givenFile=null) throws Exception {
		handle =client.openFileRO(givenFile?givenFile:remoteFile)
		byte[] bs = new byte[11]
		int i = 0
		long offset = 0
		while (i != -1) {
			i = client.read(handle, offset, bs, 0, bs.length)
			offset += i
		}
		return new String(bs)
		finaliseConnection()
	}
	
	void initialiseRemoteFile(String localfile=null, String remotedir=null) {
		this.localFile=localfile?localfile:localFile
		if (localFile) {
			this.actualFile = new File(localFile)
		}
		this.remoteDir=remotedir?remotedir:remoteDir
		if (this.actualFile && this.remoteDir && !this.remoteFile) {
			this.remoteFile = remoteDir + File.separator+ actualFile.getName()
		}
	}
	
	void writeFile(String localfile=null, String remotedir=null) throws Exception {
		initialiseRemoteFile(localfile,remotedir)
		FileOutputStream out = new FileOutputStream(remoteFile);
		
		createRemoteDirs(remoteDir)
		
		 handle = client.openFileRO(localFile)
		
		byte[] cache = new byte[BUFSIZE];
		int i = 0
		int offset = 0
		while((i = client.read(handle, offset, cache, 0, BUFSIZE)) != -1){
			out.write(cache, 0, i)
			offset += i
		}
		finaliseConnection()
	}
	
	void createRemoteDirs(String remotedir=null) {
		String path = remotedir?remotedir:remoteDir
		String FS = System.getProperty("file.separator");
		int index = path.lastIndexOf(FS);
		if (index > 1) {
			createRemoteDirs(path.substring(0, index));
		}
		try {
			SFTPv3FileAttributes attribs = client.stat(path);
			if (!((attribs.permissions & S_IFDIR) == S_IFDIR)) {
				throw new IOException(path + " is not a folder");
			}
		} catch (SFTPException e) {
			client.mkdir(path, 0777);
			log.debug("Remote folder created " + path);
		}
	}
	
	long remoteFileSize(String remotefile=null) throws Exception {
		long lngFileSize = -1;
		try {
			SFTPv3FileAttributes objAttr = client.stat(remotefile?remotefile:remoteFile)
			if (objAttr != null) {
				lngFileSize = objAttr.size.longValue()
			}
		} catch (SFTPException e) {
		}
		finaliseConnection()
		return lngFileSize
	}
	
	void writeFileWithName(String localfile=null, String remotefile=null) throws Exception {
		this.remoteFile=remotefile?:remoteFile
		initialiseRemoteFile(localfile,remotefile)
		println " -- ${remoteFile} vs ${localFile}"
		File actualFile = new File(localFile)
		SFTPv3Client sftPv3Client = new SFTPv3Client(connection)
		if (characterSet) {
			sftPv3Client.setCharset(characterSet)
		}
		sftPv3Client.createFile(remoteFile)
		handle = sftPv3Client.openFileWAppend(remoteFile)
		byte[] bytesArray = new byte[(int) actualFile.length()]
		FileInputStream fileInputStream  = new FileInputStream(actualFile)
		fileInputStream.read(bytesArray)
		fileInputStream.close()
		sftPv3Client.write(handle, 0, bytesArray, 0, bytesArray.length)
		finaliseConnection()
	}
	
	void getFile(String remotefile=null, String localdir=null) throws Exception {
		this.remoteFile=remotefile?remotefile:remoteFile
		String fileName =(new File(remoteFile)).getName()
		this.localDir=localdir?:localDir
		this.localFile = localDir + File.separator + fileName
		
		log.debug("Getting file " + remoteFile + " to local folder " + localFile)
		
		this.actualFile = new File(localFile)
		handle = client.openFileRO(remoteFile)
		BufferedOutputStream bfout = new BufferedOutputStream(new FileOutputStream(actualFile))
		byte[] buf = new byte[BUFSIZE]
		int count = 0
		int bufsiz = 0
		while ((bufsiz = client.read(handle, count, buf, 0, BUFSIZE)) != -1) {
			bfout.write(buf, 0, bufsiz)
			count += bufsiz
		}
		bfout.close()
		finaliseConnection()
	}
	void getFiles(List<String> files, String localfolder=null) {
		this.localDir=localfolder?:localDir
		files?.each {file->
			getFile(file, localDir)
		}
		finaliseConnection()
	}
	void putFile(String localfile=null, String remotedir=null) throws Exception {
		initialiseRemoteFile(localfile,remotedir)
		createRemoteDirs(remoteDir)
		client.createFile(remoteFile);
		handle = client.openFileRW(remoteFile);
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(localFile));
		byte[] buf = new byte[BUFSIZE];
		int count = 0;
		int bufsiz = 0;
		while ((bufsiz = is.read(buf)) != -1) {
			//here the writing is made on the remote file
			client.write(handle, (long) count, buf, 0, bufsiz);
			count += bufsiz;
		}
		is.close()
		finaliseConnection()
	}
	void putFiles(List<String> localFilePaths, String remotefolder) {
		this.remoteDir=remotefolder?:remoteDir
		localFilePaths?.each { file->
			putFile(file,remoteDir)
		}
		finaliseConnection()
	}
	
	
	String execute(String command=null) throws Exception {
		this.cmd=command?:cmd
		Session session
		StringBuffer buffer = new StringBuffer()
		if (!cmd) {
			return null
		}
		InputStream stdOut
		try {
			session = connection.openSession()
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
			finaliseConnection()
		} catch (Exception e) {
			log.error(e.getMessage(), e)
		} finally {
			session.close()
		}
		return buffer.toString()
	}
	
	void deleteRemoteFile(String pathName) throws IOException {
		client.rm(pathName)
		finaliseConnection()
	}
	
	boolean fileExists(String filename=null) throws Exception {
		boolean state
		try {
			SFTPv3FileAttributes attributes = client.stat(filename?:localFile)
			if (attributes != null) {
				state=attributes.isRegularFile() || attributes.isDirectory()
			} else {
				state=false;
			}
		} catch (Exception e) {
			state=false;
		}
		finaliseConnection()
		return state
	}
	
	//---------------------- STATIC METHODS BELOW -------------------------//
	/**
	 * openConnection(host,port,username,keyfile,keyfilepass)
	 * @param host
	 * @param port
	 * @param username
	 * @param keyfile
	 * @param keyfilePass
	 * @return ssh2 Connection
	 */
	public static Connection openConnection(String host, int port, String username, String keyfile, String keyfilePass) {
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
	
	Connection getOpenConnection() {
		return openConnection()
	}
	
	Connection openConnection(String host=null, int port=null) {
		if (!host) {
			host=getConfig("HOST")?.toString()?:"127.0.0.1"
		}
		if (!port) {
			port=((getConfig("PORT") as int)?:22)
		}
		String username = getConfig("USER")?.toString()?:'root'
		String pass = getConfig("PASS")?.toString()
		String sshkey=getConfig("KEY")?.toString()
		String sshkeypass=getConfig("KEYPASS")?.toString()
		if (sshkey && sshkeypass) {
			return openConnection(host,port,username,sshkey,sshkeypass)
		} else if (pass) {
			return openConnection(host,port,username,pass)
		}
	}
	/**
	 * decides if single instance and closes sftp client as well as ssh2 connection to server
	 */
	void finaliseConnection(boolean override=false) throws Exception {
		try {
			if (handle) {
				closeHandle()
			}
			if (this.singleInstance||override) {
				if (handle.isClosed()){
					client.close()
				}
				connection.close()
			}
		} catch (Exception) {
		}
	}
	
	void closeHandle(boolean override=true) {
		try {
			if (override && handle && client) {
				client.closeFile(handle)
			}
		} catch (Exception) {
			
		}
	}
	
	/**
	 * openConnection(host,port,username,pass)
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @return ssh2 connection
	 */
	public static Connection openConnection(String host, int port, String username, String password) {
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
	
	
	public static SCPClient getScpClient(Connection connection, String characterSet=null) {
		SCPClient client = new SCPClient(connection)
		if (characterSet) {
			client.setCharset(characterSet)
		}
		return client
	}
	
	public static SFTPv3Client sftpClient(Connection connection, String characterSet=null) {
		SFTPv3Client client = new SFTPv3Client(connection)
		if (characterSet) {
			client.setCharset(characterSet)
		}
		return client
	}
	
	
	
	static def getConfig(String configProperty) {
		Holders.grailsApplication.config.remotessh[configProperty] ?: ''
   }
}
