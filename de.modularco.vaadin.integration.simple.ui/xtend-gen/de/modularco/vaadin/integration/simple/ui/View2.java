package de.modularco.vaadin.integration.simple.ui;

import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;
import de.modularco.vaadin.integration.simple.ui.MyView;
import org.osgi.service.component.annotations.Component;

@Component(service = MyView.class)
@SuppressWarnings("all")
public class View2 extends Composite implements MyView {
  @Override
  public String getName() {
    return "View 2";
  }
  
  @Override
  public String getPath() {
    return "view2";
  }
  
  public View2() {
    Label _label = new Label("This is View 2");
    this.setCompositionRoot(_label);
  }
}
