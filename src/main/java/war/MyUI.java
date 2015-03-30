package war;

import java.math.BigDecimal;
import java.util.Currency;

import javax.servlet.annotation.WebServlet;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
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
import com.vaadin.ui.Table;
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
	
	MongoClient mongoClient = new MongoClient("localhost", 27017);
	MongoDatabase db = mongoClient.getDatabase("pv");
	Navigator navigator;
	
	// Home view with a menu
	public class HomeView extends VerticalLayout implements View {
		Panel LeftPanel, RightPanel;
		Button homebutton, orderbutton, ordersbutton;
		BigDecimal OrderPrice = new BigDecimal(0.00);
		Label OrderPriceLabel = new Label("Wartość zamówienia: " + OrderPrice);
		Container OrderContainer = new IndexedContainer();

		// Menu navigation button listener
		class ButtonListener implements Button.ClickListener {
			String menuitem;
			public ButtonListener(String menuitem) {this.menuitem = menuitem;}
			@Override
			public void buttonClick(ClickEvent event) {navigator.navigateTo("/" + menuitem);}
		}
		class PriceButtonListener implements Button.ClickListener {
			BigDecimal price;
			public PriceButtonListener(BigDecimal price) {this.price = price;}
			@Override
			public void buttonClick(ClickEvent event) {
				OrderPrice = OrderPrice.add(price).setScale(2, BigDecimal.ROUND_HALF_UP);
				OrderPriceLabel.setValue("Wartość zamówienia: " + OrderPrice);
			}
		}
		public HomeView() {
			setSizeFull();
			OrderContainer.addContainerProperty("pizzaname", String.class, "none");
			OrderContainer.addContainerProperty("pizzaprice", Button.class, null);
			// Layout with menu on top and view area on below
			VerticalLayout vLayout = new VerticalLayout();
			HorizontalLayout menu = new HorizontalLayout();
			HorizontalLayout panels = new HorizontalLayout();
			menu.setStyleName("main-menu");
			homebutton = new Button("Home", new ButtonListener("home"));
			orderbutton = new Button("Order", new ButtonListener("order"));
			ordersbutton = new Button("Orders", new ButtonListener("orders"));
			menu.addComponent(homebutton);
			menu.addComponent(orderbutton);
			menu.addComponent(ordersbutton);
			vLayout.addComponent(menu);
			// A panel that contains a content area under menu
			LeftPanel = new Panel("");
			LeftPanel.setStyleName("left-panel");
			LeftPanel.setWidth("");
			RightPanel = new Panel("");
			RightPanel.setStyleName("right-panel");
			RightPanel.setWidth("");
			panels.addComponent(LeftPanel);
			panels.addComponent(RightPanel);
			panels.setExpandRatio(LeftPanel, 2);
			panels.setExpandRatio(RightPanel, 1);
			panels.setWidth("100%");
			vLayout.addComponent(panels);
			addComponent(vLayout);
		}
		
		@Override
		public void enter(ViewChangeEvent event) {
			VerticalLayout LeftPanelContent = new VerticalLayout();
			LeftPanelContent.setSizeFull();
			LeftPanel.setContent(LeftPanelContent); // Also clears
			VerticalLayout RightPanelContent = new VerticalLayout();
			RightPanelContent.setSizeFull();
			RightPanel.setContent(RightPanelContent); // Also clears
			if (event.getParameters() == null || event.getParameters().isEmpty() || event.getParameters().equals("home")) {
				homebutton.setStyleName("active-button");
				orderbutton.setStyleName("");
				ordersbutton.setStyleName("");
				LeftPanelContent.addComponent(new Label("Projekt Pizzeria Vaadin - Bogaty interfejs użytkownika"));
				return;
			}
			else if (event.getParameters().equals("order")) {
				orderbutton.setStyleName("active-button");
				homebutton.setStyleName("");
				ordersbutton.setStyleName("");
				
				Container pizzacontainer = new IndexedContainer();
				pizzacontainer.addContainerProperty("pizzaname", String.class, "none");
				pizzacontainer.addContainerProperty("pizzaing", String.class, "none");
				pizzacontainer.addContainerProperty("pizzaprice1", Button.class, null);
				pizzacontainer.addContainerProperty("pizzaprice2", Button.class, null);
				Table pizzatable = new Table();
				pizzatable.setSizeFull();
				
				Item itemId = pizzacontainer.addItem("1");
				itemId.getItemProperty("pizzaname").setValue("Margherita");
				itemId.getItemProperty("pizzaing").setValue("Sos pomidorowy, Ser, Oregano");
				Button pricebutton = new Button("22.9", new PriceButtonListener(new BigDecimal(22.9)));
				itemId.getItemProperty("pizzaprice1").setValue(pricebutton);
				pricebutton = new Button("27.9", new PriceButtonListener(new BigDecimal(27.9)));
				itemId.getItemProperty("pizzaprice2").setValue(pricebutton);
				itemId = pizzacontainer.addItem("2");
				itemId.getItemProperty("pizzaname").setValue("Soprano");
				itemId.getItemProperty("pizzaing").setValue("Sos pomidorowy, Ser, Pieczarki");
				pricebutton = new Button("24.9", new PriceButtonListener(new BigDecimal(24.9)));
				itemId.getItemProperty("pizzaprice1").setValue(pricebutton);
				pricebutton = new Button("31.9", new PriceButtonListener(new BigDecimal(31.9)));
				itemId.getItemProperty("pizzaprice2").setValue(pricebutton);
				itemId = pizzacontainer.addItem("3");
				itemId.getItemProperty("pizzaname").setValue("Vesuvio");
				itemId.getItemProperty("pizzaing").setValue("Sos pomidorowy, Ser, Szynka");
				pricebutton = new Button("25.9", new PriceButtonListener(new BigDecimal(25.9)));
				itemId.getItemProperty("pizzaprice1").setValue(pricebutton);
				pricebutton = new Button("33.9", new PriceButtonListener(new BigDecimal(33.9)));
				itemId.getItemProperty("pizzaprice2").setValue(pricebutton);
				
				pizzatable.setContainerDataSource(pizzacontainer);
				pizzatable.setColumnHeaders(new String[] { "Nazwa", "Składniki", "Cena 40cm", "Cena 50cm" });
				pizzatable.setPageLength(3);
				LeftPanelContent.addComponent(pizzatable);
				RightPanelContent.addComponent(OrderPriceLabel);
				return;
			}
			else if (event.getParameters().equals("orders")) {
				ordersbutton.setStyleName("active-button");
				homebutton.setStyleName("");
				orderbutton.setStyleName("");
				LeftPanelContent.addComponent(new Label("orders."));
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
