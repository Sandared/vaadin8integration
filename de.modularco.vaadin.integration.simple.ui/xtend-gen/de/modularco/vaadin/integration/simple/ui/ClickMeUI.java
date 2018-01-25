package de.modularco.vaadin.integration.simple.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import de.modularco.vaadin.integration.simple.ui.MyView;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

@SuppressWarnings("serial")
@PushStateNavigation
@Component(service = UI.class, immediate = true, property = (HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/test"))
public class ClickMeUI extends UI {
  private final static long serialVersionUID = 1L;
  
  @Reference
  private List<MyView> views;
  
  @Override
  public void init(final VaadinRequest request) {
    final Label title = new Label("Menu");
    title.addStyleNames(ValoTheme.MENU_TITLE);
    final CssLayout menu = new CssLayout(title);
    menu.addStyleName(ValoTheme.MENU_ROOT);
    final Function1<MyView, Boolean> _function = (MyView it) -> {
      boolean _equals = "".equals(it.getPath());
      return Boolean.valueOf((!_equals));
    };
    final Consumer<MyView> _function_1 = (MyView it) -> {
      final MyView item = it;
      String _name = item.getName();
      final Button.ClickListener _function_2 = (Button.ClickEvent it_1) -> {
        this.getNavigator().navigateTo(item.getPath());
      };
      final Button view = new Button(_name, _function_2);
      view.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);
      menu.addComponent(view);
    };
    IterableExtensions.<MyView>filter(this.views, _function).forEach(_function_1);
    final CssLayout viewContainer = new CssLayout();
    final HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
    mainLayout.setSizeFull();
    this.setContent(mainLayout);
    final Navigator navigator = new Navigator(this, viewContainer);
    final Consumer<MyView> _function_2 = (MyView it) -> {
      navigator.addView(it.getPath(), it.getClass());
    };
    this.views.forEach(_function_2);
  }
}
