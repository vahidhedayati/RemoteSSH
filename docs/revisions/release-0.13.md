When executing http://localhost:8080/test-rssh/newWays 
This calls index method in test-rssh project:

```
/**
 * Tests latest currently 0.13
 * @return
 */
def index() {
	boolean singleInstance=false
	SSHUtil sshUtil = sshUtilService.initialise('username','password','localhost',22,singleInstance)
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
		/tmp/test.txt: (using readRemoteFile) """+sshUtilService.readRemoteFile(sshUtil,'/tmp/test.txt')
		
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
	
	output.hidden1="/tmp/.hiddenTest.txt is hidden  = "+sshUtil.isHiddenFile('/tmp/.hiddenTest.txt')
	output.hidden2="/tmp/.hiddenTest.txt is not hidden  = "+sshUtil.isNotHiddenFile('/tmp/.hiddenTest.txt')
	output.hidden3="/tmp/test4.txt is hidden  = "+sshUtil.isHiddenFile('/tmp/test4.txt')
	output.hidden4="/tmp/test4.txt is hidden  = "+sshUtil.isNotHiddenFile('/tmp/test4.txt')
	
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
	boolean doesItExist = sshUtil.fileExists()
	println "file exists = ${doesItExist}"
	sshUtil1.deleteRemoteFile('/tmp/test2.txt')
	doesItExist = sshUtil1.fileExists()
	println "file exists = ${doesItExist}"
	sshUtil1.disconnect
	

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
  "ranCommand3": "making dir \n\t\t\t/tmp/backup-sshUtil ",
  "ranCommand4": "Executing echo hi there from sshUtil to file:\n\t\t\t/tmp/test2.txt ",
  "ranCommand5": "Executing echo hi there from sshUtil to file:\n\t\t\t/tmp/Kispálés2.txt ",
  "ranCommand6": "Executing echo hi there from sshUtil test3 to file:\n\t\t\t/tmp/test3.txt ",
  "ranCommand7": "Executing echo hi there from sshUtil test3 to file:\n\t\t\t/tmp/Kispálés3.txt ",
  "ranCommand10": "Executing echo hi there from sshUtil test4 to file:\n\t\t\t/tmp/test4.txt ",
  "ranCommand11": "Executing echo hi there from sshUtil test5 to file:\n\t\t\t/tmp/test5.txt ",
  "ranCommand12": "Executing echo hi there from sshUtil test4 to file:\n\t\t\t/tmp/test6.txt ",
  "ranCommand13": "Executing echo hi there from sshUtil test5 to file:\n\t\t\t/tmp/test7.txt ",
  "ranCommand14": "Executing echo hi to hidden file:\n\t\t\t/tmp/.hiddenTest.txt ",
  "readFile1": "Reading remote file content of \n\t\t\t/tmp/test.txt: (using readRemoteFile) hi there\n",
  "readFile2": "Reading remote file content of \n\t\t\t/tmp/test2.txt: (using readRemoteFile) hi there from sshUtil\n",
  "readFile3": "Reading remote file content of \n\t\t\t/tmp/Kispálés.txt: (using readFile) hi there\n\u0000\u0000",
  "readFile4": "Reading remote file content of \n\t\t\t/tmp/Kispálés2.txt: (using readFile) om sshUtil\n",
  "writeFile1": "Writing local file: /tmp/Kispálés.txt as \n\t\t\t/tmp/backup-test-new/ahha1.txt on \n\t\t\tremote systemnull",
  "mkdir12": "Making recursive dir remote as\n\t\t\t\t/tmp/backup-test-new/alternative/abc null",
  "writeFile22": "Writing local file: /tmp/Kispálés.txt to remote dir \n\t\t\t/tmp/backup-test-new/alternative/null",
  "writeFile2": "Writing local file: /tmp/Kispálés.txt as \n\t\t\t/tmp/backup-test-new/ahha2.txt on \n\t\t\tremote systemnull",
  "writeFile4": "putting file: '/tmp/Kispálés3.txt'\n\t\t\tto /tmp/backup-test-newnull",
  "writeFile5": "putting file: '/tmp/test3.txt'\n\t\t\tto /tmp/backup-test-new28",
  "writeFile6": "putting file: '/tmp/Kispálés3.txt'\n\t\t\tto /tmp/backup-test-newnull",
  "writeFile7": "putting files: ['/tmp/test6.txt','/tmp/test7.txt']\n\t\t\tto /tmp/backup-test-newnull",
  "getRemoteFile1": "Getting REMOTE: \n\t\t\t\t/tmp/backup-test-new/remote-file-example.txt  and copying to: /tmp/\n\t\tnull",
  "getRemoteFile2": null,
  "getRemoteFile3": "Getting REMOTE FILES: \n\t\t\t['/tmp/backup-test-new/remote-file-example2.txt',\n\t\t\t'/tmp/backup-test-new/remote-file-example.txt']  and copying to: /tmp/remote-got\n\t\tnull",
  "getRemoteFile4": "Getting REMOTE FILES: \n\t\t\t['/tmp/backup-test-new/remote-file-example2.txt',\n\t\t\t'/tmp/backup-test-new/remote-file-example.txt'] and copying to: /tmp/remote-got2\n\t\tnull",
  "fileExists1": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt \n\t\t\t?false",
  "fileExists2": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example22.txt \n\t\t\t?false",
  "fileExists3": "Does file Exist: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt \n\t\t\t?false",
  "remoteFileSize1": "remote file size: /tmp/test5.txt 28",
  "remoteFileSize2": "remote file size: /tmp/test5.txt 28",
  "deleteFile1": "remove remote file: \n\t\t\t/tmp/backup-test-new/remote-file-example.txt null",
  "deleteFile2": "remove remote file: \n\t\t\t/tmp/test5.txt null",
  "createDir1": "createRemoteDirs: \n\t\t\t/tmp/remote/1/2/3 null",
  "createDir2": "createRemoteDirs: \n\t\t\t/tmp/remote/3/4/6 null",
  "hidden1": "/tmp/.hiddenTest.txt is hidden  = false",
  "hidden2": "/tmp/.hiddenTest.txt is not hidden  = true",
  "hidden3": "/tmp/test4.txt is hidden  = false",
  "hidden4": "/tmp/test4.txt is hidden  = true",
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
  "listNames4": "listFiles(folder) /tmp/backup-test-new[/tmp/backup-test-new/Kispálés.txt, /tmp/backup-test-new/test3.txt, /tmp/backup-test-new/Kispálés2.txt, /tmp/backup-test-new/test7.txt, /tmp/backup-test-new/test5.txt, /tmp/backup-test-new/test6.txt, /tmp/backup-test-new/alternative/Kispálés.txt, /tmp/backup-test-new/ahha1.txt, /tmp/backup-test-new/ahha2.txt, /tmp/backup-test-new/remote-file-example.txt, /tmp/backup-test-new/Kispálés3.txt, /tmp/backup-test-new/test4.txt, /tmp/backup-test-new/remote-file-example2.txt]"
}
```