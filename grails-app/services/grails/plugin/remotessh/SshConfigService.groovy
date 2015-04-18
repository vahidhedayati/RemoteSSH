package grails.plugin.remotessh

import grails.core.GrailsApplication


public class SshConfigService {

    GrailsApplication grailsApplication


    def getConfig() {
        grailsApplication?.config?.remotessh ?: ''
    }

}
