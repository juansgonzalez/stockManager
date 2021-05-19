package com.example.application.views.productmaster;

import java.util.List;
import java.util.Map;

import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;

import com.example.application.backend.model.Product;
import com.example.application.backend.model.Stock;
import com.example.application.backend.model.Product.Family;
import com.example.application.backend.service.ProductService;
import com.example.application.backend.service.WarehouseService;
import com.example.application.security.SecurityConfiguration;
import com.example.application.views.component.ErrorNotification;
import com.example.application.views.component.NonEditableCheckBox;
import com.example.application.views.enums.FORM_ACTION;
import com.example.application.views.main.MainView;
import com.example.application.views.productmaster.form.ProductFormDialog;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

@Route(value = "products", layout = MainView.class)
@PageTitle("Product Master")
public class ProductMasterView extends VerticalLayout{
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WarehouseService warehouseService;
	
	private Grid<Product> grid;
	
	private Binder<Product> binder = new BeanValidationBinder<Product>(Product.class);
	
	private Product productEditable = new Product();
	
	private ProductFormDialog productDialog;
	
	private String param;

	
	
    public ProductMasterView(ProductService productService,WarehouseService warehouseService) {    	

    	// Initialize the services
    	this.productService = productService;
    	this.warehouseService = warehouseService;

    	param = (String) UI.getCurrent().getSession().getAttribute("id");
    	UI.getCurrent().getSession().setAttribute("id",null);
    	
    	// Add some view properties
    	this.setSizeFull();
    	this.setPadding(true);
    	
    	// Add the components to the view
    	add(createButtonPanel(), createGrid());
    }
  

    /**
     * Method that generate a grid with the DB products
     * @return Return a Grid with all the columns and object for product crud
     */
	private Grid<Product> createGrid() {
		grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
				GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
		
		// Insert the product service to get the data from the DB
		if(param==null) {
			grid.setDataProvider(new ListDataProvider<Product>(productService.findAll()));						
		}else {
			grid.setDataProvider(new ListDataProvider<Product>(productService.obtenerByWarehouse(Integer.parseInt(param))));			
		}
		
		
		// Adding columns to the grid with the Product properties
//        grid.addColumn(c -> c.getId()).setHeader("Id").setWidth("70px").setFlexGrow(0).setSortable(false);
        grid.addColumn(c -> c.getWarehouse().getName()).setHeader("WareHouse").setWidth("150px").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getName()).setHeader("Name").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getDescription()).setHeader("Description").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getFamily()).setHeader("Familly").setWidth("150px").setFlexGrow(0).setSortable(true);
        grid.addColumn(c -> c.getPrice()).setHeader("Price").setFlexGrow(0).setSortable(true);
        
        grid.addComponentColumn(c -> new NonEditableCheckBox(c.isActive())).setHeader("Active").setFlexGrow(0).setSortable(false);
        
        grid.addComponentColumn(c -> createGridButton(c)).setHeader("").setWidth("250px").setFlexGrow(0).setSortable(false).setVisible(SecurityConfiguration.isAdmin());
        
        
        return grid;
	}
	
	/**
	 * Method that create the layout with 2 buttons for the action column for crud grid Product
	 * @param item what we want do the actions
	 * @return Returns an horizontal layout with 2 buttons that will allow you update or remove the product 
	 */
	private HorizontalLayout createGridButton(Product item) {
	    @SuppressWarnings("unchecked")
	    HorizontalLayout layout =  new HorizontalLayout();
	    layout.setWidthFull();
	    
	    // Create remove button with cick event acction
	    Button buttonRemove = new Button("Remove", clickEvent -> {
	        try {
	        	// Remove the product from the DB
	        	productService.delete(item);
	        	
	        	// Refresh the grid list
	        	refreshGrid();
	        	
	        }catch(Exception e) {
	        	// If wasn't possible remove the product will display a error message asking to the user to check the stock
	        	new ErrorNotification("It wasn't possible to remove the product "+item.getName()+ " be sure that the product hasn't stock").open();
	        }
	    });
	    
	    // Create update button with cick event acction
	    Button buttonUpdate = new Button("Update", clickEvent -> {
	    	// Open the Dialog with the Product info form
	    	openProductForm(item);
	    });
	    
	    // Set Style to the buttons
	    buttonRemove.getElement().getStyle().set("color", "red");
	    buttonUpdate.getElement().getStyle().set("color", "green");

	    // Add the 2 buttons to the horizontal grid
	    layout.add(buttonUpdate);
	    layout.add(buttonRemove);


	    return layout;
	}


	/**
	 * Method that create a horizontal layout where will be placed the buttons of refrech grid and the create a new product
	 * @return HorizontalLayout
	 */
	private HorizontalLayout createButtonPanel(){
		// Initialized the horiontalLayout
    	HorizontalLayout layout = new HorizontalLayout();
    	
    	// Set some properties to the layout
    	layout.setWidthFull();
    	
    	// Create the first button Update with the click event
    	
    	Button botonActualizar = new Button("Refresh List", ClickEvent->{
    		refreshGrid();
    	});
    	
    	// Set some style to the Update button
    	botonActualizar.getElement().getStyle().set("margin-left", "auto");
    	botonActualizar.getElement().getStyle().set("cursor", "pointer");

    	//Create the Create button with click event
    	Button botonCrear = new Button("Create Product", ClickEvent ->{
    		openProductForm(null);
    	});
    	
    	// Set some Styile to the create button
    	botonCrear.getElement().getStyle().set("margin-right", "auto");
    	botonCrear.getElement().getStyle().set("color", "green");
//    	botonCrear.getElement().getStyle().set("background-color", "green");
    	botonCrear.getElement().getStyle().set("cursor", "pointer");
    	botonCrear.setVisible(SecurityConfiguration.isAdmin());
    	
    	// Add the buttons to the layout
		layout.add(botonCrear);
    	layout.add(botonActualizar);
    	
    	return layout;
    }
	
	/**
	 * Method that refresh the list of the grid
	 */
	private void refreshGrid() {
		grid.setDataProvider(new ListDataProvider<>(productService.findAll()));
	}

	
	/**
	 * Method that open the Dialog where will be the Products properties
	 * @param item item that will be editated or NULL if will bew created a new one
	 */
	private void openProductForm(Product item) {
		//Initialize the product dialog
		productDialog = new ProductFormDialog(item,warehouseService);
		
		productDialog.open();
		
		// Set a component event to see when the dialog was closed
		productDialog.addOpenedChangeListener(new ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>() {

			@Override
			public void onComponentEvent(OpenedChangeEvent<Dialog> event) {
				if(!event.isOpened()) { // Check if the form was closed
					if(FORM_ACTION.SAVE.equals(productDialog.getAction())) { // Check if the form was closed with the save button
						// Get The Product with the new values
						productEditable = productDialog.getProducto();
						
						// Save in the DB 
						productService.save(productEditable);
						
						// Refresh the grid to display all the Products
						refreshGrid();
					}
				}
				
			}
		});
	}
}
