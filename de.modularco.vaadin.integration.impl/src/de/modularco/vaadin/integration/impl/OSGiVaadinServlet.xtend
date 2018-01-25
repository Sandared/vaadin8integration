package de.modularco.vaadin.integration.impl

import com.vaadin.server.DeploymentConfiguration
import com.vaadin.server.ServiceException
import com.vaadin.server.VaadinServlet
import com.vaadin.server.VaadinServletService
import org.osgi.framework.Bundle

class OSGiVaadinServlet extends VaadinServlet {
	
	Bundle bundle
	
	new (Bundle bundle){
		this.bundle = bundle
	}
	
	override protected createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
		try {
			val service = new VaadinServletService(this, deploymentConfiguration)
			service.init
			service.setClassLoader(new ClassLoader {
				
				override loadClass(String name) throws ClassNotFoundException {
					// The Vaadin BootstrapHandler tries to get a WidgetsetInfo in its setupMainDiv method, 
					// which calls the getWidgetsetInfo method of UIProvider,
					// which in turn tries to load the class "AppWidgetset". 
					// According to the comments in UIProvider.findWidgetsetClass(), a ClassNotFoundException is a normal case, 
					// so I will just throw this Exception in my custom ClassLoader whenever it is asked for this class.
					if("AppWidgetset".equals(name))
						throw new ClassNotFoundException("This is an allowed return value for AppWidgetset");
					try {
						try {
							val Class<?> loadClass = OSGiVaadinServlet.classLoader.loadClass(name)
							return loadClass
						} catch (ClassNotFoundException e) {
							return bundle.loadClass(name)
						}
					} catch (Exception e) {
						e.printStackTrace
						throw e
					}
				}
			})

			return service
		} catch (Exception e) {
			e.printStackTrace
			throw e
		}
	}
	
}
