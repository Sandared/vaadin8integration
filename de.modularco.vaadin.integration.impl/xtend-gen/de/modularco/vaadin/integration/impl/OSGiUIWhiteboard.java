package de.modularco.vaadin.integration.impl;

import com.vaadin.server.Constants;
import com.vaadin.ui.UI;
import de.modularco.vaadin.integration.impl.Helper;
import de.modularco.vaadin.integration.impl.OSGiUIProvider;
import de.modularco.vaadin.integration.impl.OSGiVaadinServlet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.Servlet;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * Tracks UIs
 * For each UI Class a VaadinServlet is created.
 * As soon as there are no more instances of a given class this servlet will be removed
 */
@Component
@SuppressWarnings("all")
public class OSGiUIWhiteboard {
  private final ConcurrentHashMap<String, ServiceRegistration<Servlet>> servlets = new ConcurrentHashMap<String, ServiceRegistration<Servlet>>();
  
  private final HashMap<String, List<UI>> instances = new HashMap<String, List<UI>>();
  
  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target = (("(&(service.factoryPid=*)(!(" + Helper.UNIQUE_ID_PROPERTY) + "=*)))"))
  private void addFactoryUI(final UI ui, final Map<String, Object> uiConfig, final ServiceReference<UI> ref) {
    try {
      final Class<? extends UI> clazz = ui.getClass();
      final BundleContext context = ref.getBundle().getBundleContext();
      final Hashtable<String, Object> servletProps = new Hashtable<String, Object>();
      Object _get = uiConfig.get(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN);
      final String urlPattern = Helper.getVaadinUrlPattern(((String) _get));
      servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, urlPattern);
      servletProps.put((HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.PARAMETER_VAADIN_RESOURCES), "/vaadin-8.2.1");
      servletProps.put((HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL), Integer.valueOf(60));
      servletProps.put((HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX + Constants.SERVLET_PARAMETER_UI_PROVIDER), OSGiUIProvider.class.getName());
      Object _elvis = null;
      Object _get_1 = uiConfig.get(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED);
      if (_get_1 != null) {
        _elvis = _get_1;
      } else {
        _elvis = Boolean.valueOf(false);
      }
      servletProps.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_ASYNC_SUPPORTED, _elvis);
      Bundle _bundle = ref.getBundle();
      OSGiVaadinServlet _oSGiVaadinServlet = new OSGiVaadinServlet(_bundle);
      final ServiceRegistration<Servlet> servlet = context.<Servlet>registerService(Servlet.class, _oSGiVaadinServlet, servletProps);
      this.servlets.put(clazz.getName(), servlet);
      synchronized (this.instances) {
        final List<UI> previous = this.instances.putIfAbsent(clazz.getName(), CollectionLiterals.<UI>newArrayList(ui));
        if ((previous != null)) {
          previous.add(ui);
        }
      }
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exception = (Exception)_t;
        exception.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
  
  private void removeFactoryUI(final UI ui, final Map<String, Object> uiConfig) {
    final Class<? extends UI> clazz = ui.getClass();
    try {
      synchronized (this.instances) {
        final List<UI> uiInstances = this.instances.get(clazz.getName());
        if ((uiInstances != null)) {
          uiInstances.remove(ui);
          int _size = uiInstances.size();
          boolean _equals = (_size == 0);
          if (_equals) {
            final ServiceRegistration<Servlet> servlet = this.servlets.remove(clazz.getName());
            try {
              servlet.unregister();
            } catch (final Throwable _t) {
              if (_t instanceof IllegalStateException) {
                final IllegalStateException ise = (IllegalStateException)_t;
              } else {
                throw Exceptions.sneakyThrow(_t);
              }
            }
          }
        }
      }
    } catch (final Throwable _t) {
      if (_t instanceof Exception) {
        final Exception exception = (Exception)_t;
        exception.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
}
