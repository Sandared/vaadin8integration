package de.modularco.vaadin.integration.impl;

import com.vaadin.ui.UI;
import java.util.Hashtable;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * Every UI is registered as a normal service with no factoryPid or else. The UIProvider uses ConfigurationAdmin and the clazz name of each UI as factoryPid to create instances of each UI for requests.</br>
 * When a non-factory service is used in another place as factory service the original service will be deregistered. This Whiteboard does this as soon as a new UI is found, marked by not having a factoryPid set yet!
 */
@Component
@SuppressWarnings("all")
public class OSGiUIWhiteboardPreProcessor {
  /**
   * This ref needs the name aaa as it shall be set before any UI is set
   */
  @Reference(name = "aaa")
  private ConfigurationAdmin cm;
  
  @Reference(name = "bbb", cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, target = "(!(service.factoryPid=*))")
  private void addNonFactoryUI(final UI ui) {
    try {
      Configuration _createFactoryConfiguration = this.cm.createFactoryConfiguration(ui.getClass().getName(), "?");
      Hashtable<String, Object> _hashtable = new Hashtable<String, Object>();
      _createFactoryConfiguration.update(_hashtable);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void removeNonFactoryUI(final UI ui) {
  }
}
