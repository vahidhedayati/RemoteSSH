RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganymed SSH-2 library : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


Dependency Grails 2:

```groovy
	compile ":remote-ssh:0.7"
```

[Grails 2 source](https://github.com/vahidhedayati/RemoteSSH/tree/grails2)

Dependency Grails 3 (build.gradle):

```groovy
	compile "org.grails.plugins:remotessh:3.0.1"
```
[Grails 3 source](https://github.com/vahidhedayati/RemoteSSH)
	


	
#### For websocket live ssh connection / interaction: 
Check out : [jssh](https://github.com/vahidhedayati/jssh)


# Config.groovy variables required:

Configure SSH and SCP by adding properties to grails-app/conf/Config.groovy under the "remotessh" key:
```groovy
//Option set a global username to access ssh through to remote host
//If you are going to define user from above commands then leave it with empty speach marks
remotessh.USER = "USER"

//The password leave blank if you are about to use SSH Keys, otherwise provide password to ssh auth
remotessh.PASS=""

//The ssh key is your id_rsa or id_dsa - please note your tomcat will need access/permissions to file/location
remotessh.KEY="/home/youruser/.ssh/id_rsa"

//If you use a key pass for your key connections then provide it below
remotessh.KEYPASS=""

//The ssh port to connect through if not given will default to 22
remotessh.PORT="22"
```

### [Youtube video walking through 0.3](https://www.youtube.com/watch?v=v_0nNJX4Xmk)

#### [older method TestController calling RemoteSSH Grails 2](https://github.com/vahidhedayati/RemoteSSH/wiki/older-method)

#### [older method TestController calling RemoteSSH Grails 3](https://github.com/vahidhedayati/RemoteSSH/wiki/older-method-grails3)

#### [0.3+ gsp taglib call : run remote command ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/controllers/test/rssh/TestController.groovy#L21-L26)

#### [0.3+ gsp taglib call : remote SCP Directory ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpDir.gsp)

#### [0.3+ gsp taglib call : remote SCP File ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpFile.gsp)

#### [0.3+ gsp taglib call : remote SCP Get File ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpGet.gsp)

#### [0.3+ gsp taglib call : run remote command + reuse connection ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/controllers/test/rssh/TestController.groovy#L52-L89)

#### [Shell script example:](https://github.com/vahidhedayati/RemoteSSH/wiki/shell-script-example)

## [Demo site grails 2.4.4 remote-ssh:0.3](https://github.com/vahidhedayati/test-rssh)
## [Demo site grails 3.0.1 remote-ssh:3.0.1](https://github.com/vahidhedayati/testrssh)

Updates on 0.9 of grails 2 branch:
----

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


# Version Info
```
0.6:	Override methods added for executeCommand possibly causing issues depending on input/config

0.5 :   typo: constraints - further clean up of code
0.4 : 	Removed Map validateParams out and introduced src/groovy/RsshValidate - updated all calls to use new bean for validation.
		Added try catch around new service methods - messages now caught and sent to frontend - no more stacktraces using new service.
		
0,3 : 	Implementation of RsshService + RsshTagLib easier/alternative calls to access plugin
0.2 : 	Tidyup and throwables added by Tobias Tschech
0.1 : 	Initial release - using src/groovy calls - a lot of tidy up by Burt Beckwith 
```
