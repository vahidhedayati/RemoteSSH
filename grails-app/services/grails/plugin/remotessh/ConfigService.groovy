package grails.plugin.remotessh

import grails.core.GrailsApplication


public class ConfigService {

    GrailsApplication grailsApplication


    public ConfigObject getConfig() {
        def aa = grailsApplication?.config?.remotessh ?: ''
        println "_ WE HAVE ${aa}"
        return aa
    }

}
