package grails.plugin.remotessh

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

class SshConfig implements GrailsApplicationAware {

    GrailsApplication grailsApplication


    public ConfigObject getConfig() {
        return grailsApplication.config.remotessh ?: ''

    }

}