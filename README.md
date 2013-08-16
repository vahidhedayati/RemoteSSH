RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganeymed-ssh2-build-210 : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


This plugin uses the Ganymed SSH-2 library, provides RemoteSSH RemoteSCP RemoteSCPDir RemoteSCPGet
Configure SSH and SCP by adding properties to grails-app/conf/Config.groovy under the "remotessh" key:


    # Option set a global username to access ssh through to remote host
    # If you are going to define user from above commands then leave it with empty speach marks
      remotessh.USER = "USER"

      # The password leave blank if you are about to use SSH Keys, otherwise provide password to ssh auth
      remotessh.PASS=""

      # The ssh key is your id_rsa or id_dsa - please note your tomcat will need access/permissions to file/location
      remotessh.KEY="/home/youruser/.ssh/id_rsa"

      # If you use a key pass for your key connections then provide it below
      remotessh.KEYPASS=""

      # The ssh port to connect through if not given will default to 22
      remotessh.PORT="22"




## TestController calling RemoteSSH

    import ssh.RemoteSSH

    class TestController {

	    def sshConfig

       def index() {
          // RemoteSSH as1h=new RemoteSSH(host, user, sudo, usercommand)

          int port=22

          RemoteSSH ash=new RemoteSSH('10.10.10.1', 'myuser', '', 'uname -a','',port)
          def result=ash.Result(sshConfig)


          RemoteSSH ash=new RemoteSSH('localhost', 'username', 'password','', 'uptime','',port)
          result=ash.Result(sshConfig)
          render(view: "index", model: [ak: result])
       }
    }


## Test/index.gsp:
    Hello ${ak}







Here are a list of all the different uses:

    import ssh.RemoteSCP
    import ssh.RemoteSCPDir
    import ssh.RemoteSCPGet
    import ssh.RemoteSSH
    class TestController {
       def sshConfig
       def sshexec() {
        // RemoteSSH as1h=new RemoteSSH(host, user, sudo, usercommand)
	 int port=22

        //RemoteSSH ash=new RemoteSSH('10.10.10.1', 'myuser', '', 'uname -a','',port)
        //  def result=ash.Result(sshConfig)

	   /* RemoteSSH ash=new RemoteSSH('hostname_to_connect_to', 
	                                  'username_to_user_optional', 
	                                  'password_for_defined_user_optional',
	   								  'sudo_optional', 
	   							      'command to run',
	   							      'filter_for_optional',
	   							       port
	   							     )
	   	The above is the full construct and options
	   	optional means empty '' fields can be provided
	   	in the case of sudo_optional change this to 'sudo' and it will exec command as sudo, 
	   	so long as user can sudo without passwords etc on remote host
	   */
	   
	   
	   // This will run uptime without sudo or any filters
	   
          RemoteSSH ash=new RemoteSSH('localhost', 'myusername', 'mypassword','', 'uptime','',port)
          def result=ash.Result(sshConfig)
	 
	 // This will run whoami after sudoing 
	 RemoteSSH ash1=new RemoteSSH('localhost', 'myusername', 'mypassword','sudo', 'whoami','',port)
	 def result1=ash1.Result(sshConfig)
	 
	 // This will sudo run cat /etc/passwd and looks for jenkins 
	 // There is a bug here since it looks for pattern at start of line rather than anywhere in the line
	 // will fix this issue in next release of the plugin
	 RemoteSSH ash2=new RemoteSSH('localhost', 'myusername', 'mypassword','', 'cat /etc/passwd','jenkins',port)
	 def result2=ash2.Result(sshConfig)
	
	 def returnit="First:"+result+"<br>Second:"+result1+"<br>Third:"+result2
	 render(view: "index", model: [ak: returnit])
	 //returns as expected too long to display
	 
	}
	
	def scp() {  
	 int port=22
	 RemoteSCP scp1=new RemoteSCP('localhost', 'myusername', 'mypassword', '/tmp/11.txt' ,'/var/tmp', port)
	 def result=scp1.Result(sshConfig)

	 def returnit="SCP file example:"+result
	 render(view: "index", model: [ak: returnit])
	 // returns
	 // SCP file example:File /tmp/11.txt should now be copied to localhost:/var/tmp
	   }
	
	def scpdir() {
		int port=22
		// Bug noticed since it copied abc content into abc1 folder and the abc1 folder had to exist
		// In short strips first folder will look at this
		RemoteSCPDir scpdir1=new RemoteSCPDir('localhost', 'myusername', 'mypassword', '/tmp/abc' ,'/var/tmp/abc1', port)
		def result=scpdir1.Result(sshConfig)
   
		def returnit="SCP Directory example:"+result
		render(view: "index", model: [ak: returnit])
		// Returns:
		//SCP Directory example:/tmp/abc should now be copied to localhost:/var/tmp/abc1
		  }
	
	def scpget() {
		int port=22
		// This will get a remote file and store it in given local path
		RemoteSCPGet scpget1=new RemoteSCPGet('localhost', 'myusername', 'mypassword', '/var/tmp/get1.txt' ,'/tmp', port)
		def result=scpget1.Result(sshConfig)
   
		def returnit="SCP Get File example:"+result
		render(view: "index", model: [ak: returnit])
		// Returns:
		// SCP Get File example:File /var/tmp/get1.txt should now be copied from localhost to localdir: /tmp
		  }
	
	
    }

