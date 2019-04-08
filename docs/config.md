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

Or
```

remotessh {
	HOST = "localhost"
	USER = "mx1"
	//PASS=""
	KEY="/home/mx1/.ssh/id_rsa"
	KEYPASS=""
	PORT="22"
	
	/*
	 * keepAliveTime in seconds
	 */
	keepAliveTime=300
	
	/*
	 * corePoolSize this should match maximumPoolSize
	 *
	 */
	corePoolSize=3
	/*
	 * maxPoolSize
	 *
	 */
	maximumPoolSize=3
	
	/*
	 * Amount of elements that can queue
	 */
	maxQueue=100
	
}
//0.11 new customised config values can be defined 

mySshConfigVar.HOST = "localhost"
mySshConfigVar.USER = "mx1"
mySshConfigVar.PASS="password"
//mySshConfigVar.KEY="/home/mx1/.ssh/id_rsa"
//mySshConfigVar.KEYPASS=""
mySshConfigVar.PORT="22"


mySshConfigVar2.HOST = "localhost"
mySshConfigVar2.USER = "mx1"
mySshConfigVar2.PASS="password"
//mySshConfigVar.KEY="/home/mx1/.ssh/id_rsa"
//mySshConfigVar.KEYPASS=""
mySshConfigVar2.PORT="22"


```