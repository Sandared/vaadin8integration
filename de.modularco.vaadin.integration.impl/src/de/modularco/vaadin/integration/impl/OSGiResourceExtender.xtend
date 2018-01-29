package de.modularco.vaadin.integration.impl

import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.osgi.framework.Bundle
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext
import org.osgi.framework.BundleEvent
import org.osgi.service.http.HttpContext
import org.osgi.service.http.HttpService
import org.osgi.util.tracker.BundleTracker
import static extension de.modularco.vaadin.integration.impl.Helper.*

class OSGiResourceExtender implements BundleActivator{
	BundleTracker resTracker
	BundleTracker themeTracker
	BundleTracker widgetsetTracker
	
	override start(BundleContext context) throws Exception {
		resTracker = new OSGiVaadinResourceTracker(context, 'OSGiVaadin-Resource')
		resTracker.open
		themeTracker = new OSGiVaadinThemeTracker(context, 'OSGiVaadin-Theme')
		themeTracker.open
		widgetsetTracker = new OSGiVaadinWidgetsetTracker(context, 'OSGiVaadin-Widgetset')
		widgetsetTracker.open
	}
	
	override stop(BundleContext context) throws Exception {
		resTracker.close
		resTracker = null
		themeTracker.close
		themeTracker = null
		widgetsetTracker.close
		widgetsetTracker = null
	}
}

abstract class OSGiVaadinTracker extends BundleTracker{
	
	String header
	
	new(BundleContext context, String header) {
		super(context, Bundle.ACTIVE, null)
		this.header = header
	}
	
	override addingBundle(Bundle bundle, BundleEvent event) {
		var resPath = bundle.getHeaders().get(header) as String
		if(resPath !== null){
			// the path usually looks like /zzz/VAADIN/xxx, where xxx is the resource
			val resourceSegments = resPath.split('VAADIN/')
			if(resourceSegments.size != 2){
				throw new IllegalArgumentException('The OSGIVaadin-Resource must be a String that contains VAADIN/ exactly once!')
			}
			// we need the path after VAADIN
			val res = resourceSegments.get(1)
			
			
			// create Vaadin compliant alias and path
			val alias = getVaadinAlias(res)
        	val path = getVaadinPath(res)
        	
        	// get a HttpService from Framework
        	val httpRef = bundle.bundleContext.getServiceReference(HttpService.name)
        	if(httpRef === null){
        		throw new IllegalStateException('OSGi runtime does not contain HttpService!')
        	}
        	val http = bundle.bundleContext.getService(httpRef) as HttpService
			
			// register resource with specialized HttpContext        	
        	http.registerResources(alias, path, new OSGiVaadinResourceHttpContext(resPath, bundle))
		}
		return bundle
	}
	
	override removedBundle(Bundle bundle, BundleEvent event, Object object) {
		
	}
	
	protected abstract def String getVaadinPath(String path)
	protected abstract def String getVaadinAlias(String path)
}

class OSGiVaadinResourceTracker extends OSGiVaadinTracker{
	new(BundleContext context, String header) {
		super(context, header)
	}
	
	override protected getVaadinPath(String path) {
		getRootResourcePath(path);
	}
	
	override protected getVaadinAlias(String path) {
		getRootResourceAlias(path, VAADIN_VERSION_PREFIX);
	}
}

class OSGiVaadinThemeTracker extends OSGiVaadinTracker{
	new(BundleContext context, String header) {
		super(context, header)
	}
	
	override protected getVaadinPath(String path) {
		getThemePath(path);
	}
	
	override protected getVaadinAlias(String path) {
		getThemeAlias(path, VAADIN_VERSION_PREFIX);
	}
}

class OSGiVaadinWidgetsetTracker extends OSGiVaadinTracker{
	new(BundleContext context, String header) {
		super(context, header)
	}
	
	override protected getVaadinPath(String path) {
		getWidgetsetPath(path);
	}
	
	override protected getVaadinAlias(String path) {
		getWidgetsetAlias(path, VAADIN_VERSION_PREFIX);
	}
}

class OSGiVaadinResourceHttpContext implements HttpContext{
	String basePath
	Bundle bundle
	
	new(String basePath, Bundle bundle){
		this.basePath = basePath
		this.bundle = bundle
	}
	
	override getMimeType(String name) {
		return null
	}
	
	override getResource(String name) {
		return bundle.getResource(basePath + name)
	}
	
	override handleSecurity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		return true
	}
}
