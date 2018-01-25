package de.modularco.vaadin.integration.simple.ui;

import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import de.modularco.vaadin.integration.simple.ui.MyView;
import org.osgi.service.component.annotations.Component;

@Component(service = MyView.class)
@SuppressWarnings("all")
public class View3 extends Composite implements MyView {
  @Override
  public String getName() {
    return "View 3";
  }
  
  @Override
  public String getPath() {
    return "view3";
  }
  
  public View3() {
    Label _label = new Label("This is View 3 and it is beautiful!");
    this.setCompositionRoot(_label);
  }
}
