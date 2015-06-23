package grails.plugin.remotessh

import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware

class RsshConfService implements GrailsApplicationAware {

	static transactional  =  false

	def config

	void setGrailsApplication(GrailsApplication grailsApplication) {
		config = grailsApplication.config.remotessh
	}


}
