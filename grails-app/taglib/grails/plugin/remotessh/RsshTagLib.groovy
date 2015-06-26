package grails.plugin.remotessh

/*
 * @author  Vahid Hedayati - 21st June 2015
 * 
 * Validation bean parsing attributes passing to rsshService
 *
 */

class RsshTagLib {
	
   static namespace  =  "rssh"
   
   def rsshService
   
   def runCommand={attrs ->
	   def rBean = new RsshValidate(attrs)
	   out << rsshService.runCommand(rBean) 
   }
   def scpDir={attrs->
	   def rBean = new RsshValidate(attrs)
	   out << rsshService.scpDir(rBean)
   }
   
   def scpFile={attrs->
	   def rBean = new RsshValidate(attrs)
	   out << rsshService.scpFile(rBean)
   }
   
   def scpGet={attrs->
	   def rBean = new RsshValidate(attrs)
	   out << rsshService.scpGet(rBean)
   }
   
}
