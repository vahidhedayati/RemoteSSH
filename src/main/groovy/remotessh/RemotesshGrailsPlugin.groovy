package remotessh

import grails.plugin.remotessh.SshConfig
import grails.plugins.*

class RemotesshGrailsPlugin extends Plugin {
    def grailsVersion = "3.0 > *"
    def title = "Remote SSH Plugin"
    def description = 'Uses the Ganymed SSH-2 library to provide RemoteSSH, RemoteSCP, RemoteSCPDir, and RemoteSCPGet'
    def documentation = "https://github.com/vahidhedayati/RemoteSSH"
    def license = "APACHE"
    def developers = [name: 'Vahid Hedayati', email: 'badvad@gmail.com']
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/RemoteSSH/issues']
    def scm = [url: 'https://github.com/vahidhedayati/RemoteSSH']
    Closure doWithSpring() { {->
        sshConfig(SshConfig)
        } 
    }
}
