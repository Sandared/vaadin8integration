package de.modularco.vaadin.integration.impl;

import de.modularco.vaadin.integration.impl.Helper;
import de.modularco.vaadin.integration.impl.OSGiVaadinTracker;
import org.osgi.framework.BundleContext;

@SuppressWarnings("all")
public class OSGiVaadinResourceTracker extends OSGiVaadinTracker {
  public OSGiVaadinResourceTracker(final BundleContext context, final String header) {
    super(context, header);
  }
  
  @Override
  protected String getVaadinPath(final String path) {
    return Helper.getRootResourcePath(path);
  }
  
  @Override
  protected String getVaadinAlias(final String path) {
    return Helper.getRootResourceAlias(path, Helper.VAADIN_VERSION_PREFIX);
  }
}
