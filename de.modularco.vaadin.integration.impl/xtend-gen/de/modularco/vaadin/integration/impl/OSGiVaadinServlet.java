package de.modularco.vaadin.integration.impl;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.osgi.framework.Bundle;

@SuppressWarnings("all")
public class OSGiVaadinServlet extends VaadinServlet {
  private Bundle bundle;
  
  public OSGiVaadinServlet(final Bundle bundle) {
    this.bundle = bundle;
  }
  
  @Override
  protected VaadinServletService createServletService(final DeploymentConfiguration deploymentConfiguration) throws ServiceException {
    try {
      try {
        final VaadinServletService service = new VaadinServletService(this, deploymentConfiguration);
        service.init();
        service.setClassLoader(new ClassLoader() {
          @Override
          public Class<?> loadClass(final String name) throws ClassNotFoundException {
            try {
              boolean _equals = "AppWidgetset".equals(name);
              if (_equals) {
                throw new ClassNotFoundException("This is an allowed return value for AppWidgetset");
              }
              try {
                try {
                  final Class<?> loadClass = OSGiVaadinServlet.class.getClassLoader().loadClass(name);
                  return loadClass;
                } catch (final Throwable _t) {
                  if (_t instanceof ClassNotFoundException) {
                    final ClassNotFoundException e = (ClassNotFoundException)_t;
                    return OSGiVaadinServlet.this.bundle.loadClass(name);
                  } else {
                    throw Exceptions.sneakyThrow(_t);
                  }
                }
              } catch (final Throwable _t_1) {
                if (_t_1 instanceof Exception) {
                  final Exception e_1 = (Exception)_t_1;
                  e_1.printStackTrace();
                  throw e_1;
                } else {
                  throw Exceptions.sneakyThrow(_t_1);
                }
              }
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          }
        });
        return service;
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception e = (Exception)_t;
          e.printStackTrace();
          throw e;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
