RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganymed SSH-2 library : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


A lot of changes made by and thanks to: burtbeckwith

      changed to use a Spring bean that reads configuration values from Config.groovy instead. 
      This is also more flexible because you can take advantage of environment support, externalized configuration, etc. 
      The one usage change is that you need to dependency-inject the "sshConfig" bean in your controller/service/etc and passthat to the Result method
      optional rewrite from Java to Groovy. I removed the constructors since in Groovy it's easier to use the Map constructor, and it has the benefit of being more readable, e.g. new RemoteSSH(host: '10.10.10.1', user: 'myuser', usercommand: 'uname -a', port: port)


# Config.groovy variables required:

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
          int port=22

          RemoteSSH ash=new RemoteSSH('10.10.10.1', 'myuser', '', 'uname -a','',port)
          def result=ash.Result(sshConfig)


          RemoteSSH ash=new RemoteSSH('localhost', 'username', 'password','', 'uptime','',port)
          result=ash.Result(sshConfig)
          render(view: "index", model: [ak: result])
       }
    }


## Test/index.gsp:
    RemoteSSH result as follows:<br> ${ak}





## RemoteSSH construct, fields explained:

           RemoteSSH ash=new RemoteSSH('hostname_to_connect_to','username_to_user_optional', 
           				'password_for_defined_user_optional','sudo_optional', 
	   				'command to run','filter_for_optional',port)
	   	The above is the full construct and options
	   	optional means empty '' fields can be provided
	   	in the case of sudo_optional change this to 'sudo' and it will exec command as sudo, 
	   	so long as user can sudo without passwords etc on remote host




## How to run it from a service 

# 1. Dependancy injection with examples for RemoteSSH (groovy) and RemoteSSH (Java)

1.1 Service for Java Construct call :

This will send a command through connect to given ip and username. Passsword is blank and so is sudo and lastly filter


	package test1

	import grails.plugin.remotessh.SshConfig
	import ssh.RemoteSSH

	class ConnectService {

   		def grailsApplication
   		SshConfig sshConfig=new SshConfig()
   	
   		def getResult(String command) {	  
	   		RemoteSSH rsh=new RemoteSSH('XX.XX.XX.XX', 'myuser','', '', command,'',0)
	   		def g= rsh.Result(sshConfig)
	   		return g
   		}

	}



1.2 Service for Groovy call of RemoteSSH


	package test1

	import grails.plugin.remotessh.RemoteSSH
	import grails.plugin.remotessh.SshConfig

	class ConnectService {

   		def grailsApplication
   		SshConfig sshConfig=new SshConfig()
   		
   		def getResult(String command) {	  
	
	   		RemoteSSH rsh=new RemoteSSH()
	   		rsh.setHost('XX.XX.XX.XX')
	   		rsh.setPort(0)
	   		rsh.setUser('myuser')
	   		rsh.setUsercommand('whoami')
	   		def g=rsh.Result(sshConfig)
	   		return g
   		}

	}






1.2 Taglib

         package test1
         class ConnectTagLib {
         static namespace = "connectit"
         def connectService
         def getResult=  { attrs, body ->
         	def command= attrs.remove('command')?.toString()
         	if(command) {
         		def result = connectService.getResult(command)
         		out << "${result}"
         	}
         }	
        }



1.3 GSP:

      <connectit:getResult command="ls -l" />
      
1.5 Controller:


	def get1= {
		render (view : 'index1', model : [:]) 
	}








	   
# RemoteSSH Construct Variations:

    RemoteSSH rsh=RemoteSSH(host,usercommand)
    RemoteSSH rsh=new RemoteSSH(host, sudo, usercommand) 
    RemoteSSH rsh=new RemoteSSH(host, user, sudo, usercommand) 
    RemoteSSH rsh=new RemoteSSH(host, user, sudo, usercommand,filter) 
    RemoteSSH rsh=new RemoteSSH(host, user, sudo, usercommand, port) 
    RemoteSSH rsh=new RemoteSSH(host, user, sudo, usercommand,filter, port) 
    RemoteSSH rsh=new RemoteSSH(host, user, userpass, sudo, usercommand,filter,port) 



# RemoteSSH With some more examples of above calls:

First example overrides config.groovy remotessh.USER so it uses the construct with sudo and runs uptime
This then reads in the key config and keypass and connects via ssh to remote host


	RemoteSSH ash4=new RemoteSSH('1xx.xx.xxx.xx', 'remoteuser', 'sudo', 'uptime')
	def result3=ash4.Result(sshConfig)
	
If I had defined the same user in my global config and the correct port etc then I could have just used:

        RemoteSSH rsh=RemoteSSH(String host, String usercommand)
         
         
# RemoteSCP Call types:

     RemoteSCP rscp=new RemoteSCP(host, file, remotedir) 
     RemoteSCP rscp=new RemoteSCP(host, user, file, remotedir) 
     RemoteSCP rscp=new RemoteSCP(host, user, userpass, file, remotedir) 
     RemoteSCP rscp=new RemoteSCP(host, file, remotedir, port) 
     RemoteSCP rscp=new RemoteSCP(host, user, file, remotedir, port) 
     RemoteSCP rscp=new RemoteSCP(host, user, userpass, file, remotedir, port) 


# RemoteSCPDir call types:

    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, localdir,remotedir) 
    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, user, localdir,remotedir) 
    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, user,userpass, localdir,remotedir) 
    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, localdir,remotedir, port) 
    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, user, localdir,remotedir, port) 
    RemoteSCPDir rscpdir=new RemoteSCPDir(hostname, user, userpass, localdir,remotedir, port) 


# RemoteSCPGet call types:

    RemoteSCPGet rscpdir=new RemoteSCPGet(host, file, localdir) 
    RemoteSCPGet rscpdir=new RemoteSCPGet(host, user, file, localdir) 
    RemoteSCPGet rscpdir=new RemoteSCPGet(host, user, userpass, file, localdir) 
    RemoteSCPGet rscpdir=new RemoteSCPGet(host, file, localdir,port) 
    RemoteSCPGet rscpdir=new RemoteSCPGet(host, user, file, localdir,port) 
    RemoteSCPGet rscpdir=new RemoteSCPGet(host, user, userpass, file, localdir,port) 

  


## Controller containing all of this plugin in use:

    import ssh.RemoteSCP
    import ssh.RemoteSCPDir
    import ssh.RemoteSCPGet
    import ssh.RemoteSSH
    
    
    class TestController {
       def sshConfig
       int port=22
       

Action called sshexec:

       def sshexec() {
       

This will run uptime without sudo or any filters
	   
          RemoteSSH ash=new RemoteSSH('localhost', 'myusername', 'mypassword','', 'uptime','',port)
          def result=ash.Result(sshConfig)
          
This will run whoami after sudoing 

	 RemoteSSH ash1=new RemoteSSH('localhost', 'myusername', 'mypassword','sudo', 'whoami','',port)
	 def result1=ash1.Result(sshConfig)
	 
	 
This will sudo run cat /etc/passwd and looks for jenkins. At the moment it only looks at lines that start with.


	 RemoteSSH ash2=new RemoteSSH('localhost', 'myusername', 'mypassword','', 'cat /etc/passwd','jenkins',port)
	 def result2=ash2.Result(sshConfig)

	
	 def returnit="First:"+result+"<br>Second:"+result1+"<br>Third:"+result2
	 render(view: "index", model: [ak: returnit])
	}


Picking up on above calls, you could write a shell (semicoloned lines and if you line ;\n so on remote host is gets executed like a shell script. Then set response to conatin something like __:, the set filter to look for __:



Action called scp:

	def scp() {  
	 RemoteSCP scp1=new RemoteSCP('localhost', 'myusername', 'mypassword', '/tmp/11.txt' ,'/var/tmp', port)
	 def result=scp1.Result(sshConfig)

	 def returnit="SCP file example:"+result
	 render(view: "index", model: [ak: returnit])
	 
	}


Action called scpdir:

	def scpdir() {
Warning this strips remote folder so on local host the files ended up in /tmp/abc and not /tmp/abc/abc1


		RemoteSCPDir scpdir1=new RemoteSCPDir('localhost', 'myusername', 'mypassword', '/tmp/abc' ,'/var/tmp/abc1', port)
		def result=scpdir1.Result(sshConfig)
   
		def returnit="SCP Directory example:"+result
		render(view: "index", model: [ak: returnit])
	}
	
		  
Action called scpget:
	
	def scpget() {
This will get a remote file and store it in given local path


		RemoteSCPGet scpget1=new RemoteSCPGet('localhost', 'myusername', 'mypassword', '/var/tmp/get1.txt' ,'/tmp', port)
		def result=scpget1.Result(sshConfig)
   
		def returnit="SCP Get File example:"+result
		render(view: "index", model: [ak: returnit])
	}
	
	
   }





# Wrong way of doing a service:

Not really the correct way of doing things, this is the lengthy way of doing things by passing sshConfig from controller to gsp then from gsp to taglib and from taglib to service:

Controller:

        class TestController {
         	def sshConfig
         	def get1() {
         		render(view: "index1", model: [sshConfig:sshConfig])
         	}


View/GSp:

                <connectit:getResult command="ls -l" sshConfig="${sshConfig }" />
                
                
TagLib:

               package test1
               class ConnectTagLib {
              	static namespace = "connectit"
              	def connectService
              	def getResult=  { attrs, body ->
              	def command= attrs.remove('command')?.toString()
              	def sshConfig =attrs.remove('sshConfig')
              	if(command) {
              		def result = connectService.getResult(command,sshConfig)
              		out << "${result}"
              	}	
              }
              
Service:

              package test1
              import grails.plugin.remotessh.SshConfig
              import ssh.RemoteSSH
              class ConnectService {
                def getResult(String command, SshConfig sshConfig) {	  
                  RemoteSSH rsh=new RemoteSSH('ip', 'user','', '', command,'',0)
                  return rsh.Result(sshConfig)
                }  
            }
            
            
            

And now finally a big thank you to Burt Beckwith for all his input and making this a lot more flexible than was originally written.

I have written a wrapper for this using 

	String scriptName 
	String scriptContent
	// This being hidden field for user logged in who added the script
	String addedby
	Boolean sudo

	static mapping = { scriptContent type: "text" } }


The scripts are then dynamically listed as options and a next user can select to run them on the given remote host...


