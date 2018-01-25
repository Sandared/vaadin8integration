package de.modularco.vaadin.integration.simple.ui

import com.vaadin.navigator.Navigator
import com.vaadin.navigator.PushStateNavigation
import com.vaadin.navigator.View
import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Button
import com.vaadin.ui.Composite
import com.vaadin.ui.CssLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import java.util.List
import org.osgi.service.component.annotations.Component
import org.osgi.service.component.annotations.Reference
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants

@SuppressWarnings("serial")
@PushStateNavigation
@Component(service=UI, immediate= true, property = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/test" )
public class ClickMeUI extends UI {
	private static final long serialVersionUID = 1L;
	
	@Reference
	List<MyView> views
	
	override init(VaadinRequest request) {
		
		val title = new Label("Menu")
		title.addStyleNames(ValoTheme.MENU_TITLE)
		
		val menu = new CssLayout(title);
		menu.addStyleName(ValoTheme.MENU_ROOT)
		
		views.filter[!''.equals(path)].forEach[
			val item = it
			val view = new Button(item.name, [navigator.navigateTo(item.path)])
			view.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM)
			menu.addComponent(view)
		]
		
		val viewContainer = new CssLayout
		
		val mainLayout = new HorizontalLayout(menu, viewContainer)
		mainLayout.setSizeFull
		content = mainLayout
		
		val navigator = new Navigator(this, viewContainer)
		views.forEach[
			navigator.addView(path, it.class)
		]
		
	}
	
}

interface MyView extends View{
	
	def String getName()
	def String getPath()
}

@Component(service = MyView)
class DefaultView extends Composite implements MyView{
	
	override getName() {
		'Default'
	}
	
	override getPath() {
		''
	}
	
	new (){
		compositionRoot = new Label('This is the default view')
	}
	
}

@Component(service = MyView)
class View1 extends Composite implements MyView{
	
	override getName() {
		'View 1'
	}
	
	override getPath() {
		'view1'
	}
	
	new (){
		compositionRoot = new Label('This is View 1 and I changed the content')
	}
	
}

@Component(service = MyView)
class View2 extends Composite implements MyView{
	
	override getName() {
		'View 2'
	}
	
	override getPath() {
		'view2'
	}
	
	new (){
		compositionRoot = new Label('This is View 2')
	}
	
}

@Component(service = MyView)
class View3 extends Composite implements MyView{
	
	override getName() {
		'View 3'
	}
	
	override getPath() {
		'view3'
	}
	
	new (){
		compositionRoot = new Label('This is View 3 and it is beautiful!')
	}
	
}
