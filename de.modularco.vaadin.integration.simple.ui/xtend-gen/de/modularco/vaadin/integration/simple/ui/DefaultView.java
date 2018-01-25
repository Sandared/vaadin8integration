package de.modularco.vaadin.integration.simple.ui;

import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import de.modularco.vaadin.integration.simple.ui.MyView;
import org.osgi.service.component.annotations.Component;

@Component(service = MyView.class)
@SuppressWarnings("all")
public class DefaultView extends Composite implements MyView {
  @Override
  public String getName() {
    return "Default";
  }
  
  @Override
  public String getPath() {
    return "";
  }
  
  public DefaultView() {
    Label _label = new Label("This is the default view");
    this.setCompositionRoot(_label);
  }
}
