package de.modularco.vaadin.integration.impl

import com.vaadin.server.Constants
import com.vaadin.ui.UI
import java.util.HashMap
import java.util.Hashtable
import java.util.List
import java.util.Map
import java.util.concurrent.ConcurrentHashMap
import javax.servlet.Servlet
import org.osgi.framework.ServiceReference
import org.osgi.framework.ServiceRegistration
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.component.annotations.ReferenceCardinality
import org.osgi.service.component.annotations.ReferencePolicy
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants

import static extension de.modularco.vaadin.integration.impl.Helper.*

/**
 * Tracks UIs
 * For each UI Class a VaadinServlet is created.
 * As soon as there are no more instances of a given class this servlet will be removed
 */
@Component
class OSGiUIWhiteboard{

	val servlets = new ConcurrentHashMap<String, ServiceRegistration<Servlet>>
	val instances = new HashMap<String, List<UI>>
	
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target='(&(service.factoryPid=*)(!(' + UNIQUE_ID_PROPERTY + '=*)))')
	private def void addFactoryUI(UI ui, Map<String, Object> uiConfig, ServiceReference<UI> ref) {
		try {
			val clazz = ui.class
			val context = ref.bundle.bundleContext
			val servletProps = new Hashtable
			val urlPattern = getVaadinUrlPattern(uiConfig.get(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN) as String)
			
			servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, urlPattern)
			// hardcoded but the official Vaadin solution is even uglier, this sets the path where vaadin searches for resources
			servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.PARAMETER_VAADIN_RESOURCES, "/vaadin-8.2.1")
			// Vaadin UIs that are open on the client side send a regular heartbeat to the server to indicate they are still alive, even though there is no ongoing user interaction. The server closes an UI after three missed heartbeats.
			servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL, 60)
			// set our UI Provider to be used by Vaadin
			servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.SERVLET_PARAMETER_UI_PROVIDER, OSGiUIProvider.name)
			servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, uiConfig.get(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED) ?: false)
			// TODO: Listen to other possbile properties of Vaadin
			
			val servlet = context.registerService(Servlet, new OSGiVaadinServlet(ref.bundle), servletProps);
			servlets.put(clazz.name, servlet);
			synchronized (instances) {
				val previous = instances.putIfAbsent(clazz.name, newArrayList(ui))
				if (previous !== null){
					previous.add(ui)
				}
			}
			
		} catch (Exception exception) {
			exception.printStackTrace	
		}
	}

	private def void removeFactoryUI(UI ui, Map<String, Object> uiConfig) {
		val clazz = ui.class
		try {
			synchronized (instances) {
				val uiInstances = instances.get(clazz.name)
				if(uiInstances !== null){
					uiInstances.remove(ui)
					if(uiInstances.size == 0){
						val servlet = servlets.remove(clazz.name)
						try {
			                servlet.unregister();
			            } catch (IllegalStateException ise) {
			                // This service may have already been unregistered
			                // automatically by the OSGi framework if the
			                // application bundle is being stopped. This is
			                // obviously not a problem for us.
			            }					
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace
		}
	}
}

/**
 * Every UI is registered as a normal service with no factoryPid or else. The UIProvider uses ConfigurationAdmin and the clazz name of each UI as factoryPid to create instances of each UI for requests.</br>
 * When a non-factory service is used in another place as factory service the original service will be deregistered. This Whiteboard does this as soon as a new UI is found, marked by not having a factoryPid set yet!
 */
@Component
class OSGiUIWhiteboardPreProcessor{
	
	/**
	 * This ref needs the name aaa as it shall be set before any UI is set
	 */
	@Reference(name='aaa')
	ConfigurationAdmin cm
	
	@Reference(name='bbb', cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target='(!(service.factoryPid=*))')
	private def void addNonFactoryUI(UI ui){
		// This makes the non factory component a factory component and leads to deregistering the old non-factory component
		cm.createFactoryConfiguration(ui.class.name, '?').update(new Hashtable)
	}
	
	private def void removeNonFactoryUI(UI ui){}
}

