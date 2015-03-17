package grails.plugin.remotessh
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

class SshConfig implements GrailsApplicationAware {

    GrailsApplication grailsApplication

	def getConfig(String configProperty) {
		grailsApplication.config.remotessh[configProperty] ?: ''
	}
}
