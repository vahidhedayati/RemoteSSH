When executing http://localhost:8080/test-rssh/newWays 
This calls index method in test-rssh project:

```
def sshUtilService
	def	sshExecutor
	def scheduledSshExecutor
	/**
	 * Tests latest currently 0.14
	 * @return
	 */
	def index() {
		boolean singleInstance=false
		SSHUtil sshUtil = sshUtilService.initialise('mx1','password','localhost',22,singleInstance)
		Map output=[:]
		
		//Either now like this
		output.ranCommand = sshUtilService.execute(sshUtil,'mkdir /tmp/backup-test-new')
		
		output.ranCommand = sshUtilService.execute(sshUtil,'mkdir /tmp/remote-got')
		output.ranCommand = sshUtilService.execute(sshUtil,'mkdir /tmp/remote-got2')
		
		output.ranCommand2 = sshUtilService.execute(sshUtil,'echo "hi there" > /tmp/Kispálés.txt')
		
		//returned files ie will scp them down
		output.ranCommand2 = sshUtilService.execute(sshUtil,'echo "hi there return" > /tmp/backup-test-new/remote-file-example.txt')
		output.ranCommand2 = sshUtilService.execute(sshUtil,'echo "hi there return" > /tmp/backup-test-new/remote-file-example2.txt')
		
		//OR direct now you have sshUtil class:
		output.ranCommand3 = """making dir
			/tmp/backup-sshUtil """+sshUtil.execute('mkdir /tmp/backup-sshUtil')
			
		output.ranCommand4 = """Executing echo hi there from sshUtil to file:
			/tmp/test2.txt """+sshUtil.execute('echo "hi there from sshUtil" > /tmp/test2.txt')
			
		output.ranCommand5 = """Executing echo hi there from sshUtil to file:
			/tmp/Kispálés2.txt """+sshUtil.execute('echo "hi there from sshUtil" > /tmp/Kispálés2.txt')
				
		output.ranCommand6 = """Executing echo hi there from sshUtil test3 to file:
			/tmp/test3.txt """+sshUtil.execute('echo "hi there from sshUtil test3" > /tmp/test3.txt')
			
		output.ranCommand7 = """Executing echo hi there from sshUtil test3 to file:
			/tmp/Kispálés3.txt """+sshUtil.execute('echo "hi there from sshUtil test3" > /tmp/Kispálés3.txt')
		
		output.ranCommand10 = """Executing echo hi there from sshUtil test4 to file:
			/tmp/test4.txt """+sshUtil.execute('echo "hi there from sshUtil test4" > /tmp/test4.txt')
		output.ranCommand11 = """Executing echo hi there from sshUtil test5 to file:
			/tmp/test5.txt """+sshUtil.execute('echo "hi there from sshUtil test5" > /tmp/test5.txt')
		
		output.ranCommand12 ="""Executing echo hi there from sshUtil test4 to file:
			/tmp/test6.txt """+sshUtil.execute('echo "hi there from sshUtil test4" > /tmp/test6.txt')
			
		output.ranCommand13 = """Executing echo hi there from sshUtil test5 to file:
			/tmp/test7.txt """+sshUtil.execute('echo "hi there from sshUtil test5" > /tmp/test7.txt')
			
		output.ranCommand14 = """Executing echo hi to hidden file:
			/tmp/.hiddenTest.txt """+sshUtil.execute('echo "hi" > /tmp/.hiddenTest.txt')
			
		output.readFile1 = """Reading remote file content of
			/tmp/test2.txt: (using readRemoteFile) """+sshUtilService.readRemoteFile(sshUtil,'/tmp/test2.txt')
			
		output.readFile2 = """Reading remote file content of
			/tmp/test2.txt: (using readRemoteFile) """+sshUtil.readRemoteFile('/tmp/test2.txt')
		
		output.readFile3 = """Reading remote file content of
			/tmp/Kispálés.txt: (using readFile) """+sshUtilService.readFile(sshUtil,'/tmp/Kispálés.txt')
			
		output.readFile4 ="""Reading remote file content of
			/tmp/Kispálés2.txt: (using readFile) """+ sshUtil.readFile('/tmp/Kispálés2.txt')
		
		
		//These are voids no output
		output.writeFile1 ="""Writing file /tmp/Kispálés.txt to
			/tmp/backup-test-new/"""+sshUtilService.writeFile(sshUtil,'/tmp/Kispálés.txt','/tmp/backup-test-new/')
			
		//sshUtil.mkdir('/tmp/backup-test-new/alternative')
		//output.mkdir11 ="""Making recursive dir remote as /tmp/backup-test-new/alternative """+sshUtil.mkDirs('/tmp/backup-test-new/alternative')
		
		try {
			output.mkdir12 ="""Making recursive dir remote as
				/tmp/backup-test-new/alternative/abc """+sshUtil.mkDirs('/tmp/backup-test-new/alternative/abc')
				
		} catch (Exception e) {
			log.error "Something went wrong with ${e.message} ",e
		}
			
		output.writeFile22 ="""Writing local file: /tmp/Kispálés.txt to remote dir 
			/tmp/backup-test-new/alternative/"""+sshUtil.writeFile('/tmp/Kispálés.txt','/tmp/backup-test-new/alternative/')
			
		output.writeFile2 ="""Writing local file: /tmp/Kispálés2.txt to remote dir 
			/tmp/backup-test-new/"""+sshUtil.writeFile('/tmp/Kispálés2.txt','/tmp/backup-test-new/')
			
		
		output.writeFile1 ="""Writing local file: /tmp/Kispálés.txt as 
			/tmp/backup-test-new/ahha1.txt on
			remote system"""+sshUtilService.writeFileWithName(sshUtil,'/tmp/Kispálés.txt','/tmp/backup-test-new/ahha1.txt')
			
		output.writeFile2 ="""Writing local file: /tmp/Kispálés.txt as 
			/tmp/backup-test-new/ahha2.txt on
			remote system"""+sshUtil.writeFileWithName('/tmp/Kispálés.txt','/tmp/backup-test-new/ahha2.txt')
		
		output.writeFile4 ="""putting file: '/tmp/Kispálés3.txt'
			to /tmp/backup-test-new"""+sshUtilService.putFile(sshUtil,'/tmp/Kispálés3.txt','/tmp/backup-test-new/')
			
		output.writeFile5 ="""putting file: '/tmp/test3.txt'
			to /tmp/backup-test-new"""+sshUtil.putFile('/tmp/test3.txt','/tmp/backup-test-new/')
			
		output.writeFile6 ="""putting file: '/tmp/Kispálés3.txt'
			to /tmp/backup-test-new"""+sshUtilService.putFile(sshUtil,'/tmp/Kispálés3.txt','/tmp/backup-test-new/')

		//uploads  a bunch of files
		output.writeFile7 ="""putting files: ['/tmp/test4.txt','/tmp/test5.txt']
			to /tmp/backup-test-new"""+sshUtilService.putFiles(sshUtil,['/tmp/test4.txt','/tmp/test5.txt'],'/tmp/backup-test-new')
			
		output.writeFile7 ="""putting files: ['/tmp/test6.txt','/tmp/test7.txt']
			to /tmp/backup-test-new"""+sshUtil.putFiles(['/tmp/test6.txt','/tmp/test7.txt'],'/tmp/backup-test-new')
		
		//Pretending to get remoteFile and storing locally
		output.getRemoteFile1 ="""Getting REMOTE: 
				/tmp/backup-test-new/remote-file-example.txt  and copying to: /tmp/
		"""+sshUtilService.getFile(sshUtil,'/tmp/backup-test-new/remote-file-example.txt','/tmp/')
		
		output.getRemoteFile2 =sshUtil.getFile('/tmp/backup-test-new/remote-file-example2.txt','/tmp/')
		
		//gets a bunch of files
		output.getRemoteFile3 ="""Getting REMOTE FILES: 
			['/tmp/backup-test-new/remote-file-example2.txt',
			'/tmp/backup-test-new/remote-file-example.txt']  and copying to: /tmp/remote-got
		"""+sshUtilService.getFiles(sshUtil,['/tmp/backup-test-new/remote-file-example2.txt',
			'/tmp/backup-test-new/remote-file-example.txt'],'/tmp/remote-got')
	
		output.getRemoteFile4 ="""Getting REMOTE FILES: 
			['/tmp/backup-test-new/remote-file-example2.txt',
			'/tmp/backup-test-new/remote-file-example.txt'] and copying to: /tmp/remote-got2
		"""+sshUtil.getFiles(['/tmp/backup-test-new/remote-file-example2.txt',
			'/tmp/backup-test-new/remote-file-example.txt'],'/tmp/remote-got2')
	
		
		//Boolean check if a file exists
		output.fileExists1="""Does file Exist: 
			/tmp/backup-test-new/remote-file-example.txt
			?"""+ sshUtilService.fileExists(sshUtil,'/tmp/backup-test-new/remote-file-example.txt')
			
		output.fileExists2= """Does file Exist: 
			/tmp/backup-test-new/remote-file-example22.txt
			?"""+sshUtilService.fileExists(sshUtil,'/tmp/backup-test-new/remote-file-example22.txt')
			
		output.fileExists3= """Does file Exist: 
			/tmp/backup-test-new/remote-file-example.txt
			?"""+sshUtil.fileExists('/tmp/backup-test-new/remote-file-example.txt')
		
		
		output.remoteFileSize1='remote file size: /tmp/test5.txt '+sshUtilService.remoteFileSize(sshUtil,'/tmp/test5.txt')
		output.remoteFileSize2='remote file size: /tmp/test5.txt '+sshUtil.remoteFileSize('/tmp/test5.txt')
		
		
		//Delete remote file
		output.deleteFile1= """remove remote file: 
			/tmp/backup-test-new/remote-file-example.txt """+sshUtilService.deleteRemoteFile(sshUtil,'/tmp/backup-test-new/remote-file-example.txt')
			
		output.deleteFile2= """remove remote file: 
			/tmp/backup-test-new/remote-file-example2.txt """+sshUtil.deleteRemoteFile('/tmp/backup-test-new/remote-file-example2.txt')
		output.deleteFile2= """remove remote file: 
			/tmp/test5.txt """+sshUtil.del('/tmp/test5.txt')

		output.createDir1="""createRemoteDirs: 
			/tmp/remote/1/2/3 """+sshUtilService.createRemoteDirs(sshUtil,'/tmp/remote/1/2/3')
		output.createDir2="""createRemoteDirs: 
			/tmp/remote/3/4/5 """+sshUtil.createRemoteDirs('/tmp/remote/3/4/5')
		//alternative 0.13 same as above
		output.createDir2="""createRemoteDirs: 
			/tmp/remote/3/4/6 """+sshUtil.mkDirs('/tmp/remote/3/4/6')
		
		/**
		 * Some additional stuff in 0.13+ please note the sshUtilService method by passing sshUtil could also be used in
		 * all examples cases below
		 */
		//hidden/notHidden file how to:
		//sshUtil.isHiddenFile('path/file')
		//sshUtil.isNotHiddenFile('path/file')
		
		//output.hidden1="/tmp/.hiddenTest.txt is hidden  = "+sshUtil.isHiddenFile('/tmp/.hiddenTest.txt')
		//output.hidden2="/tmp/.hiddenTest.txt is not hidden  = "+sshUtil.isNotHiddenFile('/tmp/.hiddenTest.txt')
		//output.hidden3="/tmp/test4.txt is hidden  = "+sshUtil.isHiddenFile('/tmp/test4.txt')
		//output.hidden4="/tmp/test4.txt  isNotHiddenFile  = "+sshUtil.isNotHiddenFile('/tmp/test4.txt')
		
		output.isDirectory='/tmp/backup-test-new is directory: '+sshUtil.isDirectory('/tmp/backup-test-new')
		output.isDirectory1='/tmp/test4.txt is directory: '+sshUtil.isDirectory('/tmp/test4.txt')
		
		output.changeDir = 'Changing dir to: /tmp/backup-test-new '+sshUtil.cd('/tmp/backup-test-new')
		
		//Despite work this loads up home folder of user
		//utput.dirList = 'dir  /tmp/backup-test-new'+sshUtil.dir
		//output.dirList4 = 'ls  /tmp/backup-test-new'+sshUtil.ls
		
		//
		output.dirList1 = 'dir(folder)  /tmp/'+sshUtil.listFiles('/tmp/backup-test-new',true)
		output.dirList2 = 'dir(folder)  /tmp/'+sshUtil.dir('/tmp/backup-test-new')
		
		
		output.listNames='listNames(folder) /tmp/backup-test-new'+sshUtil.listNames('/tmp/backup-test-new')
		output.listNames1='getFileNames(folder) /tmp/backup-test-new'+sshUtil.getFileNames('/tmp/backup-test-new')
		output.listNames2='listFiles(folder) /tmp/remote non recursive'+sshUtil.listFiles('/tmp/remote')
		output.listNames3='listFiles(folder) /tmp/remote recursive'+ sshUtil.listFiles('/tmp/remote',true)
		
		println "calling rmdir"
		
	
		try {
			
			output.removeDir1='rmdir(folder) /tmp/remote/1/2/3 non recursive'+sshUtil.rmdir('/tmp/remote/1/2/3',false)
			
			
			//by default now made recursive or
			output.removeDir2='rmdir(folder) /tmp/remote/1/2/3  recursive'+sshUtil.rmdir('/tmp/remote/3/4/5')
		
			//like this
			//output.removeDir2=sshUtil.rmdir('/tmp/remote/3/4/5',true)
		} catch (Exception e) {
			log.error "Something went wrong with ${e.message} ",e
		}
		
		try {
			output.mkdir='mkdir(folder) /tmp/remote/6  non recursive'+sshUtil.mkdir('/tmp/remote/6')
		} catch (Exception e) {
			log.error "Something went wrong with ${e.message} ",e
		}
		output.listNames4='listFiles(folder) /tmp/backup-test-new'+ sshUtil.listFiles('/tmp/backup-test-new',true)
		
		
		
		/**
		 * How to use your own custom configuration file mapping - we are replacing the default
		 * remotessh grails config with mySshConfigVar part of 0.11 release
		 */
		
		
		SSHUtil sshUtil1 = new SSHUtil()
		sshUtil1.configVariable='mySshConfigVar'
		sshUtil1.initialise
		// sshUtil.initialise('someHost',22)
		sshUtil1.localFile='/tmp/test2.txt'
		boolean doesItExist = sshUtil1.fileExists()
		println "file exists = ${doesItExist}"
		sshUtil1.deleteRemoteFile('/tmp/test2.txt')
		doesItExist = sshUtil1.fileExists()
		println "file exists = ${doesItExist}"
		sshUtil1.disconnect
		
		
		
		SSHUtil sshUtil2 = new SSHUtil()
		sshUtil2.configVariable='mySshConfigVar2'
		sshUtil2.initialise
		// sshUtil.initialise('someHost',22)
		sshUtil2.localFile='/tmp/test3.txt'
		boolean doesItExist1 = sshUtil2.fileExists()
		println "/tmp/test3.txt using  mySshConfigVar2 file exists = ${doesItExist1}"
		sshUtil2.disconnect
		
		

		output.fileDateTime = 'Getting file date time for /tmp/test7.txt: '+\
			sshUtil.getModificationDateTime('/tmp/test7.txt')
		
		output.setfileDateTime = 'Setting new Date ->'+(new Date()+4)
		sshUtil.setModificationDateTime('/tmp/test7.txt', new Date()+4)
		
		output.fileDateTime1 = 'Getting date again 2 : '+sshUtil.getModificationDateTime('/tmp/test7.txt')
		
		//output.setfileDateTime2 = 'Setting new Date using long value:'+\
		//sshUtil.setModificationDateTime('/tmp/test7.txt', (new Date()+44).getTime())
		
		//output.fileDateTime3 = 'Getting date again 3 : '+sshUtil.getModificationDateTime('/tmp/test7.txt')
		
		
		output.filePermission = 'Getting file Permission /tmp/test7.txt: '+\
			sshUtil.getFilePermission('/tmp/test7.txt')
			
		output.filePermissionString = 'Getting chmod file Permission /tmp/test7.txt: '+\
			sshUtil.getPermission('/tmp/test7.txt')
			
		output.fileUserId = 'Getting file Userid: '+\
			sshUtil.getFileUserId('/tmp/test7.txt')
			
			output.setFileUserId = 'setFileUserId file Userid to root: '+\
			sshUtil.setFileUserId('/tmp/test7.txt',33)
			
			output.fileUserId1 = 'Getting file Userid again: '+\
			sshUtil.getFileUserId('/tmp/test7.txt')
			
			
		output.setfilePermission = 'Setting file Permission /tmp/test7.txt to 755: '
		sshUtil.setFilePermission('/tmp/test7.txt',755)
			
		output.filePermissionString4 = 'Getting chmod file Permission /tmp/test7.txt: '+\
			sshUtil.getPermission('/tmp/test7.txt')
			
		output.filePermission1 = 'Getting Human readable file Permission: '+\
			sshUtil.getFilePermission('/tmp/test7.txt')
		
			
		output.filePermission2 = 'Getting Octal file Permission : '+\
			sshUtil.getOctalPermission('/tmp/test7.txt')
			
		output.filePermission3 = 'Getting file Decimal Permission: '+\
				sshUtil.getDecimalPermission('/tmp/test7.txt')
				
		output.filePermission5 = 'Getting file Binary Permission: '+\
				sshUtil.getBinaryPermission('/tmp/test7.txt')
			
		output.setfilePermission1 = 'Setting file Permission /tmp/test7.txt to 644: '
		sshUtil.setFilePermission('/tmp/test7.txt','0644')
				
		output.filePermissionString6 = 'Getting chmod file Permission /tmp/test7.txt: '+\
				sshUtil.getPermission('/tmp/test7.txt')
			
		
				
				///Advanced topics 
						
		Priority priority = Priority.LOW
				
		SshRunnable currentTask = new SshRunnable({
			sshUtil = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			sshUtil.localFile='/tmp/test3.txt'
			sshUtil.fileExists()
			sshUtil.createRemoteDirs('/tmp/v/a/h/i/d/99')
			sshUtil.disconnect
			})
		RunnableFuture task = sshExecutor.execute(currentTask, priority.value)
		def t =  task?.get()
		
				
		/**
		 * Below are some closure methods used in conjunction with Threaded Executable
		 *  This is useful for background jobs that does not to be viewed as such
		 *  since the tasks happen in external threads 
		 * 		
		 */
				
		/**
		 * Threaded Executor with closure to carry out background tasks - to limit amount of concurrent ssh sessions
		 * 
		 * The initial methods call entire SSHUTil methods within closure and are totall thread safe
		 * as present further down when we get file permissions after 2 disconnections within the closures
		 * 		
		 */				
		Closure closure1 = {
			//We have not declared sshUtil since it is used on this page above - 
			// if no declaration above then in each closure it be like:
			// SshUtil sshUtil = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			SSHUtil sshUtil5 = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			sshUtil5.localFile='/tmp/test3.txt'
			sshUtil5.fileExists()
			sshUtil5.createRemoteDirs('/tmp/v/a/h/i/d/1')
			sshUtil5.disconnect
		}
		//Method 1
		output.threadedExecutorResults1  = sshUtilService.threadedExecutor(closure1)
	
		
		Closure closure2 = {
			sshUtil = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			sshUtil.localFile='/tmp/test3.txt'
			sshUtil.fileExists()
			sshUtil.createRemoteDirs('/tmp/v/a/h/i/d/2')
			sshUtil.disconnect
		} 
		// Method 2
		output.threadedExecutorResults2  = sshUtilService.threadedExecutor(closure2, Priority.HIGHEST)

		/**
		 * Now we are calling the existing SSHUtil on this page to get the file permissions all over again
		 * to demonstrate that this thread of sshUtil has nothing to do with above running tasks
		 */
		output.filePermissionString7 = 'Getting chmod file Permission  after closure with disconnections: /tmp/test7.txt: '+\
		sshUtil.getPermission('/tmp/test7.txt')
	
		
		
		
		//Advanced Threading topics
		
		println "going to schedule 1"
		SshRunnable currentTaskAdvanced = new SshRunnable({
			sshUtil = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			sshUtil.localFile='/tmp/test3.txt'
			sshUtil.fileExists()
			sshUtil.createRemoteDirs('/tmp/v/a/h/i/d/96')
			sshUtil.disconnect
			})
		
		println "going to schedule 1 start"
		RunnableFuture advancedTask = scheduledSshExecutor.schedule(currentTaskAdvanced, 10 , TimeUnit.SECONDS )
		//advancedTask?.get()
		println "going to schedule 1 emnd"
		
		
		SshRunnable currentTaskAdvanced1 = new SshRunnable({
			sshUtil = new SSHUtil().initialise('mx1', 'password', 'localhost', 22, false)
			sshUtil.localFile='/tmp/test3.txt'
			sshUtil.fileExists()
			sshUtil.createRemoteDirs('/tmp/v/a/h/i/d/97')
			sshUtil.disconnect
			})
		println "going to schedule 2"
		RunnableFuture advancedTask1 = scheduledSshExecutor.schedule(currentTaskAdvanced1, 25 , TimeUnit.SECONDS )
		//advancedTask1?.get()
		println "going to schedule 2"
		
		
		
		
		
		//--- Below methods are not thread safe
		
		/**
		 * Below methods have been left but are not thread safe - the current SSHUtil is then passed to 
		 * threadExecutor which launches / reuses the 1 current connection to do other things
		 * it is kept since it is still using that 1 underlying connection created above
		 */
		
		
		/**
		 * This is using existing connection from here which gets passed to external thread
		 * and resets the connection as this connection so in effect connects here then sends to thread
		 * when thread gets it uses this connection this is unlikely to be thread safe
		 * Manually calling SSH Util the Threaded way of working -
		 * Using threads to spread out ssh requests
		 */
		
		Closure closure = {
			sshUtil.createRemoteDirs('/tmp/v1/a1')
		}
		output.threadedExecutorResults4 = sshUtilService.threadedExecutor(sshUtil, closure)
		
		SshRunnable currentTask1 = new SshRunnable(sshUtil,sshUtil.connection,{sshUtil.createRemoteDirs('/tmp/v1/a2')})
		RunnableFuture task1 = sshExecutor.execute(currentTask1, priority.value)
		output.threadedExecutorResults3 =  task1?.get()
		
		//We are going to try to cheat and capture connection before disconnection
		Connection existingConnectionBeforeDisconnection = sshUtil.connection
		
		//We are going to disconnect on this page
		sshUtil.disconnect
		
		//Now using same connection within thread  - this will not work since already disconnected
		/**
		 * This is using existing connection from here which gets passed to external thread
		 * and resets the connection as this connection so in effect connects here then sends to thread
		 * when thread gets it uses this connection this is unlikely to be thread safe
		 * Using sshUtilService to call threaded calls to ssh end points
		 *
		 */
		Closure closure4 = {
			sshUtil.createRemoteDirs('/tmp/v1/a3')
		}
		output.threadedExecutorResults5 = sshUtilService.threadedExecutor(sshUtil, closure4)
		
		//Again we try something similar but to reuse the existingConnection saved - again this will not work 
		/**
		 * This is calling or persisting the connection before disconnection above.
		 */
		Closure closure3 = {
			sshUtil.createRemoteDirs('/tmp/v1/a4')
		}
		output.threadedExecutorResults6 = sshUtilService.threadedExecutor(sshUtil,
			existingConnectionBeforeDisconnection, closure3)
		
		
		sshUtil.disconnect
		
		JSON json = output as JSON
		json.prettyPrint = true
		json.render response
	}
	
```


The following output is produced:

```
{
  "ranCommand": "",
  "ranCommand2": "",
  "ranCommand3": "making dir\n\t\t\t/tmp/backup-sshUtil ",
  "ranCommand4": "Executing echo hi there from sshUtil to file:\n\t\t\t/tmp/test2.txt ",
  "ranCommand5": "Executing echo hi there from sshUtil to file:\n\t\t\t/tmp/Kispálés2.txt ",
  "ranCommand6": "Executing echo hi there from sshUtil test3 to file:\n\t\t\t/tmp/test3.txt ",
  "ranCommand7": "Executing echo hi there from sshUtil test3 to file:\n\t\t\t/tmp/Kispálés3.txt ",
  "ranCommand10": "Executing echo hi there from sshUtil test4 to file:\n\t\t\t/tmp/test4.txt ",
  "ranCommand11": "Executing echo hi there from sshUtil test5 to file:\n\t\t\t/tmp/test5.txt ",
  "ranCommand12": "Executing echo hi there from sshUtil test4 to file:\n\t\t\t/tmp/test6.txt ",
  "ranCommand13": "Executing echo hi there from sshUtil test5 to file:\n\t\t\t/tmp/test7.txt ",
  "ranCommand14": "Executing echo hi to hidden file:\n\t\t\t/tmp/.hiddenTest.txt ",
  "readFile1": "Reading remote file content of\n\t\t\t/tmp/test2.txt: (using readRemoteFile) hi there from sshUtil\n",
  "readFile2": "Reading remote file content of\n\t\t\t/tmp/test2.txt: (using readRemoteFile) hi there from sshUtil\n",
  "readFile3": "Reading remote file content of\n\t\t\t/tmp/Kispálés.txt: (using readFile) hi there\n\u0000\u0000",
  "readFile4": "Reading remote file content of\n\t\t\t/tmp/Kispálés2.txt: (using readFile) om sshUtil\n",
  "writeFile1": "Writing local file: /tmp/Kispálés.txt as \n\t\t\t/tmp/backup-test-new/ahha1.txt on\n\t\t\tremote systemnull",
  "mkdir12": "Making recursive dir remote as\n\t\t\t\t/tmp/backup-test-new/alternative/abc null",
  "writeFile22": "Writing local file: /tmp/Kispálés.txt to remote dir \n\t\t\t/tmp/backup-test-new/alternative/null",
  "writeFile2": "Writing local file: /tmp/Kispálés.txt as \n\t\t\t/tmp/backup-test-new/ahha2.txt on\n\t\t\tremote systemnull",
  "writeFile4": "putting file: '/tmp/Kispálés3.txt'\n\t\t\tto /tmp/backup-test-newnull",
  "writeFile5": "putting file: '/tmp/test3.txt'\n\t\t\tto /tmp/backup-test-new28",
  "writeFile6": "putting file: '/tmp/Kispálés3.txt'\n\t\t\tto /tmp/backup-test-newnull",
  "writeFile7": "putting files: ['/tmp/test6.txt','/tmp/test7.txt']\n\t\t\tto /tmp/backup-test-newnull",
  "getRemoteFile1": "Getting REMOTE: \n\t\t\t\t/tmp/backup-test-new/remote-file-example.txt  and copying to: /tmp/\n\t\tnull",
  "getRemoteFile2": null,
  "getRemoteFile3": "Getting REMOTE FILES: \n\t\t\t['/tmp/backup-test-new/remote-file-example2.txt',\n\t\t\t'/tmp/backup-test-new/remote-file-example.txt']  and copying to: /tmp/remote-got\n\t\tnull",
  "getRemoteFile4": "Getting REMOTE FILES: \n\t\t\t['/tmp/backup-test-new/remote-file-example2.txt',\n\t\t\t'/tmp/backup-test-new/remote-file-example.txt'] and copying to: /tmp/remote-got2\n\t\tnull",
  "fileExists1": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt\n\t\t\t?false",
  "fileExists2": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example22.txt\n\t\t\t?false",
  "fileExists3": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt\n\t\t\t?false",
  "remoteFileSize1": "remote file size: /tmp/test5.txt 28",
  "remoteFileSize2": "remote file size: /tmp/test5.txt 28",
  "deleteFile1": "remove remote file: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt null",
  "deleteFile2": "remove remote file: \n\t\t\t/tmp/test5.txt null",
  "createDir1": "createRemoteDirs: \n\t\t\t/tmp/remote/1/2/3 null",
  "createDir2": "createRemoteDirs: \n\t\t\t/tmp/remote/3/4/6 null",
  "isDirectory": "/tmp/backup-test-new is directory: true",
  "isDirectory1": "/tmp/test4.txt is directory: false",
  "changeDir": "Changing dir to: /tmp/backup-test-new 1",
  "dirList1": "dir(folder)  /tmp/[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/alternative/Kispálés.txt, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]",
  "dirList2": "dir(folder)  /tmp/[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]",
  "listNames": "listNames(folder) /tmp/backup-test-new[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/.., /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/alternative, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/., /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]",
  "listNames1": "getFileNames(folder) /tmp/backup-test-new[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]",
  "listNames2": "listFiles(folder) /tmp/remote non recursive[]",
  "listNames3": "listFiles(folder) /tmp/remote recursive[]",
  "removeDir1": "rmdir(folder) /tmp/remote/1/2/3 non recursivenull",
  "mkdir": "mkdir(folder) /tmp/remote/6  non recursivenull",
  "listNames4": "listFiles(folder) /tmp/backup-test-new[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/alternative/Kispálés.txt, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]",
  "fileDateTime": "Getting file date time for /tmp/test7.txt: Fri Apr 12 19:22:10 BST 2019",
  "setfileDateTime": "Setting new Date ->Fri Apr 12 19:23:41 BST 2019",
  "fileDateTime1": "Getting date again 2 : Fri Apr 12 19:23:41 BST 2019",
  "filePermission": "Getting file Permission /tmp/test7.txt: 644",
  "filePermissionString": "Getting chmod file Permission /tmp/test7.txt: rw- r-- r--",
  "fileUserId": "Getting file Userid: 1000",
  "setFileUserId": "setFileUserId file Userid to root: null",
  "fileUserId1": "Getting file Userid again: 1000",
  "setfilePermission": "Setting file Permission /tmp/test7.txt to 755: ",
  "filePermissionString4": "Getting chmod file Permission /tmp/test7.txt: rwx r-x r-x",
  "filePermission1": "Getting Human readable file Permission: 755",
  "filePermission2": "Getting Octal file Permission : 0100755",
  "filePermission3": "Getting file Decimal Permission: 33261",
  "filePermission5": "Getting file Binary Permission: 1000000111101101",
  "setfilePermission1": "Setting file Permission /tmp/test7.txt to 644: ",
  "filePermissionString6": "Getting chmod file Permission /tmp/test7.txt: rw- r-- r--",
  "threadedExecutorResults1": null,
  "threadedExecutorResults2": null,
  "filePermissionString7": "Getting chmod file Permission  after closure with disconnections: /tmp/test7.txt: rw- r-- r--",
  "threadedExecutorResults4": null,
  "threadedExecutorResults3": null,
  "threadedExecutorResults5": null,
  "threadedExecutorResults6": null
}
```