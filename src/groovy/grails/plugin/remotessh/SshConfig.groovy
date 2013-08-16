package grails.plugin.remotessh

class SshConfig {

	def grailsApplication

	def getConfig(String configProperty) {
		grailsApplication.config.remotessh[configProperty] ?: ''
	}
}
