package de.modularco.vaadin.integration.impl;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.server.UIProviderEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.UI;
import de.modularco.vaadin.integration.impl.Helper;
import de.modularco.vaadin.integration.impl.SortableUIEntry;
import de.modularco.vaadin.integration.impl.UIServiceListener;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@SuppressWarnings("all")
public class OSGiUIProvider extends UIProvider {
  /**
   * Returns the UI class that matches the path of the servlet of the request of the given event best
   */
  @Override
  public Class<? extends UI> getUIClass(final UIClassSelectionEvent event) {
    return this.getUIInternal(event).getClass();
  }
  
  /**
   * Creates an instance of the UI that matches the path of the servlet of the request of the given event best via ConfigurationAdmin. </br>
   * This operation is synchronous although ConfigurationAdmin operates async. </br>
   */
  @Override
  public UI createInstance(final UICreateEvent event) {
    try {
      final Class<? extends UI> clazz = this.getUIInternal(event).getClass();
      final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
      ConfigurationAdmin _service = context.<ConfigurationAdmin>getService(context.<ConfigurationAdmin>getServiceReference(ConfigurationAdmin.class));
      final ConfigurationAdmin cm = ((ConfigurationAdmin) _service);
      final Configuration configuration = cm.createFactoryConfiguration(clazz.getName(), "?");
      final String uniqueID = UUID.randomUUID().toString();
      Hashtable<String, Object> _hashtable = new Hashtable<String, Object>();
      final Procedure1<Hashtable<String, Object>> _function = (Hashtable<String, Object> it) -> {
        it.put(Helper.UNIQUE_ID_PROPERTY, uniqueID);
      };
      final Hashtable<String, Object> uiConfig = ObjectExtensions.<Hashtable<String, Object>>operator_doubleArrow(_hashtable, _function);
      final CountDownLatch latch = new CountDownLatch(1);
      final UIServiceListener listener = new UIServiceListener(latch);
      context.addServiceListener(listener, (((("(" + Helper.UNIQUE_ID_PROPERTY) + "=") + uniqueID) + ")"));
      configuration.update(uiConfig);
      try {
        latch.await(5, TimeUnit.SECONDS);
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception exception = (Exception)_t;
          InputOutput.<String>println("Could not create a new UI");
          throw exception;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      final UI createdUI = context.<UI>getService(listener.getUIReference());
      context.removeServiceListener(listener);
      final ClientConnector.DetachListener _function_1 = (ClientConnector.DetachEvent it) -> {
        try {
          configuration.delete();
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      createdUI.addDetachListener(_function_1);
      return createdUI;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private UI getUIInternal(final UIProviderEvent event) {
    try {
      final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
      final ServiceReference<?>[] uiRefs = context.getAllServiceReferences(UI.class.getName(), (("(!(" + Helper.UNIQUE_ID_PROPERTY) + "=*))"));
      VaadinRequest _request = event.getRequest();
      final VaadinServletRequest request = ((VaadinServletRequest) _request);
      final String urlPattern = request.getRequestURI();
      UI ui = null;
      try {
        final Function1<ServiceReference<?>, SortableUIEntry> _function = (ServiceReference<?> it) -> {
          Object _property = it.getProperty(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN);
          final String servletPattern = Helper.getVaadinUrlPattern(((String) _property));
          Object _service = context.getService(it);
          final UI entryUI = ((UI) _service);
          SortableUIEntry _sortableUIEntry = new SortableUIEntry();
          final Procedure1<SortableUIEntry> _function_1 = (SortableUIEntry it_1) -> {
            it_1.setKey(Helper.matchingPathSegments(urlPattern, servletPattern, "/"));
            it_1.setUi(entryUI);
          };
          return ObjectExtensions.<SortableUIEntry>operator_doubleArrow(_sortableUIEntry, _function_1);
        };
        final Function1<SortableUIEntry, Boolean> _function_1 = (SortableUIEntry it) -> {
          UI _ui = it.getUi();
          return Boolean.valueOf((_ui != null));
        };
        final Comparator<SortableUIEntry> _function_2 = (SortableUIEntry $0, SortableUIEntry $1) -> {
          int _key = $0.getKey();
          int _key_1 = $1.getKey();
          boolean _lessThan = (_key < _key_1);
          if (_lessThan) {
            return (-1);
          } else {
            int _key_2 = $0.getKey();
            int _key_3 = $1.getKey();
            boolean _equals = (_key_2 == _key_3);
            if (_equals) {
              return 0;
            } else {
              return 1;
            }
          }
        };
        ui = IterableExtensions.<SortableUIEntry>max(IterableExtensions.<SortableUIEntry>filter(ListExtensions.<ServiceReference<?>, SortableUIEntry>map(((List<ServiceReference<?>>)Conversions.doWrapArray(uiRefs)), _function), _function_1), _function_2).getUi();
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
          final Exception exception = (Exception)_t;
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("Could not find any matching UI for path: ");
          _builder.append(urlPattern);
          _builder.append(". Currently registered UI references: ");
          _builder.append(uiRefs);
          InputOutput.<String>println(_builder.toString());
          throw exception;
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
      return ui;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
