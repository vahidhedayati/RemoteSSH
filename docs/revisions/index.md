
[How to use 0.14+/3.0.9+ revision](https://github.com/vahidhedayati/RemoteSSH/docs/release-0.14.md)  -- current 

[How to use 0.13+ revision](https://github.com/vahidhedayati/RemoteSSH/docs/release-0.13.md)

[How to use 0.10 - 0.13 revision](https://github.com/vahidhedayati/RemoteSSH/docs/revision-0.10.md)

[How to use 0.9 revision](https://github.com/vahidhedayati/RemoteSSH/docs/revision-0.9.md)



Much much older - ancient code 

### [Youtube video walking through 0.3](https://www.youtube.com/watch?v=v_0nNJX4Xmk)

#### [older method TestController calling RemoteSSH Grails 2](https://github.com/vahidhedayati/RemoteSSH/wiki/older-method)

#### [older method TestController calling RemoteSSH Grails 3](https://github.com/vahidhedayati/RemoteSSH/wiki/older-method-grails3)

#### [0.3+ gsp taglib call : run remote command ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/controllers/test/rssh/TestController.groovy#L21-L26)

#### [0.3+ gsp taglib call : remote SCP Directory ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpDir.gsp)

#### [0.3+ gsp taglib call : remote SCP File ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpFile.gsp)

#### [0.3+ gsp taglib call : remote SCP Get File ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/views/test/scpGet.gsp)

#### [0.3+ gsp taglib call : run remote command + reuse connection ](https://github.com/vahidhedayati/test-rssh/blob/master/grails-app/controllers/test/rssh/TestController.groovy#L52-L89)

#### [Shell script example:](https://github.com/vahidhedayati/RemoteSSH/wiki/shell-script-example)




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
