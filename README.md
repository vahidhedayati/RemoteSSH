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
