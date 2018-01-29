package de.modularco.vaadin.integration.impl;

import de.modularco.vaadin.integration.impl.Helper;
import de.modularco.vaadin.integration.impl.OSGiVaadinTracker;
import org.osgi.framework.BundleContext;

@SuppressWarnings("all")
public class OSGiVaadinThemeTracker extends OSGiVaadinTracker {
  public OSGiVaadinThemeTracker(final BundleContext context, final String header) {
    super(context, header);
  }
  
  @Override
  protected String getVaadinPath(final String path) {
    return Helper.getThemePath(path);
  }
  
  @Override
  protected String getVaadinAlias(final String path) {
    return Helper.getThemeAlias(path, Helper.VAADIN_VERSION_PREFIX);
  }
}
