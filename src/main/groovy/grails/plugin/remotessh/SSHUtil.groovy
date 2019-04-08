package grails.plugin.remotessh

import ch.ethz.ssh2.*
import grails.util.Holders
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
    protected String reply="OK"
    private String currentDirectory=""
    private static final Logger log = LoggerFactory.getLogger(this.class)
    private static final int BUFSIZE = 1024

    public static final int S_IFMT = 0170000 // bitmask for the file type bitfields
    public static final int S_IFDIR = 0040000 // directory
    private String FS = System.getProperty("file.separator")
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

    /*
     * Please note you can point to another configuration file if needed to do some of this connection
     *
     * so SSHUtil sshUtil = new SSHUtil()
     * sshUtil.configVariable='mySshConfigVar'
     * sshUtil.initialise
     * // sshUtil.initialise('someHost',22)
     * sshUtil.localFile='/tmp/test.txt'
     * boolean doesItExist = sshUtil.fileExists()
     * println "file exists = ${doesItExist}"
     * sshUtil.delete('/tmp/test.txt')
     *  doesItExist = sshUtil.fileExists()
     * println "file exists = ${doesItExist}"
     */
    String configVariable='remotessh'
    Map cfg=[:]
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


    /**
     * Creates a new subdirectory on the FTP server in the current directory .
     * @param pstrPathName The pathname of the directory to create.
     * @return True if successfully completed, false if not.
     * @throws java.lang.IOException
     */

    void mkdir(final String pstrPathName) {
        try {
            reply = "mkdir OK"
            String strPath = ""
            if (pstrPathName.startsWith(FS)) {
                strPath = FS
            }
            String[] strP = pstrPathName.split(FS)
            for (String strSubFolder : strP) {
                if (strSubFolder.trim().length() > 0) {
                    strPath += strSubFolder + FS
                    SFTPv3FileAttributes objFA
                    try {
                        objFA = client.lstat(strPath)
                    }
                    catch (Exception e) {
                        objFA = null;
                    }
                    //&& client.lstat(strPath).isDirectory() == false
                    if (!objFA) {
                        client.mkdir(strPath, 484)
                        log.debug("[mkdir]"+ strPath)
                    }
                }
            }
        } catch (IOException e) {
            throw new Exception("makeDirectory()", e);
        }
        finaliseConnection()
    }

    /**
     * Removes a directory on the FTP server (if empty).
     * @param pathname The pathname of the directory to remove.
     * @throws java.lang.IOException
     */

    void rmdir(String pstrPathName, boolean recursive=true) throws IOException {
        try {
            String[] strP = pstrPathName.split(FS);
            reply = "rmdir OK";
            if (strP) {
                int p=0
                for (int i = strP.length; i > 0; i--) {

                    String strT = "";
                    for (int j = 0; j < i; j++) {
                        strT += strP[j] + FS;
                    }
                    if ((!recursive && p==0||recursive)  && client.lstat(strT).isDirectory()) {
                        log.debug("[rmdir]"+ strT)
                        client.rmdir(strT)
                    }
                    p++
                }
            }
        } catch (Exception e) {
            //log.error ("removeDirectory()", e)
            throw new Exception("removeDirectory()", e)
        }
        finaliseConnection()
    }

    /**
     * Reads string content of remote file
     * @return
     * @throws Exception
     */
    String readRemoteFile(String givenFile=null) throws Exception {
        StringBuffer resultBuffer = new StringBuffer()
        remoteFile=givenFile?:remoteFile
        if (remoteFile) {
            SCPInputStream scpInputStream = scpClient.get(remoteFile)
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(scpInputStream))
            String data
            while ((data = bufferedReader.readLine()) != null) {
                resultBuffer.append(data + "\n")
            }
        }
        finaliseConnection()
        return resultBuffer.toString()
    }

    String readFile(String givenFile=null) throws Exception {
        byte[] bs = new byte[11]
        remoteFile=givenFile?:remoteFile
        if (remoteFile) {
            handle =client.openFileRO(remoteFile)
            int i = 0
            long offset = 0
            while (i != -1) {
                i = client.read(handle, offset, bs, 0, bs.length)
                offset += i
            }

        }
        finaliseConnection()
        return new String(bs)
    }

    private void initialiseRemoteFile(String localfile=null, String remotedir=null, boolean overrideRemoteFile=false) {
        this.localFile=localfile?localfile:localFile
        if (localFile) {
            this.actualFile = new File(localFile)
        }
        this.remoteDir=remotedir?remotedir:remoteDir
        if (this.actualFile && this.remoteDir && (!this.remoteFile||overrideRemoteFile)) {
            this.remoteFile = remoteDir + File.separator+ actualFile.getName()
        }
    }

    void writeFile(String localfile=null, String remotedir=null) throws Exception {
        initialiseRemoteFile(localfile,remotedir,true)
        if (remoteFile && localFile && remoteDir) {
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
        }
        finaliseConnection()
    }

    void getMkDirs()  throws Exception {
        createRemoteDirs()
    }
    void mkDirs(String remotedir=null)  throws Exception {
        createRemoteDirs(remotedir?:remoteDir)
    }

    void createRemoteDirs(String remotedir=null)  {
        String path = remotedir?remotedir:remoteDir
        if (path) {
            try {
                int index = path.lastIndexOf(FS);
                if (index > 1) {
                    createRemoteDirs(path.substring(0, index))
                }
                SFTPv3FileAttributes attribs
                try {
                    attribs = client.stat(path)
                    if (!((attribs.permissions & S_IFDIR) == S_IFDIR)) {
                        throw new IOException(path + " is not a folder")
                    }
                    //} catch (SFTPException e) {
                    //	attribs=null
                } catch (Exception e) {
                    attribs=null
                }
                if (!attribs) {
                    client.mkdir(path, 0777)
                    log.debug("Remote folder created " + path)
                }

            } catch (IOException e) {
                log.debug("createRemoteDirs()", e)
                throw new Exception("createRemoteDirs()", e);
            }

        }
        finaliseConnection()
    }

    long remoteFileSize(String remotefile=null) {
        long lngFileSize = -1;
        remoteFile=remotefile?:remoteFile
        if (remoteFile) {
            try {
                SFTPv3FileAttributes objAttr = client.stat(remoteFile)
                if (objAttr != null) {
                    lngFileSize = objAttr.size.longValue()
                }
            } catch (SFTPException e) {
            }
        }
        finaliseConnection()
        return lngFileSize
    }

    void writeFileWithName(String localfile=null, String remotefile=null) throws Exception {
        this.remoteFile=remotefile?:remoteFile
        try {
            initialiseRemoteFile(localfile,remotefile)
            if (remoteFile && localFile) {
                File actualFile = new File(localFile)
                if (remoteFile.contains(FS)) {
                    createRemoteDirs(remoteFile?.substring(0,remoteFile.lastIndexOf(FS)))
                }

                client.createFile(remoteFile)
                handle = client.openFileWAppend(remoteFile)
                byte[] bytesArray = new byte[(int) actualFile.length()]
                FileInputStream fileInputStream  = new FileInputStream(actualFile)
                fileInputStream.read(bytesArray)
                fileInputStream.close()
                client.write(handle, 0, bytesArray, 0, bytesArray.length)
                finaliseConnection()
            }
        } catch (Exception e) {
            throw new Exception(e)
        }
    }

    void getFile(String remotefile=null, String localdir=null, boolean recursive=false)  {
        this.remoteFile=remotefile?remotefile:remoteFile
        String fileName
        if (remoteFile) {
            fileName=(new File(remoteFile)).getName()
        }
        this.localDir=localdir?:localDir
        if (localDir && fileName) {
            this.localFile = localDir + File.separator + fileName
        }
        if (localFile && localDir && fileName) {
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
            if (!recursive) {
                finaliseConnection()
            } else {
                closeHandle()
            }
        }
    }
    void getFiles(List<String> files, String localfolder=null) {
        this.localDir=localfolder?:localDir
        files?.each {file->
            if (file) {
                getFile(file, localDir,true)
            }
        }
        finaliseConnection()
    }

    boolean isNotHiddenFile(String strFileName) {
        return !isHiddenFile(strFileName);
    }

    boolean isHiddenFile(String pstrFolderName) {
        boolean flgR
        if (pstrFolderName.endsWith("..") == true || pstrFolderName.endsWith(".") == true) {
            flgR = true
        }
        return flgR
    }

    List<String> listFiles(String pathname, boolean recurseSubFolder) {
        try {
            return getFileNames(pathname, recurseSubFolder)
        } catch (Exception e) {
            throw new Exception("getfilenames() listFiles", e)
        }
    }

    /**
     * return a listing of the contents of a directory in short format on
     * the remote machine
     *
     * @return a listing of the contents of a directory on the remote machine
     *
     * @exception Exception
     * @see #nList( String )
     * @see #dir()
     * @see #dir( String )
     */
    List<String> getLs() throws Exception {
        return getFileNames()
    }
    List<String> getListFiles() throws Exception {
        return getFileNames()
    }
    List<String> listFiles() throws Exception {
        return getFileNames()
    }

    /**
     * return a listing of the contents of a directory in short format on
     * the remote machine (without subdirectory)
     *
     * @return a listing of the contents of a directory on the remote machine
     *
     * @exception Exception
     * @see #nList( String )
     * @see #dir()
     * @see #dir( String )
     */
    List<String> getFileNames() throws Exception {
        return getFileNames("", false);
    }

    List<String> getFileNames(boolean recurseSubFolders) throws Exception {
        return getFileNames("", recurseSubFolders)
    }

    /**
     * return a listing of the contents of a directory in short format on
     * the remote machine
     *
     * @return a listing of the contents of a directory on the remote machine
     *
     * @exception Exception
     * @see #nList( String )
     * @see #dir()
     * @see #dir( String )
     */

    List<String> listFiles(final boolean recursive) throws Exception {
        return getFileNames(recursive)
    }

    List<String> listFiles(String pathname) {
        return getFileNames(pathname)
    }

    List<String> getFileNames(String pathname) {
        List<String> vecT
        try {
            vecT = getFileNames(pathname, false)
        } catch (Exception e) {
            throw new Exception("getFileNames()", e)
        }
        return vecT
    }

    List<String> getFileNames(String pstrPathName, boolean recurseSubFolders) throws Exception {
        String strCurrentDirectory
        List<String> vecDirectoryListing=[]
        if (!vecDirectoryListing) {
            vecDirectoryListing = []
            String[] fileList = null
            String lstrPathName = pstrPathName?.trim()
            if (lstrPathName.length() <= 0) {
                lstrPathName = ".";
            }
            if (1 == 1) {
                try {
                    fileList = listNames(lstrPathName);
                }
                catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }

            if (!fileList) {
                return vecDirectoryListing;
            }

            for (String strCurrentFile : fileList) {
                if (isNotHiddenFile(strCurrentFile)) {
                    if (client.lstat(strCurrentFile).isDirectory() == false) {
                        if (lstrPathName.startsWith(FS) == false) {
                            if (strCurrentFile && strCurrentDirectory && strCurrentFile.startsWith(strCurrentDirectory) == false) {
                                strCurrentFile = addFileSeparator(strCurrentDirectory) + strCurrentFile
                            }
                        }
                        vecDirectoryListing.add(strCurrentFile)
                    }
                    else {
                        if (recurseSubFolders) {
                            log.debug("start scan for subdirectory "+ strCurrentFile)
                            Vector<String> vecNames = getFileNames(strCurrentFile, recurseSubFolders)
                            if (vecNames != null) {
                                vecDirectoryListing.addAll(vecNames)
                            }
                        }
                    }
                }
            }
        }
        return vecDirectoryListing;
    }

    int DoCD(String strFolderName) {
        int x = 0
        try {
            x = cd(strFolderName)
        } catch (IOException e) {
        }
        return x
    }
    private String trimResponseCode(final String response) throws Exception {
        if (response.length() < 5)
            return response;
        return response.substring(4).trim()
    }
    List<String> getDir() {
        return dir()
    }
    List<String> dir() {
        try {
            return dir("")
        } catch (Exception e) {
            throw new RuntimeException("dir", e);
        }
    }

    List<String>  dir(String pathname) {
        return getFileNames(pathname)
    }
    List<String> dir(String pathname, int flag) {
        List<String> fileList = []
        String[] listFiles = null;
        try {
            listFiles = this.listNames(pathname);
        }
        catch (IOException e) {
            throw new Exception("listfiles() dir")
        }
        if (listFiles != null) {
            for (String listFile : listFiles) {
                if (flag > 0 && isDirectory(listFile)) {
                    fileList.addAll(this.dir(pathname + FS + listFile, flag >= 1024 ? flag : flag + 1024));
                }
                else {
                    if (flag >= 1024) {
                        fileList.add(pathname + FS + listFile.toString());
                    }
                    else {
                        fileList.add(listFile.toString());
                    }
                }
            }
        }
        return fileList;
    }

    // private int DoCD
    int cd(String directory) throws IOException {
        changeWorkingDirectory(directory);
        return 1
    }

    boolean changeWorkingDirectory(String pathname) throws IOException {
        pathname = resolvePathname(pathname);
        // cut trailing "/" if it's not the only character
        if (pathname.length() > 1 && pathname.endsWith(FS))
            pathname = pathname.substring(0, pathname.length() - 1);
        if (!fileExists(pathname)) {
            reply = "\"" + pathname + "\" doesn't exist."
            return false
        }
        if (!isDirectory(pathname)) {
            reply = "\"" + pathname + "\" is not a directory."
            return false
        }
        if (pathname.startsWith(FS) || currentDirectory.length() == 0) {
            currentDirectory = pathname
            reply = "cd OK"
            return true
        }
        currentDirectory = pathname
        reply = "cd OK"
        return true
    }

    private String resolvePathname(final String pathname) {
        String strR = pathname;
        if (!pathname.startsWith("./") && !pathname.startsWith("/") && currentDirectory.length() > 0) {
            String slash = ""
            if (!currentDirectory.endsWith("/"))
                slash = "/"
            strR = currentDirectory + slash + pathname
        }
        while (pathname.contains("\\")) {
            strR = pathname.replace('\\', '/')
        }
        if (strR.endsWith("/")) {
            strR = strR.substring(0, strR.length() - 1)
        }
        return strR;
    }

    boolean isDirectory(final String filename) {
        try {
            return client.stat(filename).isDirectory()
        }
        catch (Exception e) {
        }
        return false
    }

    List<String> listNames(String pathname) throws IOException {
        pathname = resolvePathname(pathname)
        try {
            if (pathname.length() == 0) {
                pathname = "."
            }

            //if (!fileExists(pathname)) {
            //	return null
            //}

            if (!isDirectory(pathname)) {
                File remoteFile = new File(pathname)
                reply = "ls OK";
                return  [remoteFile.getName()]
            }
            Vector<SFTPv3DirectoryEntry> files = client.ls(pathname)
            List<String> rvFiles = []

            for (int i = 0; i < files.size(); i++) {
                SFTPv3DirectoryEntry entry = files.get(i)
                rvFiles << addFileSeparator(pathname) + entry.filename
            }
            reply = "ls OK"
            return rvFiles
        } catch (Exception e) {
            reply = e.toString()
            return null
        }
    }
    SFTPv3FileHandle openFileWR(final String pstrFilename) throws Exception {
        SFTPv3FileHandle sftpFileHandle = null;
        String sourceLocation = resolvePathname(pstrFilename)
        try {
            //	sftpFileHandle = objFTPClient.createFile(pstrFilename);
            sftpFileHandle = client.createFileTruncate(sourceLocation);

        }
        catch (Exception e) {
            throw new Exception("opening file [" + sourceLocation + "]", e.getMessage())
        } finally {
        }
        return sftpFileHandle
    }

    SFTPv3FileHandle openFileRO(final String pstrFilename) throws Exception {
        String sourceLocation = resolvePathname(pstrFilename)
        SFTPv3FileHandle sftpFileHandle = null
        try {
            sftpFileHandle = client.openFileRO(sourceLocation)
        }
        catch (Exception e) {
            throw new Exception("opening file [" + sourceLocation + "]", e.getMessage());
        } finally {
        }
        return sftpFileHandle
    }

    void put(String localFile, String remoteFile) {

        try {
            long lngBytesWritten = this.putFile(localFile, remoteFile);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    long putFile(String localfile=null, String remotedir=null, boolean recursive=false) throws Exception {

        int count = 0
        initialiseRemoteFile(localfile,remotedir,true)
        if (remoteDir && remoteFile && localFile) {
            remoteFile = resolvePathname(remoteFile);
            createRemoteDirs(remoteDir)
            client.createFile(remoteFile)
            handle = client.openFileRW(remoteFile)
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(localFile))
            byte[] buf = new byte[BUFSIZE]

            int bufsiz = 0
            while ((bufsiz = is.read(buf)) != -1) {
                //here the writing is made on the remote file
                client.write(handle, (long) count, buf, 0, bufsiz)
                count += bufsiz
            }
            is.close()
            if (!recursive) {
                finaliseConnection()
            } else {
                closeHandle()
            }
        }
        return count
    }

    long putFile(final String localFile, final OutputStream out) {
        FileInputStream input
        long lngTotalBytesWritten = 0
        try {
            byte[] buffer = new byte[4096]
            input = new FileInputStream(new File(localFile))
            // TODO get progress info
            int bytesWritten
            synchronized (this) {
                while ((bytesWritten = input.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesWritten)
                    lngTotalBytesWritten += bytesWritten
                }
            }
            input.close()
            out.close()
            return lngTotalBytesWritten;
        }
        catch (Exception e) {
            throw new Exception("putFile()",e);
        }
        finally {
            input.close()
            out.close()
        }
        return lngTotalBytesWritten;
    }

    void putFiles(List<String> localFilePaths, String remotefolder=null) {
        this.remoteDir=remotefolder?:remoteDir
        if (this.remoteDir) {
            localFilePaths?.each { file->
                if (file) {
                    putFile(file,remoteDir,true)
                }
            }
            finaliseConnection()
        }
    }
    public String addFileSeparator(final String str) {
        return str.endsWith("/") || str.endsWith("\\") ? str : str + "/";
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
            Integer ret = session?.getExitStatus()
            if (!ret || ret != 0) {
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

    void del(String pathName)  {
        deleteRemoteFile(pathName)
    }
    void deleteRemoteFile(String pathName) throws IOException {
        if (fileExists(pathName)) {
            client.rm(pathName)
            finaliseConnection()
        }
    }

    boolean fileExists(String filename=null) throws Exception {
        boolean state=false
        this.localFile = filename?:localFile
        if (localFile) {
            try {
                SFTPv3FileAttributes attributes = client.stat()
                if (attributes != null) {
                    state=attributes.isRegularFile() || attributes.isDirectory()
                } else {
                    state=false;
                }
            } catch (Exception e) {
                state=false;
            }
        }
        finaliseConnection()
        return state
    }

    Date getModificationDateTime(String strFileName) {
        Date dt
        try {
            handle = client.openFileRO(strFileName)
            dt=new Date(client.fstat(handle).atime*1000L)
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return dt
    }

    long setModificationDateTime(String file, long pdteDateTime) {
        long lngR = 0
        try {
            handle = client.openFileRW(file)
            SFTPv3FileAttributes objFA = client.fstat(handle)
            //objFA.mtime = new Integer((int) pdteDateTime /1000L)
            objFA.atime = new Integer((int) pdteDateTime/1000L)

            client.fsetstat(handle,objFA)
            client.closeFile(handle)
            lngR = pdteDateTime
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        finaliseConnection()
        return lngR
    }

    void setModificationDateTime(String file, Date date) {
        try {
            handle = client.openFileRW(file)
            SFTPv3FileAttributes objFA = client.fstat(handle)
            //objFA.mtime = (date.getTime()/1000L).intValue()
            objFA.atime = (date.getTime()/1000L).intValue()
            client.fsetstat(handle,objFA)
            client.closeFile(handle)
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        finaliseConnection()
    }

    Integer getFilePermission(String strFileName) {
        Integer permission
        try {
            handle = client.openFileRO(strFileName)
            //permission=client.fstat(handle).permissions
            String op = client.fstat(handle).octalPermissions
            permission=op.substring(4,op.length()) as int
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return permission
    }

    String getPermission(String strFileName=null) {
        if (!strFileName)  strFileName=remoteFile
        String permission
        try {
            handle = client.openFileRO(strFileName)
            String op = client.fstat(handle).octalPermissions
            permission=splitPermission(op.substring(4,op.length()))
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return permission
    }

    String getOctalPermission(String strFileName=null) {
        if (!strFileName)  strFileName=remoteFile
        String permission
        try {
            handle = client.openFileRO(strFileName)
            permission= client.fstat(handle).octalPermissions
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return permission
    }
    int getDecimalPermission(String strFileName=null) {
        if (!strFileName)  strFileName=remoteFile
        int permission
        try {
            handle = client.openFileRO(strFileName)
            permission= client.fstat(handle).permissions
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return permission
    }

    String getBinaryPermission(String strFileName=null) {
        if (!strFileName)  strFileName=remoteFile
        String permission
        try {
            handle = client.openFileRO(strFileName)
            permission = Integer.toBinaryString(client.fstat(handle).permissions)
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return permission
    }

    String getFileUserId(String strFileName) {
        String uid
        try {
            handle = client.openFileRO(strFileName)
            uid=client.fstat(handle).uid
            //permission=octalToBinary(client.stat(strFileName).permissions)
        } catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
        return uid
    }

    /**
     * Your end user needs to have sufficient privileges
     * The user does not have sufficient permissions to perform the operation.
     * @param strFileName
     * @param uid
     */
    void setFileUserId(String strFileName, int uid) {
        try {
            handle = client.openFileRW(strFileName)
            SFTPv3FileAttributes objFA = client.fstat(handle)
            objFA.uid=uid
            client.fsetstat(handle,objFA)
            client.closeFile(handle)
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        finaliseConnection()
    }


    String splitPermission(String line) {
        String[] nums = line.split("");
        StringBuilder sb = new StringBuilder()
        nums.findAll{it}?.each { s->
            if (sb.length() > 0) sb.append(" ")
            int num = Integer.parseInt(s)
            sb.append((num & 4) == 0 ? '-' : 'r')
            sb.append((num & 2) == 0 ? '-' : 'w')
            sb.append((num & 1) == 0 ? '-' : 'x')
        }
        log.debug("Permission ${line} = ${sb.toString()}")
        return sb.toString()
    }

    void setFilePermission(String strFileName, int newPermission) {
        try {
            handle = client.openFileRW(strFileName)
            SFTPv3FileAttributes objFA = client.fstat(handle)
            //FileMode fm = new FileMode(newPermission.toString())
            int decimal=Integer.parseInt("0100${newPermission.toString()}",8)
            objFA.permissions=decimal
            client.fsetstat(handle,objFA)
            client.closeFile(handle)
        }
        catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
    }


    void setFilePermission(String strFileName, String newPermission) {
        try {
            handle = client.openFileRW(strFileName)
            SFTPv3FileAttributes objFA = client.fstat(handle)
            //FileMode fm = new FileMode(newPermission.toString())
            int decimal=Integer.parseInt(newPermission.startsWith("0100") ? newPermission : '0100'+newPermission,8)
            objFA.permissions=decimal
            client.fsetstat(handle,objFA)
            client.closeFile(handle)
        }
        catch (IOException e) {
            // e.printStackTrace();
        }
        finaliseConnection()
    }

    long fileSize(String remotefile=null) {
        return remoteFileSize(remotefile)
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

    Connection openConnection(String host=null, int port=0) {
        if (!host) {
            host=getConfig("HOST")?.toString()?:"127.0.0.1"
        }
        if (!port) {
            port=(getConfig("PORT")?getConfig("PORT") as int:22)
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

    String getDisconnect() {
        disconnect()
        return ''
    }
    void disconnect() {
        finaliseConnection(true)
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

    /**
     * 0.11 customised to lookup internal configVariable meaning default remotessh config key
     * can be overridden demonstrated in release-0.10.md
     * @param configProperty
     * @return
     */
    def getConfig(String configProperty) {
        if (cfg.size()==0) {
            //Get this once to save repeating getting holders
            cfg =  Holders.grailsApplication.config."${this.configVariable}"
        }
        return (cfg."${configProperty}" ?: null)
    }
}