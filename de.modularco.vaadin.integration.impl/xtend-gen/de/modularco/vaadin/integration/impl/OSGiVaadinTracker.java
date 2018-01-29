package de.modularco.vaadin.integration.impl;

import de.modularco.vaadin.integration.impl.OSGiVaadinResourceHttpContext;
import java.util.List;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.BundleTracker;

@SuppressWarnings("all")
public abstract class OSGiVaadinTracker extends BundleTracker {
  private String header;
  
  public OSGiVaadinTracker(final BundleContext context, final String header) {
    super(context, Bundle.ACTIVE, null);
    this.header = header;
  }
  
  @Override
  public Object addingBundle(final Bundle bundle, final BundleEvent event) {
    try {
      String _get = bundle.getHeaders().get(this.header);
      String resPath = ((String) _get);
      if ((resPath != null)) {
        final String[] resourceSegments = resPath.split("VAADIN/");
        int _size = ((List<String>)Conversions.doWrapArray(resourceSegments)).size();
        boolean _notEquals = (_size != 2);
        if (_notEquals) {
          throw new IllegalArgumentException("The OSGIVaadin-Resource must be a String that contains VAADIN/ exactly once!");
        }
        final String res = resourceSegments[1];
        final String alias = this.getVaadinAlias(res);
        final String path = this.getVaadinPath(res);
        final ServiceReference<?> httpRef = bundle.getBundleContext().getServiceReference(HttpService.class.getName());
        if ((httpRef == null)) {
          throw new IllegalStateException("OSGi runtime does not contain HttpService!");
        }
        Object _service = bundle.getBundleContext().getService(httpRef);
        final HttpService http = ((HttpService) _service);
        OSGiVaadinResourceHttpContext _oSGiVaadinResourceHttpContext = new OSGiVaadinResourceHttpContext(resPath, bundle);
        http.registerResources(alias, path, _oSGiVaadinResourceHttpContext);
      }
      return bundle;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Override
  public void removedBundle(final Bundle bundle, final BundleEvent event, final Object object) {
  }
  
  protected abstract String getVaadinPath(final String path);
  
  protected abstract String getVaadinAlias(final String path);
}
