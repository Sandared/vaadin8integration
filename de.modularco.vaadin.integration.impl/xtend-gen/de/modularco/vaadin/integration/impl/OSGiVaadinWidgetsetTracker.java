package de.modularco.vaadin.integration.impl;

import de.modularco.vaadin.integration.impl.Helper;
import de.modularco.vaadin.integration.impl.OSGiVaadinTracker;
import org.osgi.framework.BundleContext;

@SuppressWarnings("all")
public class OSGiVaadinWidgetsetTracker extends OSGiVaadinTracker {
  public OSGiVaadinWidgetsetTracker(final BundleContext context, final String header) {
    super(context, header);
  }
  
  @Override
  protected String getVaadinPath(final String path) {
    return Helper.getWidgetsetPath(path);
  }
  
  @Override
  protected String getVaadinAlias(final String path) {
    return Helper.getWidgetsetAlias(path, Helper.VAADIN_VERSION_PREFIX);
  }
}
