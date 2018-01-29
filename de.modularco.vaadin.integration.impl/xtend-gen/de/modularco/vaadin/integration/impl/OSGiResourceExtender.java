package de.modularco.vaadin.integration.impl;

import de.modularco.vaadin.integration.impl.OSGiVaadinResourceTracker;
import de.modularco.vaadin.integration.impl.OSGiVaadinThemeTracker;
import de.modularco.vaadin.integration.impl.OSGiVaadinWidgetsetTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.BundleTracker;

@SuppressWarnings("all")
public class OSGiResourceExtender implements BundleActivator {
  private BundleTracker resTracker;
  
  private BundleTracker themeTracker;
  
  private BundleTracker widgetsetTracker;
  
  @Override
  public void start(final BundleContext context) throws Exception {
    OSGiVaadinResourceTracker _oSGiVaadinResourceTracker = new OSGiVaadinResourceTracker(context, "OSGiVaadin-Resource");
    this.resTracker = _oSGiVaadinResourceTracker;
    this.resTracker.open();
    OSGiVaadinThemeTracker _oSGiVaadinThemeTracker = new OSGiVaadinThemeTracker(context, "OSGiVaadin-Theme");
    this.themeTracker = _oSGiVaadinThemeTracker;
    this.themeTracker.open();
    OSGiVaadinWidgetsetTracker _oSGiVaadinWidgetsetTracker = new OSGiVaadinWidgetsetTracker(context, "OSGiVaadin-Widgetset");
    this.widgetsetTracker = _oSGiVaadinWidgetsetTracker;
    this.widgetsetTracker.open();
  }
  
  @Override
  public void stop(final BundleContext context) throws Exception {
    this.resTracker.close();
    this.resTracker = null;
    this.themeTracker.close();
    this.themeTracker = null;
    this.widgetsetTracker.close();
    this.widgetsetTracker = null;
  }
}
