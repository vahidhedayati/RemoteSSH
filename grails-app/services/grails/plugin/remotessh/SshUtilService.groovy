package grails.plugin.remotessh

import ch.ethz.ssh2.Connection
import ch.ethz.ssh2.SCPClient
import ch.ethz.ssh2.SFTPv3Client
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

class SshUtilService implements GrailsApplicationAware {

    /**
     * 0.10 introduces initialise  which simplifies and wraps requirements in
     * SSHUtil class
     * with initialise  connection, SCPClient and SFTPv3Client are all initiated
     * @return
     */

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


    /**
     * runs default no host no port which finds as per configuration file or defaults
     * @return sh2 Connection for reuse
     *
     * if you have ran intialise no need to run this
     */
    Connection getOpenConnection() {
        return openConnection()
    }

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
    Connection openConnection(String host=null, int port=0) {
        if (!host) {
            host=config?.HOST?.toString()?:"127.0.0.1"
        }
        if (!port) {
            port=((config?.PORT as int)?:22)
        }
        String username = config?.USER?.toString()?:'root'
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
        return SSHUtil.openConnection(host,port,username,keyfile,keyfilePass)
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
        return SSHUtil.openConnection(host,port,username,password)
    }


    String readRemoteFile(String remoteFile, Connection connection=null, boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        return readRemoteFile(new SSHUtil(connection,closeConnection),remoteFile)
    }

    /**
     * This is using SCPClient to read remote file
     * //sshUtil.remoteFile=remoteFile
     * //sshUtil.readRemoteFile()
     * @param sshUtil
     * @param remoteFile
     * @return
     * @throws Exception
     */
    String readRemoteFile(SSHUtil sshUtil,String remoteFile) throws Exception {
        return sshUtil.readRemoteFile(remoteFile)
    }


    String readFile(String remoteFile, Connection connection=null,boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        return readFile(new SSHUtil(connection,closeConnection),remoteFile)
    }

    /**
     * This is using SFTPv3Client to read file
     *
     * 	//sshUtil.remoteFile=remoteFile
     //return sshUtil.readFile()
     * @param sshUtil
     * @param remoteFile
     * @return
     * @throws Exception
     */
    String readFile(SSHUtil sshUtil,String remoteFile) throws Exception {
        return sshUtil.readFile(remoteFile)
    }

    /**
     * writes a file to remote server
     * @param localFile
     * @param remoteDir
     * @throws Exception
     */
    void writeFile(String localFile, String remoteDir,  Connection connection=null, boolean closeConnection=false, String characterSet=null) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        writeFile(new SSHUtil(connection,closeConnection),localFile,remoteDir)
    }

    /**
     * simplified version of original above
     *
     * Other ways
     * //sshUtil.localFile=localFile
     //sshUtil.remoteDir=remoteDir
     //sshUtil.writeFile()
     * @param sshUtil
     * @param localFile
     * @param remoteDir
     * @throws Exception
     */
    void writeFile(SSHUtil sshUtil,String localFile, String remoteDir) throws Exception {
        sshUtil.writeFile(localFile,remoteDir)
    }

    void writeFileWithName(String localFile, String remoteFile,  Connection connection=null, boolean closeConnection=false,String characterSet=null) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        writeFileWithName(new SSHUtil(connection,closeConnection),localFile,remoteFile)
    }
    /**
     * Other ways:
     * 	//sshUtil.localFile=localFile
     //sshUtil.remoteFile=remoteFile
     //return sshUtil.writeFileWithName()

     * @param sshUtil
     * @param localFile
     * @param remoteFile
     * @throws Exception
     */
    void writeFileWithName(SSHUtil sshUtil,String localFile, String remoteFile) throws Exception {
        sshUtil.writeFileWithName(localFile,remoteFile)
    }

    /**
     * Very similar to writeFile except SFTPv3Client  is used slightly differently to write file
     * @param localFile
     * @param remoteDir
     * @throws Exception
     */
    void putFile(String localFile, String remoteDir,  Connection connection=null, boolean closeConnection=false, String characterSet=null) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        putFile(new SSHUtil(connection,closeConnection),localFile,remoteDir)

    }

    /**
     * sshUtik.remoteDir=remoteDir
     * sshUtil.localFile=localFile
     * sshUtil.putFile()
     * @param sshUtil
     * @param localFile
     * @param remoteDir
     * @throws Exception
     */
    void putFile(SSHUtil sshUtil,String localFile, String remoteDir) throws Exception {
        sshUtil.putFile(localFile,remoteDir)
    }

    void putFiles(List<String> localFilePaths, String remoteFolder,Connection connection=null, boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        putFiles(new SSHUtil(connection,closeConnection),localFilePaths,remoteFolder)
    }
    void putFiles(SSHUtil sshUtil,List<String> localFilePaths ,String remoteFolder) throws Exception {
        sshUtil.putFiles(localFilePaths,remoteFolder)
    }

    void getFile(String remoteFilePath, String localFolder, Connection connection=null, boolean closeConnection=false,String characterSet=null) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        getFile(new SSHUtil(connection,closeConnection),remoteFilePath,localFolder)
    }
    /**
     *
     * Examples other than below:
     *
     * //sshUtil.localDir=localFolder
     //sshUtil.remoteFile=remoteFilePath
     //return sshUtil.getFile()

     * @param sshUtil
     * @param remoteFilePath
     * @param localFolder
     */
    void getFile(SSHUtil sshUtil,String remoteFilePath, String localFolder) {
        sshUtil.getFile(remoteFilePath,localFolder)
    }

    void getFiles(List<String> files, String localFolder,  Connection connection=null, boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        getFiles(new SSHUtil(connection,closeConnection),files,localFolder)
    }
    void getFiles(SSHUtil sshUtil,List<String> files, String localFolder) {
        sshUtil.getFiles(files,localFolder)
    }

    /**
     * Execute a command
     * @param cmd
     * @return
     * @throws Exception
     */
    String execute(Connection conn=null, String cmd, boolean closeConnection=false) throws Exception {
        if (!conn) {
            conn=openConnection
            closeConnection=true
        }
        return execute(new SSHUtil(conn,closeConnection),cmd)
    }

    /**
     //sshUtil.cmd=cmd
     //return sshUtil.execute()
     * @param sshUtil
     * @param cmd
     * @return
     */
    String execute(SSHUtil sshUtil,String cmd) {
        sshUtil.execute(cmd)
    }

    /**
     * checks to see if file exists
     * @param filename
     * @return
     * @throws Exception
     */
    boolean fileExists(String filename,Connection connection=null,boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        return fileExists(new SSHUtil(connection,closeConnection),filename)
    }

    /**
     * sshUtil.localFile=fileName
     * boolean fileExists = sshUtil.fileExists()
     * @param sshUtil
     * @param filename
     * @return
     */
    boolean fileExists(SSHUtil sshUtil,String filename) {
        return sshUtil.fileExists(filename)
    }

    /**
     * Deletes a remote file
     * @param pathName
     * @throws IOException
     */
    void deleteRemoteFile(String pathName,Connection connection=null,boolean closeConnection=false) throws IOException {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        deleteRemoteFile(new SSHUtil(connection,closeConnection),pathName)
    }

    void deleteRemoteFile(SSHUtil sshUtil,String remoteFile) {
        sshUtil.deleteRemoteFile(remoteFile)
    }

    long remoteFileSize(String remoteFile,Connection connection=null,boolean closeConnection=false) throws Exception {
        if (!connection) {
            connection=openConnection
            closeConnection=true
        }
        return remoteFileSize(new SSHUtil(connection,closeConnection),remoteFile)
    }
    /**
     * 	//sshUtil.remoteFile=remoteFile
     //return sshUtil.remoteFileSize()
     * @param sshUtil
     * @param remoteFile
     * @return
     */
    long remoteFileSize(SSHUtil sshUtil,String remoteFile) {
        return sshUtil.remoteFileSize(remoteFile)
    }

    void disconnect(SSHUtil sshUtil) {
        sshUtil.disconnect()
    }
    /**
     * Will decide on if closeConnection is provided if so will close connection
     * @param conn
     */
    void disconnectConnection(Connection conn, boolean closeConnection=true) {
        if (conn && closeConnection) {
            conn.close()
        }
    }


    void createRemoteDirs(Connection conn, SFTPv3Client sc, String path) throws Exception {
        SSHUtil sshUtil=new SSHUtil()
        sshUtil.connection=conn
        sshUtil.client=sc
        createRemoteDirs(sshUtil,path)
    }

    /**
     * createRemoteDirs remotely
     * @param sshUtil
     * @param path
     */
    void createRemoteDirs(SSHUtil sshUtil, String path) {
        sshUtil.createRemoteDirs(path)
    }

    SCPClient getScpClient(Connection connection, String characterSet=null) {
        return SSHUtil.getScpClient(connection,characterSet)
    }

    SFTPv3Client sftpClient(Connection connection, String characterSet=null) {
        return SSHUtil.sftpClient(connection,characterSet)
    }

    def config

    void setGrailsApplication(GrailsApplication grailsApplication) {
        config = grailsApplication.config.remotessh
    }

}