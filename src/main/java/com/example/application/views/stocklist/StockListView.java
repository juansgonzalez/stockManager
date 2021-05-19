package com.example.application.views.stocklist;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.backend.model.Stock;
import com.example.application.backend.service.ProductService;
import com.example.application.backend.service.StockService;
import com.example.application.backend.service.WarehouseService;
import com.example.application.security.SecurityConfiguration;
import com.example.application.views.enums.FORM_ACTION;
import com.example.application.views.main.MainView;
import com.example.application.views.stocklist.form.StockFormDialog;

@Route(value = "stock", layout = MainView.class)
@PageTitle("Stock List")
public class StockListView extends VerticalLayout {

	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StockService stockService;
	
	Grid<Stock> grid;
	
	Binder<Stock> binder = new BeanValidationBinder<Stock>(Stock.class);
	
	Stock stockEditable = new Stock();
	
	StockFormDialog stockDialog;
	
    public StockListView(WarehouseService warehouseService,ProductService productService,StockService stockService) {
    	// Initialize the services
    	this.productService = productService;
    	this.warehouseService = warehouseService;
    	this.stockService = stockService;
    	
    	// Add some view properties
    	this.setSizeFull();
    	this.setPadding(true);
    	
    	// Add the components to the view
    	add(createButtonPanel(), createGrid());
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
    	Button botonCrear = new Button("Create Stock", ClickEvent ->{
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
     * Method that generate a grid with the DB products
     * @return Return a Grid with all the columns and object for product crud
     */
	private Grid<Stock> createGrid() {
		grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
				GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
		
		// Insert the product service to get the data from the DB
		grid.setDataProvider(new ListDataProvider<Stock>(stockService.findAll()));
		
		
		// Adding columns to the grid with the Product properties
//        grid.addColumn(c -> c.getId()).setHeader("Id").setWidth("70px").setFlexGrow(0).setSortable(false);
        grid.addColumn(c -> c.getWarehouse().getName()).setHeader("WareHouse").setWidth("150px").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getProduct().getName()).setHeader("Name").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getQuantity()).setHeader("Quantity").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getExpirationDate()).setHeader("Expidation Date").setWidth("150px").setFlexGrow(0).setSortable(true);
        grid.addColumn(c -> c.getLot()).setHeader("Lot").setFlexGrow(0).setSortable(true);
        grid.addColumn(c -> c.getSerialNumber()).setHeader("Serial Number").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getStatus()).setHeader("Status").setFlexGrow(0).setSortable(true);
       
        grid.addComponentColumn(c -> createGridButton(c)).setHeader("").setWidth("250px").setFlexGrow(0).setSortable(false).setVisible(SecurityConfiguration.isAdmin());

        
        return grid;
	}
	
	/**
	 * Method that create the layout with 2 buttons for the action column for crud grid Product
	 * @param item what we want do the actions
	 * @return Returns an horizontal layout with 2 buttons that will allow you update or remove the product 
	 */
	private HorizontalLayout createGridButton(Stock item) {
	    @SuppressWarnings("unchecked")
	    HorizontalLayout layout =  new HorizontalLayout();
	    layout.setWidthFull();
	    
	    // Create remove button with cick event acction
	    Button buttonRemove = new Button("Remove", clickEvent -> {
	        try {
	        	// Remove the product from the DB
	        	stockService.delete(item);
	        	
	        	// Refresh the grid list
	        	refreshGrid();
	        	
	        }catch(Exception e) {
	        	
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
	 * Method that open the Dialog where will be the Stock properties
	 * @param item item that will be editated or NULL if will bew created a new one
	 */
	private void openProductForm(Stock item) {
		//Initialize the product dialog
		stockDialog = new StockFormDialog(item,warehouseService,productService, stockService);
		
		stockDialog.open();
		
		// Set a component event to see when the dialog was closed
		stockDialog.addOpenedChangeListener(new ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>() {

			@Override
			public void onComponentEvent(OpenedChangeEvent<Dialog> event) {
				if(!event.isOpened()) { // Check if the form was closed
					if(FORM_ACTION.SAVE.equals(stockDialog.getAction())) { // Check if the form was closed with the save button
						// Get The Product with the new values
						stockEditable = stockDialog.getProducto();
						
						// Save in the DB 
						stockService.save(stockEditable);
						
						// Refresh the grid to display all the Products
						refreshGrid();
					}
				}
				
			}
		});
	}
	
	/**
	 * Method that refresh the list of the grid
	 */
	private void refreshGrid() {
		grid.setDataProvider(new ListDataProvider<>(stockService.findAll()));
	}
}
