package war;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import domain.Order;
import domain.Pizza;
import domain.User;

import javax.servlet.annotation.WebServlet;

import service.DBManager;
import service.Broadcaster;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
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

@Theme("mytheme")
@Widgetset("war.MyAppWidgetset")
@Push
@PreserveOnRefresh
public class MyUI extends UI implements Broadcaster.BroadcastListener {
	private static final long serialVersionUID = 1L;
	Navigator navigator;
	DBManager dbManager = new DBManager();
	Panel LeftPanel, RightPanel;
	VerticalLayout AdminContent = new VerticalLayout();
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
	    return bd.doubleValue();
	}
	
	// Home view with a menu
	public class HomeView extends VerticalLayout implements View {
		private static final long serialVersionUID = 1L;
		User loggedIn = new User();
		HorizontalLayout menu;
		Button orderbutton, ordersbutton, signupbutton, logoutbutton;
		Order UserOrder = new Order();
		Label OrderPriceLabel = new Label("Wartość zamówienia: " + UserOrder.getOrderPrice());
		Container ordercontainer = new IndexedContainer();
		Table ordertable = new Table();

		class ButtonListener implements Button.ClickListener {
			private static final long serialVersionUID = 1L;
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
		
		class OrderButtonListener implements Button.ClickListener {
			private static final long serialVersionUID = 1L;
			Pizza pizza = new Pizza();
			int index;
			public OrderButtonListener(Pizza pizza, int index) {
				//this.pizza = pizza;
				this.pizza.setName(pizza.getName());
				this.pizza.setIngredients(pizza.getName());
				this.pizza.setPrice40(pizza.getPrice40());
				this.pizza.setPrice50(pizza.getPrice50());
				this.pizza.setVer(pizza.getVer());
				this.index = index;
			}
			@Override
			public void buttonClick(ClickEvent event) {
				Integer newAmount = UserOrder.getOrder().get(index).getAmount() - 1;
				//Item itemId = ordercontainer.getItem(index);
				if (newAmount.equals(0)) {
					UserOrder.getOrder().remove(index);
					ordercontainer.removeAllItems();
					for (int x = 0; x < UserOrder.getOrder().size(); x++) {
						Item itemId = ordercontainer.addItem(x);
						itemId.getItemProperty("pizzaname").setValue(UserOrder.getOrder().get(x).getName());
						if (UserOrder.getOrder().get(x).getVer() == 1) itemId.getItemProperty("pizzaprice").setValue(pizza.getPrice40());
						else if (UserOrder.getOrder().get(x).getVer() == 2) itemId.getItemProperty("pizzaprice").setValue(pizza.getPrice50());
						itemId.getItemProperty("amount").setValue(new Button(UserOrder.getOrder().get(x).getAmount().toString(), new OrderButtonListener(UserOrder.getOrder().get(x), x)));
					}
					ordertable.setPageLength(ordercontainer.size());
				}
				else {
					UserOrder.getOrder().get(index).setAmount(newAmount);
					Item itemId = ordercontainer.getItem(index);
					itemId.getItemProperty("amount").setValue(new Button(newAmount.toString(), new OrderButtonListener(pizza, index)));
				}
				if (pizza.getVer() == 1) {
					UserOrder.setOrderPrice(round(UserOrder.getOrderPrice() - pizza.getPrice40(), 2));
				}
				else if (pizza.getVer() == 2) {
					UserOrder.setOrderPrice(round(UserOrder.getOrderPrice() - pizza.getPrice50(), 2));
				}
				OrderPriceLabel.setValue("Wartość zamówienia: " + UserOrder.getOrderPrice());
			}
		}
		
		class PriceButtonListener implements Button.ClickListener {
			private static final long serialVersionUID = 1L;
			Pizza pizza = new Pizza();
			int ver;
			public PriceButtonListener(Pizza pizza, int ver) {
				this.pizza.setName(pizza.getName());
				this.pizza.setIngredients(pizza.getName());
				this.pizza.setPrice40(pizza.getPrice40());
				this.pizza.setPrice50(pizza.getPrice50());
				this.pizza.setVer(ver);
				this.ver = ver;
			}
			@Override
			public void buttonClick(ClickEvent event) {
				int uos = ordercontainer.size();
				int i = 0;
				boolean name = false;
				boolean size = false;
				boolean inc = false;
				while (i < uos) {
					name = ordercontainer.getItem(i).getItemProperty("pizzaname").getValue().toString().compareTo(pizza.getName()) == 0;
					if (ver == 1) size = ordercontainer.getItem(i).getItemProperty("pizzaprice").getValue().toString().compareTo(pizza.getPrice40().toString()) == 0;
					else if (ver == 2) size = ordercontainer.getItem(i).getItemProperty("pizzaprice").getValue().toString().compareTo(pizza.getPrice50().toString()) == 0;
					if (name && size) {
						Integer newAmount = UserOrder.getOrder().get(i).getAmount() + 1;
						UserOrder.getOrder().get(i).setAmount(newAmount);
						Item itemId = ordercontainer.getItem(i);
						itemId.getItemProperty("amount").setValue(new Button(newAmount.toString(), new OrderButtonListener(pizza, i)));
						inc = true;
						i = uos;
					}
					i++;
				}
				if (!inc || uos == 0) {
					pizza.setVer(ver);
					UserOrder.getOrder().add(i, pizza);
					Item itemId = ordercontainer.addItem(i);
					itemId.getItemProperty("pizzaname").setValue(pizza.getName());
					if (ver == 1) itemId.getItemProperty("pizzaprice").setValue(pizza.getPrice40());
					else if (ver == 2) itemId.getItemProperty("pizzaprice").setValue(pizza.getPrice50());
					itemId.getItemProperty("amount").setValue(new Button("1", new OrderButtonListener(pizza, i)));
					ordertable.setPageLength(ordercontainer.size());
				}
				if (ver == 1) {
					UserOrder.setOrderPrice(round(UserOrder.getOrderPrice() + pizza.getPrice40(), 2));
				}
				else if (ver == 2) {
					UserOrder.setOrderPrice(round(UserOrder.getOrderPrice() + pizza.getPrice50(), 2));
				}
				OrderPriceLabel.setValue("Wartość zamówienia: " + UserOrder.getOrderPrice());
			}
		}
		
		class ExecuteOrderListener implements Button.ClickListener {
			
			@Override
			public void buttonClick(ClickEvent event) {
				Broadcaster.broadcast(UserOrder);
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
			
			// NOT LOGGED IN
			if (loggedIn.getLogin().isEmpty()) {
				Button loginbutton = new Button("Login", new ButtonListener("login"));
				signupbutton = new Button("Sign up", new ButtonListener("signup"));
				menu.addComponent(loginbutton);
				menu.addComponent(signupbutton);
				
				// ~LOGIN
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
						private static final long serialVersionUID = 1L;

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
									UserOrder.setOwner(loggedIn.get_id());
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
				// ~SIGNUP
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
						private static final long serialVersionUID = 1L;
						@Override
						public void buttonClick(ClickEvent event) {
							try {
								binder.commit();
								User newuser = new User();
								newuser.setLogin(binder.getField("login").getValue().toString());
								try {
									newuser = dbManager.findUser(newuser);
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
				// ~WRONG ADRESS
				else {navigator.navigateTo("/login");}
			}
			
			// LOGGED IN
			else if (!loggedIn.getLogin().isEmpty() && loggedIn.getUseable() == true) {
				try {
					loggedIn = dbManager.findUser(loggedIn);
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				UserOrder.setOwner(loggedIn.get_id());
				orderbutton = new Button("Order", new ButtonListener("order"));
				ordersbutton = new Button("Orders", new ButtonListener("orders"));
				logoutbutton = new Button("Logout", new ButtonListener("login", "logout"));
				menu.addComponent(orderbutton);
				menu.addComponent(logoutbutton);
				if (loggedIn.getType().compareTo("admin") == 0) {
					menu.addComponent(ordersbutton);
				}
				
				// ~ORDER
				if (event.getParameters().equals("order")) {
					orderbutton.setStyleName("active-button");
					ordersbutton.setStyleName("");
					
					ordertable.setSizeFull();
					ordercontainer.addContainerProperty("pizzaname", String.class, "none");
					ordercontainer.addContainerProperty("pizzaprice", Double.class, 0.00);
					ordercontainer.addContainerProperty("amount", Button.class, null);
					ordertable.setPageLength(ordercontainer.size());
					ordertable.setContainerDataSource(ordercontainer);
					ordertable.setColumnHeaders(new String[] { "Nazwa", "Cena / szt", "Ilość" });
					
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
						//UserOrder = dbManager.findOrder(UserOrder);
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					
					Item itemId;
					Button pricebutton1, pricebutton2;
					int i = 0;
					while (i < pizzas.size()) {
						itemId = pizzacontainer.addItem(i);
						itemId.getItemProperty("pizzaname").setValue(pizzas.get(i).getName());
						itemId.getItemProperty("pizzaing").setValue(pizzas.get(i).getIngredients());
						pricebutton1 = new Button(pizzas.get(i).getPrice40().toString(), new PriceButtonListener(pizzas.get(i), 1));
						itemId.getItemProperty("pizzaprice40").setValue(pricebutton1);
						pricebutton2 = new Button(pizzas.get(i).getPrice50().toString(), new PriceButtonListener(pizzas.get(i), 2));
						itemId.getItemProperty("pizzaprice50").setValue(pricebutton2);
						i++;
					}
					pizzatable.setPageLength(i);
					
					Button executeOrder = new Button("Zamów", new ExecuteOrderListener());
					
					pizzatable.setContainerDataSource(pizzacontainer);
					pizzatable.setColumnHeaders(new String[] { "Nazwa", "Składniki", "Cena 40cm", "Cena 50cm" });
					LeftPanelContent.addComponent(pizzatable);
					RightPanelContent.addComponent(ordertable);
					RightPanelContent.addComponent(OrderPriceLabel);
					RightPanelContent.addComponent(executeOrder);
					return;
				}
				// ~ORDERS
				else if (event.getParameters().equals("orders") && loggedIn.getType().compareTo("admin") == 0) {
					ordersbutton.setStyleName("active-button");
					orderbutton.setStyleName("");
					AdminContent.setSizeFull();
					LeftPanel.setContent(AdminContent);
					//LeftPanelContent.addComponent(new Label("orders."));
					return;
				}
				// ~WRONG ADRESS
				else {navigator.navigateTo("/order");}
			}
			// BANNED
			else if (loggedIn.getUseable() == false) {
				Notification.show("Your account has beed banned!", Notification.Type.ERROR_MESSAGE);
			}
		}
	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		//WrappedSession session = vaadinRequest.getWrappedSession();
		//HttpSession httpSession = ((WrappedHttpSession) vaadinRequest).getHttpSession();
		getPage().setTitle("Pizzeria Vaadin");
		navigator = new Navigator(this, this);
    	navigator.addView("", new HomeView());
    	new FeederThread().start();
    	Broadcaster.register(this);
   }
	
	@Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }
	
	@Override
	public void receiveBroadcast(final Order order) {
		access(new Runnable() {
            @Override
            public void run() {
            	Item itemId;
            	Table pizzatable = new Table();
				pizzatable.setSizeFull();
				Container orderscontainer = new IndexedContainer();
				orderscontainer.addContainerProperty("owner", String.class, "none");
				orderscontainer.addContainerProperty("pizzaname", String.class, "");
				orderscontainer.addContainerProperty("pizzasize", String.class, "");
				int i = 0;
				while (i < order.getOrder().size()) {
					itemId = orderscontainer.addItem(i);
					itemId.getItemProperty("owner").setValue(order.getOwner().toString());
					itemId.getItemProperty("pizzaname").setValue(order.getOrder().get(i).getName());
					if (order.getOrder().get(i).getVer() == 1) itemId.getItemProperty("pizzasize").setValue("40cm");
					else if (order.getOrder().get(i).getVer() == 2) itemId.getItemProperty("pizzasize").setValue("50cm");
					i++;
				}
				pizzatable.setPageLength(i);
				pizzatable.setContainerDataSource(orderscontainer);
				pizzatable.setColumnHeaders(new String[] { "Klient", "Pizza", "Rozmiar" });
				AdminContent.addComponent(pizzatable);
            }
        });
	}
	
	class FeederThread extends Thread {
	    int count = 0;
	    
	    @Override
	    public void run() {
	    	try {
	    		Thread.sleep(1000);
	    	} catch (InterruptedException e) {
                e.printStackTrace();
            }
	    	access(new Runnable() {
	            @Override
	            public void run() {}
            });
	    }
	}

   @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
   @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
   public static class MyUIServlet extends VaadinServlet {}
}
