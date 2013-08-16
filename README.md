RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganeymed-ssh2-build-210 : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


This plugin uses ganymed-ssh-build210.jar, provides RemoteSSH RemoteSCP RemoteSCPDir RemoteSCPGet
Each one reads in SSHController within the plugin which looks for conf/SSHConfig.groovy in your project:


In your app:

conf/SSHConfig.groovy:

    # Option set a global username to access ssh through to remote host
    # If you are going to define user from above commands then leave it with empty speach marks
		ssh.USER = "USER"

		# The password leave blank if you are about to use SSH Keys, otherwise provide password to ssh auth
		ssh.PASS=""

		# The ssh key is your id_rsa or id_dsa - please note your tomcat will need access/permissions to file/location
		ssh.KEY="/home/youruser/.ssh/id_rsa"

		# If you use a key pass for your key connections then provide it below
		ssh.KEYPASS=""

		# The ssh port to connect through if not given will default to 22
		ssh.PORT="22"




## TestController calling RemoteSSH

		import ssh.RemoteSSH

    class TestController {

      def index() {
        // RemoteSSH as1h=new RemoteSSH(host, user, sudo, usercommand)

	      Integer port=22;

	    	RemoteSSH ash=new RemoteSSH('10.10.10.1', 'myuser', '', 'uname -a','',port)
	      def result=ash.Result()


	      RemoteSSH ash=new RemoteSSH('localhost', 'username', 'password','', 'uptime','',port)
	      def result=ash.Result()
	      render(view: "index", model: [ak: result])

	     }
    }


## Test/index.gsp:
       Hello ${ak}



