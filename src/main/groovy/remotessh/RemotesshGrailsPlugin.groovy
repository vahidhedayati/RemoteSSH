package remotessh

import grails.plugin.remotessh.SshConfig
import grails.plugins.*

class RemotesshGrailsPlugin extends Plugin {
    def grailsVersion = "3.0 > *"
    def title = "Remote SSH Plugin"
    def author = "Vahid Hedayati"
    def authorEmail = "badvad@gmail.com"
    def description = 'Uses the Ganymed SSH-2 library to provide RemoteSSH, RemoteSCP, RemoteSCPDir, and RemoteSCPGet'
    def documentation = "http://grails.org/plugin/remote-ssh"
    def license = "Apache-2.0"
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/vahidhedayati/RemoteSSH/issues']
    def scm = [url: 'https://github.com/vahidhedayati/RemoteSSH']

    Closure doWithSpring() { {->
        sshConfig(SshConfig)
        } 
    }
}
