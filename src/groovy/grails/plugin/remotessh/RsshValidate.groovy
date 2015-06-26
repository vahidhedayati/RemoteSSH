package grails.plugin.remotessh

import grails.util.Holders
import grails.validation.Validateable

/*
 * @author  Vahid Hedayati - 26th June 2015
 *
 * Validation bean used in conjunction with taglib attributes 
 * and parameters passed to service.
 *
 */

@Validateable
class RsshValidate {

	String sshuser = getConfig('USER') as String
	String sshpass = getConfig('PASS') as String
	String sshkey = getConfig('KEY') as String
	String sshkeypass = getConfig('KEYPASS') as String
	def sshport = (getConfig('PORT'))?:22
	String host = 'localhost'
	String splitter='<br>'
	
	File keyfile = new File(sshkey)

	//runCommand params
	String usercommand=''
	String sudo=''
	String filter=''

	//scpDir commands
	String localDirectory=''
	String remoteTargetDirectory=''
	String mode=''

	//scpGet commands and scp
	String file=''
	String localdir=''
	String remotedir=''
	
	static contstraints = {
		usercommand nullable: true
		file nullable: true
		sudo nullable: true
		filter nullable:true
		localDirectory nullable: true
		remoteTargetDirectory nullable: true
		file nullable: true
		localdir nullable:true
		remotedir nullable:true
		host nullable:true
		sshuser nullable:true
		sshpass nullable:true
		sshkey nullable:true
		sshkeypass nullable:true
		
	}

	def getConfig(String configProperty) {
		Holders.config.remotessh[configProperty] ?: ''
	}
}
