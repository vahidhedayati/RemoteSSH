
```groovy
def newConnectionTest() {
		
		/**
		 * please note we are getting a connection but in default case where everything including host is configured
		 * in the configuration file you don't even need a connection 
		 * 
		 * Please note by establishing a connection once in any call means it can be reused
		 * 
		 * if it is not provided and end command is run then a new connection is launched upon each call
		 * 
		 * 
		 * in our example we are going to reuse the one connection that we start with
		 * 
		 * This is ran under Linux and it uses the utility to create and read / write files under /tmp
		 *
		 */
		
		//----------------------- CONNECTION ------------------------------------ //
		
		//If all set in config you can call like this
		// Connection connection = sshUtilService.openConnection
		
		//Or
		// Connection connection = sshUtilService.openConnection("localhost")
		
		//Or
		// Connection connection = sshUtilService.openConnection("localhost",22)
		
		//Or  ---- this is what we are using
		Connection connection = sshUtilService.openConnection("localhost", 22, 'username','password')
		
		//Or
		//Connection connection = sshUtilService.openConnection("localhost", 22, 'username','keyfile','keyfilePass')
		
		
		//Now that you have connection we are going to reuse it to do many things;
		
		String ranCommand = sshUtilService.execute(connection,'mkdir /tmp/backup-test')
		String ranCommand1 = sshUtilService.execute(connection,'echo "hi there" > /tmp/test.txt')
		String ranCommand2 = sshUtilService.execute(connection,'echo "hi there" > /tmp/Kispálés.txt')
		//or 
		//ranCommand = sshUtilService.execute('mkdir /tmp/backup-test && echo "hi testing" > /tmp/test.txt')
		//where connection is created as per config and on each call 
		println "Ran command ${ranCommand} ${ranCommand1} "
		
		String readRemoteFile = sshUtilService.readRemoteFile('/tmp/test.txt',connection)
		//if we did not want to initiate connection and get it to trigger connection based on config
		//sshUtilService.readRemoteFile('/tmp/test.txt')
		
		println "Read readRemoteFile a file ${readRemoteFile}"
		
		
		sshUtilService.writeFile('/tmp/test.txt','/tmp/backup-test/',connection)
		sshUtilService.writeFile('/tmp/Kispálés.txt','/tmp/backup-test/',connection)
		
		sshUtilService.writeFileWithName('/tmp/test.txt','/tmp/backup-test/happy.txt',connection)
		
		sshUtilService.writeFileWithName('/tmp/test.txt','/tmp/backup-test/Kispálés-happy.txt',connection)
		
		//sshUtilService.writeFileWithName('/tmp/test.txt','/tmp/backup-test/Kispálés.txt',connection)
		
		
		//Please NOTE READ REMOTE FILE closes can connection
		String readACopiedRemoteFile = sshUtilService.readRemoteFile('/tmp/backup-test/test.txt',connection)
		println "Read copied file as Remote File ${readACopiedRemoteFile}"
		
		
		//String readACopiedRemoteFile1 = sshUtilService.readRemoteFile('/tmp/backup-test/Kispálés.txt',connection)
		//println "Read copied file as Remote File ${readACopiedRemoteFile1} /tmp/backup-test/Kispálés.txt"
		//add true to close connection
		//String readACopiedRemoteFile = sshUtilService.readRemoteFile('/tmp/backup/test.txt',connection,true)
		//println "Read copied file as Remote File ${readACopiedRemoteFile}"
		
		
		sshUtilService.connClose(connection)
```



