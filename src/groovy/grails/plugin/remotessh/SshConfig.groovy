package grails.plugin.remotessh

class SshConfig {
	def getConfig(String configProperty) {
		def config = new ConfigSlurper().parse(new File('grails-app/conf/SSHConfig.groovy').toURL())
		configProperty.split(/\./).inject(config) { co, k ->
		  co."${k}"
		}
	}
}
