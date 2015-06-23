RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganymed SSH-2 library : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


Dependency Grails 2:

```groovy
	compile ":remote-ssh:0.3"
```

[Grails 2 source](https://github.com/vahidhedayati/RemoteSSH/tree/grails2-v0.3)

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



And now finally a big thank you to Burt Beckwith for adding so much flexibility to the global configuration and additional groovy calls. 


```
