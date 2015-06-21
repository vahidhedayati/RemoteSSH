package grails.plugin.remotessh

class RsshTagLib {
	
   static namespace  =  "rssh"
   
   def rsshService
   
   def runCommand={attrs->
	   out << rsshService.runCommand(attrs)   
   }
   
   def scpDir={attrs->
	   out << rsshService.scpDir(attrs)
   }
   
   def scpFile={attrs->
	   out << rsshService.scpFile(attrs)
   }
   
   def scpGet={attrs->
	   out << rsshService.scpGet(attrs)
   }
   
}
