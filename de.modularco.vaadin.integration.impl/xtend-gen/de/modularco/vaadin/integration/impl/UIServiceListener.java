package de.modularco.vaadin.integration.impl;

import com.vaadin.ui.UI;
import java.util.concurrent.CountDownLatch;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("all")
public class UIServiceListener implements ServiceListener {
  private CountDownLatch latch;
  
  private ServiceReference<UI> ref;
  
  public UIServiceListener(final CountDownLatch latch) {
    this.latch = latch;
  }
  
  @Override
  public void serviceChanged(final ServiceEvent event) {
    boolean _equals = Integer.valueOf(ServiceEvent.REGISTERED).equals(Integer.valueOf(event.getType()));
    if (_equals) {
      ServiceReference<?> _serviceReference = event.getServiceReference();
      this.ref = ((ServiceReference<UI>) _serviceReference);
      this.latch.countDown();
    }
  }
  
  public ServiceReference<UI> getUIReference() {
    return this.ref;
  }
}
