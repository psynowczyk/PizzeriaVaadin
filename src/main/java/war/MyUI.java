package war;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 *
 */
@Theme("mytheme")
@Widgetset("war.MyAppWidgetset")
public class MyUI extends UI {
	
	Navigator navigator;
	
	// Home view with a menu
	public class HomeView extends VerticalLayout implements View {
		Panel LeftPanel;
		// Menu navigation button listener
		class ButtonListener implements Button.ClickListener {
			String menuitem;
			public ButtonListener(String menuitem) {
				this.menuitem = menuitem;
			}
			@Override
			public void buttonClick(ClickEvent event) {navigator.navigateTo("/" + menuitem);}
		}
		public HomeView() {
			setSizeFull();
			// Layout with menu on top and view area on below
			VerticalLayout vLayout = new VerticalLayout();
			HorizontalLayout menu = new HorizontalLayout();
			menu.setStyleName("main-menu");
			Button homebutton = new Button("Home", new ButtonListener("home"));
			homebutton.setStyleName("active-button");
			menu.addComponent(homebutton);
			menu.addComponent(new Button("Order", new ButtonListener("order")));
			menu.addComponent(new Button("Orders", new ButtonListener("orders")));
			vLayout.addComponent(menu);
			// A panel that contains a content area under menu
			LeftPanel = new Panel("");
			LeftPanel.setStyleName("left-panel");
			LeftPanel.setWidth("");
			vLayout.addComponent(LeftPanel);
			addComponent(vLayout);
		}
		
		@Override
		public void enter(ViewChangeEvent event) {
			VerticalLayout LeftPanelContent = new VerticalLayout();
			LeftPanelContent.setSizeFull();
			//LeftPanelContent.setMargin(true);
			LeftPanel.setContent(LeftPanelContent); // Also clears
			if (event.getParameters() == null || event.getParameters().isEmpty()) {
				LeftPanelContent.addComponent(new Label("Nothing to see here, just pass along."));
				return;
			}
			/*
			Label watching = new Label("You are currently watching a " + event.getParameters());
			watching.setSizeUndefined();
			LeftPanelContent.addComponent(watching);
			LeftPanelContent.setComponentAlignment(watching, Alignment.MIDDLE_CENTER);
			// Some other content
			Embedded pic = new Embedded(null, new ThemeResource("img/" + event.getParameters() + "-128px.png"));
			LeftPanelContent.addComponent(pic);
			LeftPanelContent.setExpandRatio(pic, 1.0f);
			LeftPanelContent.setComponentAlignment(pic, Alignment.MIDDLE_CENTER);
			Label back = new Label("And the " + event.getParameters() + " is watching you");
			back.setSizeUndefined();
			LeftPanelContent.addComponent(back);
			LeftPanelContent.setComponentAlignment(back, Alignment.MIDDLE_CENTER);
			*/
		}
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		getPage().setTitle("Pizzeria Vaadin");
		navigator = new Navigator(this, this);
    	navigator.addView("", new HomeView());

   }

   @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
   @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
   public static class MyUIServlet extends VaadinServlet {}
}
