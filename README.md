RemoteSSH
=========

Grails RemoteSSH Plugin based on Ganymed SSH-2 library : Provides ( RemoteSSH + exec command ) (RemoteSCP) (RemoteSCPDir) (RemoteSCPGet)


Dependency Grails 2:

```groovy
	compile ":remote-ssh:0.2"
```

Dependency Grails 3:

```groovy
	compile ":remote-ssh:0.3"
```


Grails 3:  https://bintray.com/artifact/download/vahid/maven/RemoteSSH-0.3.jar
	

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
```groovy

package testrssh
import grails.core.GrailsApplication
import grails.plugin.remotessh.RemoteSSH

class TestController {

    GrailsApplication grailsApplication


    def index() {

        ConfigObject remotessh = grailsApplication.config.remotessh

        RemoteSSH rsh = new RemoteSSH()


        // THIS IS NOT DEFINED - since the user / key values passed in via configObject above..
        //rsh.setUser('someUser')
        //rsh.setUserpass('somePass')

        rsh.setHost('someHOST')
        rsh.setPort(22)
        rsh.setUsercommand('hostname -s && uname -a && whoami')
        def g = rsh.Result(remotessh)
        render "---- $g"

    }

}
```

## Test/conf/application.groovy:
```groovy
remotessh.KEY="/home/vahid/.ssh/id_rsa"
remotessh.USER = "vahid"
```



# 2. Shell script example:
I am about to pgrep mysqld then for the pid split out all of its running config then parse for datadir|socket|default-file and return the pid with those values starting with __:, all on one line.

```bash
	 mysqlpid=$(pgrep mysqld);for mid in $mysqlpid; do echo -n "__:$mid config is ";
	 cfg1=$(ps wp $mid|grep $mid|sed -e 's/ \-/\n\-/g'|grep '^-'|egrep "datadir|socket|default-file"|tr  "\n\r" " ");
	 echo  "$cfg1"; done
```
The above works fine on local terminal, and when if you do decide to create like example given at the very bottom to have a
 domainclass manage scripts, you should be able to put above into the shell script textarea of your domainclass --> scriptContent.


But in order to make it work via the service as is, additional \backslashes have to be added:


```groovy
	 String testscript2='mid=$(pgrep mysql);for mpid in $mid; do echo -n \"__:$mpid config is \";
	 mdet=$(ps wp $mpid|grep $mpid|sed -e \'s/ \\-/\\n\\-/g\'|grep \'^-\'|egrep \"datadir|socket|default-file\"|tr  \"\\n\\r\" \" \");
	 echo  \"$mdet\"; done'


```
With that in place it should connect to remote host run that script and look out for __: displaying this back to the screen


2.1 Remove filter : basic replace, where __: was the filter:

    return rsh.Result(remotessh).toString().replace('__:', '')

