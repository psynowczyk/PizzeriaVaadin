package war;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domain.Pizza;
import domain.User;

import javax.servlet.annotation.WebServlet;

import org.bson.types.ObjectId;

import service.DBManager;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
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
@PreserveOnRefresh
public class MyUI extends UI {
	Navigator navigator;
	DBManager dbManager = new DBManager();
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	// Home view with a menu
	public class HomeView extends VerticalLayout implements View {
		User loggedIn = new User();
		Panel LeftPanel, RightPanel;
		HorizontalLayout menu;
		Button orderbutton, ordersbutton, signupbutton, logoutbutton;
		Double OrderPrice = new Double(0.00);
		Label OrderPriceLabel = new Label("Wartość zamówienia: " + OrderPrice);
		Container OrderContainer = new IndexedContainer();

		// Menu navigation button listener
		class ButtonListener implements Button.ClickListener {
			String menuitem;
			String action = "";
			public ButtonListener(String menuitem) {this.menuitem = menuitem;}
			public ButtonListener(String menuitem, String action) {
				this.menuitem = menuitem;
				this.action = action;
			}
			@Override
			public void buttonClick(ClickEvent event) {
				if (action.compareTo("logout") == 0) loggedIn = new User();
				navigator.navigateTo("/" + menuitem);
			}
		}
		class PriceButtonListener implements Button.ClickListener {
			Pizza pizza;
			int ver;
			public PriceButtonListener(Pizza pizza, int ver) {
				this.pizza = pizza;
				this.ver = ver;
			}
			@Override
			public void buttonClick(ClickEvent event) {
				if (ver == 1) {
					OrderPrice = round(OrderPrice + pizza.getPrice40(), 2);
				}
				else if (ver == 2) {
					OrderPrice = round(OrderPrice + pizza.getPrice50(), 2);
				}
				OrderPriceLabel.setValue("Wartość zamówienia: " + OrderPrice);
			}
		}
		public HomeView() {
			setSizeFull();
			VerticalLayout vLayout = new VerticalLayout();
			menu = new HorizontalLayout();
			HorizontalLayout panels = new HorizontalLayout();
			menu.setStyleName("main-menu");
			
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
			menu.removeAllComponents();
			VerticalLayout LeftPanelContent = new VerticalLayout();
			LeftPanelContent.setSizeFull();
			LeftPanel.setContent(LeftPanelContent); // Also clears
			VerticalLayout RightPanelContent = new VerticalLayout();
			RightPanelContent.setSizeFull();
			RightPanelContent.setMargin(true);
			RightPanel.setContent(RightPanelContent); // Also clears
			if (loggedIn.getLogin().isEmpty()) {
				Button loginbutton = new Button("Login", new ButtonListener("login"));
				signupbutton = new Button("Sign up", new ButtonListener("signup"));
				menu.addComponent(loginbutton);
				menu.addComponent(signupbutton);
				if (event.getParameters().equals("login")) {
					loginbutton.setStyleName("active-button");
					signupbutton.setStyleName("");
					final BeanItem<User> userItem = new BeanItem<User>(new User());
					final FormLayout form = new FormLayout();
					final FieldGroup binder = new FieldGroup(userItem);
					final Button saveBtn = new Button("Log in!");
					
					form.setStyleName("signup-form");
					form.addComponent(binder.buildAndBind("Login", "login"));
					form.addComponent(binder.buildAndBind("Password", "password"));
					form.addComponent(saveBtn);
					binder.setBuffered(true);
					binder.getField("login").setRequired(true);
					binder.getField("login").addValidator(new BeanValidator(User.class, "login"));
					binder.getField("password").setRequired(true);
					binder.getField("password").addValidator(new BeanValidator(User.class, "password"));
					LeftPanelContent.addComponent(form);
					RightPanelContent.addComponent(new Label("Witaj! Zaloguj się aby złożyć zamówienie."));
					
					saveBtn.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								binder.commit();
								User loginuser = new User();
								loginuser.setLogin(binder.getField("login").getValue().toString());
								loginuser.setPassword(binder.getField("password").getValue().toString());
								try {
									loginuser = dbManager.findUser(loginuser.getLogin(), loginuser.getPassword());
								} catch (UnknownHostException e) {
									e.printStackTrace();
								}
								if (!loginuser.getLogin().isEmpty()) {
									loggedIn = loginuser;
									navigator.navigateTo("/order");
								}
								else {
									Notification.show("Wrong login or password!", Notification.Type.WARNING_MESSAGE);
								}
							}
							catch (CommitException e) {
								Notification.show("Please correct errors before commiting", Notification.Type.WARNING_MESSAGE);
								e.printStackTrace();
							}
						}
					});
				}
				else if (event.getParameters().equals("signup")) {
					signupbutton.setStyleName("active-button");
					loginbutton.setStyleName("");
					final BeanItem<User> userItem = new BeanItem<User>(new User());
					final FormLayout form = new FormLayout();
					final FieldGroup binder = new FieldGroup(userItem);
					final Button saveBtn = new Button("Sign up!");
					
					form.setStyleName("signup-form");
					form.addComponent(binder.buildAndBind("Login", "login"));
					form.addComponent(binder.buildAndBind("Password", "password"));
					form.addComponent(binder.buildAndBind("Verify Password", "repassword"));
					form.addComponent(saveBtn);
					binder.setBuffered(true);
					binder.getField("login").setRequired(true);
					binder.getField("login").addValidator(new BeanValidator(User.class, "login"));
					binder.getField("password").setRequired(true);
					binder.getField("password").addValidator(new BeanValidator(User.class, "password"));
					binder.getField("repassword").setRequired(true);
					binder.getField("repassword").addValidator(new BeanValidator(User.class, "repassword"));
					LeftPanelContent.addComponent(form);
					saveBtn.addClickListener(new ClickListener() {
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								binder.commit();
								User newuser = new User();
								newuser.setLogin(binder.getField("login").getValue().toString());
								try {
									newuser = dbManager.findUser(newuser.getLogin());
								} catch (UnknownHostException e) {
									e.printStackTrace();
								}
								if (newuser.getLogin().isEmpty()) {
									newuser.setLogin(binder.getField("login").getValue().toString());
									newuser.setPassword(binder.getField("password").getValue().toString());
									newuser.setRepassword(binder.getField("repassword").getValue().toString());
									if (newuser.getPassword().compareTo(newuser.getRepassword()) == 0) {
										try {
											dbManager.insertUser(newuser);
											loggedIn = newuser;
											navigator.navigateTo("/order");
										} catch (UnknownHostException e) {
											e.printStackTrace();
										}
									}
									else {
										Notification.show("Password fields must match!", Notification.Type.WARNING_MESSAGE);
									}
								}
								else {
									Notification.show("Login already taken!", Notification.Type.WARNING_MESSAGE);
								}
								
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
			else if (!loggedIn.getLogin().isEmpty() && loggedIn.getUseable() == true) {
				try {
					loggedIn = dbManager.findUser(loggedIn.getLogin());
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				OrderContainer.addContainerProperty("pizzaname", String.class, "none");
				OrderContainer.addContainerProperty("pizzaprice", Button.class, null);
				orderbutton = new Button("Order", new ButtonListener("order"));
				ordersbutton = new Button("Orders", new ButtonListener("orders"));
				logoutbutton = new Button("Logout", new ButtonListener("login", "logout"));
				menu.addComponent(orderbutton);
				menu.addComponent(logoutbutton);
				if (loggedIn.getType().compareTo("admin") == 0) {
					menu.addComponent(ordersbutton);
				}
				if (event.getParameters().equals("order")) {
					orderbutton.setStyleName("active-button");
					ordersbutton.setStyleName("");
					
					Container pizzacontainer = new IndexedContainer();
					pizzacontainer.addContainerProperty("pizzaname", String.class, "none");
					pizzacontainer.addContainerProperty("pizzaing", String.class, "none");
					pizzacontainer.addContainerProperty("pizzaprice40", Button.class, null);
					pizzacontainer.addContainerProperty("pizzaprice50", Button.class, null);
					Table pizzatable = new Table();
					pizzatable.setSizeFull();
					
					List<Pizza> pizzas = new ArrayList<Pizza>();
					try {
						pizzas = dbManager.findPizzas();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					
					Item itemId;
					Button pricebutton;
					int i = 0;
					while (i < pizzas.size()) {
						itemId = pizzacontainer.addItem(i+1);
						itemId.getItemProperty("pizzaname").setValue(pizzas.get(i).getName());
						itemId.getItemProperty("pizzaing").setValue(pizzas.get(i).getIngredients());
						pricebutton = new Button(pizzas.get(i).getPrice40().toString(), new PriceButtonListener(pizzas.get(i), 1));
						itemId.getItemProperty("pizzaprice40").setValue(pricebutton);
						pricebutton = new Button(pizzas.get(i).getPrice50().toString(), new PriceButtonListener(pizzas.get(i), 2));
						itemId.getItemProperty("pizzaprice50").setValue(pricebutton);
						//cursor.next();
						i++;
					}
					pizzatable.setPageLength(i);
					
					pizzatable.setContainerDataSource(pizzacontainer);
					pizzatable.setColumnHeaders(new String[] { "Nazwa", "Składniki", "Cena 40cm", "Cena 50cm" });
					LeftPanelContent.addComponent(pizzatable);
					RightPanelContent.addComponent(OrderPriceLabel);
					return;
				}
				else if (event.getParameters().equals("orders") && loggedIn.getType().compareTo("admin") == 0) {
					ordersbutton.setStyleName("active-button");
					orderbutton.setStyleName("");
					LeftPanelContent.addComponent(new Label("orders."));
					return;
				}
				else {navigator.navigateTo("/order");}
			}
			else if (loggedIn.getUseable() == false) {
				Notification.show("Your account has beed banned!", Notification.Type.ERROR_MESSAGE);
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
