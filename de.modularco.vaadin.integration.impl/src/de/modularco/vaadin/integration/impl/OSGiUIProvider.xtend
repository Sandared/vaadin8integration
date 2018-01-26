package de.modularco.vaadin.integration.impl

import com.vaadin.server.UIClassSelectionEvent
import com.vaadin.server.UICreateEvent
import com.vaadin.server.UIProvider
import com.vaadin.server.UIProviderEvent
import com.vaadin.server.VaadinServletRequest
import com.vaadin.ui.UI
import java.util.Hashtable
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.eclipse.xtend.lib.annotations.Accessors
import org.osgi.framework.FrameworkUtil
import org.osgi.framework.ServiceEvent
import org.osgi.framework.ServiceListener
import org.osgi.framework.ServiceReference
import org.osgi.service.cm.ConfigurationAdmin
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants

import static extension de.modularco.vaadin.integration.impl.Helper.*

class OSGiUIProvider extends UIProvider{
	
	/**
	 * Returns the UI class that matches the path of the servlet of the request of the given event best
	 */
	override getUIClass(UIClassSelectionEvent event) {
		return getUIInternal(event).class
		//kasflkhdfökjshadfkjh
	}
	
	/**
	 * Creates an instance of the UI that matches the path of the servlet of the request of the given event best via ConfigurationAdmin. </br>
	 * This operation is synchronous although ConfigurationAdmin operates async. </br>
	 */
	override createInstance(UICreateEvent event) {
		
		// get the UI class
		val clazz = getUIInternal(event).class
			
		// get bundle context
		val context = FrameworkUtil.getBundle(class).bundleContext
		
		// get configuration admin
		val cm = context.getService(context.getServiceReference(ConfigurationAdmin)) as ConfigurationAdmin
		
		// create a new factory configuration for the given UI class
		val configuration = cm.createFactoryConfiguration(clazz.name, '?')
		
		val uniqueID = UUID.randomUUID.toString
		val uiConfig = new Hashtable<String, Object> => [put(UNIQUE_ID_PROPERTY, uniqueID)]
		
		// create a latch to sync on with OSGi Framework
		val latch = new CountDownLatch(1)
		
		// create and register a ServiceListener that listens to the UI service that we want to create (it only listens to services that have exactly the uniqueId)
		val listener = new UIServiceListener(latch)
		context.addServiceListener(listener, '(' + UNIQUE_ID_PROPERTY + '=' + uniqueID + ')')
		
		// command ConfigurationAdmin to create a new UI service
		configuration.update(uiConfig)
		
		// wait for OSGi to create the UI but abort after 5 seconds if nothing happens
		try {
			latch.await(5, TimeUnit.SECONDS)	
		} catch (Exception exception) {
			println('Could not create a new UI')
			throw exception
		}
		
		// retrieve the service reference from the listener and ask bundle context for the service
		val createdUI = context.getService(listener.UIReference)
		
		// remove the listener as it is not needed any more
		context.removeServiceListener(listener)
		
		// remove this instance after it is detached (server didn't receive heartbeat 3 times in a row, or site is refreshed)
		createdUI.addDetachListener[configuration.delete]
		
		return createdUI
	}
	
	private def getUIInternal(UIProviderEvent event){
		// get bundle context
		val context = FrameworkUtil.getBundle(class).bundleContext
		
		// get all UIs that have no uniqueId, because this means they are registered UIS and not instances generated for users
		val uiRefs = context.getAllServiceReferences(UI.name, '(!(' + UNIQUE_ID_PROPERTY + '=*))')
		
		// FIXME: we need the requestUri ... I don't know another way to retrieve this URI without casting the request to an implementation type???
		val request = event.request as VaadinServletRequest
		val urlPattern = request.requestURI
		
		var UI ui
		try {
			// try to retrieve the best fitting UI according to the requested servlet path
			ui = uiRefs.map[
				// get the path of the ui as it is registered in the servletcontainer
				val servletPattern = (getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN) as String).vaadinUrlPattern
				// get the according ui to the ui reference
				val entryUI = context.getService(it) as UI
				return new SortableUIEntry => [
					// compare the path of the ui with the requested path, the mor elemts of this path are the same, the higher the key gets
					it.key = urlPattern.matchingPathSegments(servletPattern, '/')
					it.ui = entryUI
				]
			].filter[
				// maybe some refs lead to a null service
				it.ui !== null
			].max[
				// get the service with the beest fitting path
				if($0.key < $1.key)
					return -1
				else{
					if($0.key == $1.key)
						return 0
					else{
						return 1
					}
				}
			].ui
		} catch (Exception exception) {
			println('''Could not find any matching UI for path: «urlPattern». Currently registered UI references: «uiRefs»''')		
			throw exception	
		}
		return ui
	}
}

class UIServiceListener implements ServiceListener{
	CountDownLatch latch
	ServiceReference<UI> ref
	
	new(CountDownLatch latch){
		this.latch = latch
	}
	
	override serviceChanged(ServiceEvent event) {
		if(ServiceEvent.REGISTERED.equals(event.type)){
			ref = event.serviceReference as ServiceReference<UI>
			latch.countDown
		}
	}
	
	def getUIReference(){
		return ref
	}
}

@Accessors
class SortableUIEntry{
	int key
	UI ui
}

