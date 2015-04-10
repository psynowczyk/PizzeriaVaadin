package war;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.UUID;

import domain.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.bson.Document;
import org.bson.types.ObjectId;

import service.DBManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.client.ui.Field;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.WrappedHttpSession;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
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
@PreserveOnRefresh
public class MyUI extends UI {
	Navigator navigator;
	DBManager userManager = new DBManager();
	
	// Home view with a menu
	public class HomeView extends VerticalLayout implements View {
		Boolean loggedIn = false;
		Panel LeftPanel, RightPanel;
		Button homebutton, orderbutton, ordersbutton, loginbutton, registerbutton;
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
			// Layout with menu on top and view area on below
			VerticalLayout vLayout = new VerticalLayout();
			HorizontalLayout menu = new HorizontalLayout();
			HorizontalLayout panels = new HorizontalLayout();
			menu.setStyleName("main-menu");
			if (!loggedIn) {		
				loginbutton = new Button("Login", new ButtonListener("login"));
				registerbutton = new Button("Register", new ButtonListener("register"));
				menu.addComponent(loginbutton);
				menu.addComponent(registerbutton);
			}
			else if (loggedIn) {
				OrderContainer.addContainerProperty("pizzaname", String.class, "none");
				OrderContainer.addContainerProperty("pizzaprice", Button.class, null);
				orderbutton = new Button("Order", new ButtonListener("order"));
				ordersbutton = new Button("Orders", new ButtonListener("orders"));
				menu.addComponent(orderbutton);
				menu.addComponent(ordersbutton);
			}
			vLayout.addComponent(menu);
			LeftPanel = new Panel("");
			LeftPanel.setStyleName("left-panel");
			LeftPanel.setWidth("");
			RightPanel = new Panel("");
			RightPanel.setStyleName("right-panel");
			RightPanel.setWidth("");
			panels.addComponent(LeftPanel);
			panels.addComponent(RightPanel);
			panels.setWidth("100%");
			panels.setExpandRatio(LeftPanel, 2);
			panels.setExpandRatio(RightPanel, 1);
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
			if (!loggedIn) {
				if (event.getParameters().equals("login")) {
					LeftPanelContent.addComponent(new Label("LOGIN."));
				}
				else if (event.getParameters().equals("register")) {
					final BeanItem<User> userItem = new BeanItem<User>(new User());
					final FormLayout form = new FormLayout();
					final FieldGroup binder = new FieldGroup(userItem);
					final Button saveBtn = new Button("Sign up!");
					
					//form.setStyleName("signup-form");
					form.addComponent(binder.buildAndBind("Login", "login"));
					form.addComponent(binder.buildAndBind("Password", "password"));
					binder.setBuffered(true);
					binder.getField("login").setRequired(true);
					binder.getField("login").addValidator(new BeanValidator(User.class, "login"));
					binder.getField("password").setRequired(true);
					binder.getField("password").addValidator(new BeanValidator(User.class, "password"));
					LeftPanelContent.addComponent(form);
					LeftPanelContent.addComponent(saveBtn);
					saveBtn.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								binder.commit();
								String newlogin = binder.getField("login").getValue().toString();
								User userexists = new User();
								try {
									userexists = userManager.findUser(newlogin);
								} catch (UnknownHostException e) {
									e.printStackTrace();
								}
								if (userexists.getLogin().isEmpty()) {
									userexists.set_id(new ObjectId());
									userexists.setLogin(binder.getField("login").getValue().toString());
									userexists.setPassword(binder.getField("password").getValue().toString());
									try {
										userManager.insertUser(userexists);
										Notification.show("Success!", Notification.Type.TRAY_NOTIFICATION);
									} catch (UnknownHostException e) {
										e.printStackTrace();
									}
								}
								else {
									Notification.show("Login already taken!", Notification.Type.WARNING_MESSAGE);
								}
								close();
							}
							catch (CommitException e) {
								Notification.show("Please correct errors before commiting", Notification.Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}
					});
				}
				else {navigator.navigateTo("/login");}
			}
			else if (loggedIn) {
				if (event.getParameters().equals("order")) {
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
				else {navigator.navigateTo("/order");}
			}
		}
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		//WrappedSession session = vaadinRequest.getWrappedSession();
	   // HttpSession httpSession = ((WrappedHttpSession) vaadinRequest).getHttpSession();
		getPage().setTitle("Pizzeria Vaadin");
		navigator = new Navigator(this, this);
    	navigator.addView("", new HomeView());
   }

   @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
   @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
   public static class MyUIServlet extends VaadinServlet {}
}
