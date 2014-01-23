import grails.plugin.remotessh.SshConfig

class RemoteSshGrailsPlugin {
	def version = "0.2"
	def grailsVersion = "2.0 > *"
	def title = "Remote SSH Plugin"
	def author = "Vahid Hedayati"
	def authorEmail = "badvad@gmail.com"
	def description = 'Uses the Ganymed SSH-2 library to provide RemoteSSH, RemoteSCP, RemoteSCPDir, and RemoteSCPGet'
	def documentation = "http://grails.org/plugin/remote-ssh"
	def license = "GPL2"
	def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/RemoteSSH/issues']
	def scm = [url: 'https://github.com/vahidhedayati/RemoteSSH']

	def doWithSpring = {
		sshConfig(SshConfig) {
			grailsApplication = ref('grailsApplication')
		}
	}
}
