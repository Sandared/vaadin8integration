package de.modularco.vaadin.integration.impl;

import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

@SuppressWarnings("all")
public class OSGiVaadinResourceHttpContext implements HttpContext {
  private String basePath;
  
  private Bundle bundle;
  
  public OSGiVaadinResourceHttpContext(final String basePath, final Bundle bundle) {
    this.basePath = basePath;
    this.bundle = bundle;
  }
  
  @Override
  public String getMimeType(final String name) {
    return null;
  }
  
  @Override
  public URL getResource(final String name) {
    return this.bundle.getResource((this.basePath + name));
  }
  
  @Override
  public boolean handleSecurity(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
    return true;
  }
}
