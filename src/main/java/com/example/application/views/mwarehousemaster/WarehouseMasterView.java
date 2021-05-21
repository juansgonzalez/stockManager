package com.example.application.views.mwarehousemaster;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.backend.model.Warehouse;
import com.example.application.backend.service.WarehouseService;
import com.example.application.security.SecurityConfiguration;
import com.example.application.views.enums.FORM_ACTION;
import com.example.application.views.main.MainView;
import com.example.application.views.mwarehousemaster.form.WarehouseFormDialog;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog.OpenedChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * The warehouse
 */
@Route(value = "warehouse", layout = MainView.class)
@PageTitle("Warehouse Master")
public class WarehouseMasterView extends VerticalLayout{
	
	@Autowired
	private WarehouseService warehouseService;
	
	private Grid<Warehouse> grid;
	
	private Binder<Warehouse> binder = new BeanValidationBinder<Warehouse>(Warehouse.class);

	private Warehouse warehouseEditable = new Warehouse();

	private WarehouseFormDialog warehouseDialog;
	

    public WarehouseMasterView(WarehouseService warehouseService) {    	

    	// Initialize the services
    	this.warehouseService = warehouseService;
    	
    	// Add some view properties
    	this.setSizeFull();
    	this.setPadding(true);
    	
    	// Add the components to the view
    	add(createButtonPanel(), createGrid());
    }
    
    /**
     * Method that generate a grid with the DB warehouse
     * @return Return a Grid with all the columns and object for Warehouse crud
     */
	private Grid<Warehouse> createGrid() {
		grid = new Grid<>();
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
				GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
		
		grid.setDataProvider(new ListDataProvider<Warehouse>(warehouseService.findAll()));
		
		// Adding columns to the grid with the Product properties
//        grid.addColumn(c -> c.getId()).setHeader("Id").setWidth("70px").setFlexGrow(0).setSortable(false);
        grid.addColumn(c -> c.getName()).setHeader("Name").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getAddress()).setHeader("Addres").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getLatitude()).setHeader("Latitude").setWidth("150px").setFlexGrow(1).setSortable(true);
        grid.addColumn(c -> c.getLongitude()).setHeader("Longitude").setWidth("150px").setFlexGrow(0).setSortable(true);
        
        grid.addComponentColumn(c -> createGridButton(c)).setHeader("").setWidth("250px").setFlexGrow(0).setSortable(false).setVisible(SecurityConfiguration.isAdmin());
        
        
        return grid;
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
    	Button botonCrear = new Button("Create Warehouse", ClickEvent ->{
    		openWarehouseForm(null);
    	});
    	
    	// Set some Style to the create button
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
		grid.setDataProvider(new ListDataProvider<>(warehouseService.findAll()));
	}
	
	/**
	 * Method that open the Dialog where will be the Products properties
	 * @param item item that will be editated or NULL if will bew created a new one
	 */
	private void openWarehouseForm(Warehouse item) {
		//Initialize the product dialog
		warehouseDialog = new WarehouseFormDialog(item,warehouseService);
		
		warehouseDialog.open();
		
		// Set a component event to see when the dialog was closed
		warehouseDialog.addOpenedChangeListener(new ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>() {

			@Override
			public void onComponentEvent(OpenedChangeEvent<Dialog> event) {
				if(!event.isOpened()) { // Check if the form was closed
					if(FORM_ACTION.SAVE.equals(warehouseDialog.getAction())) { // Check if the form was closed with the save button
						// Get The Product with the new values
						warehouseEditable = warehouseDialog.getWarehouse();
						
						// Save in the DB 
						warehouseService.save(warehouseEditable);
						
						// Refresh the grid to display all the Warehouses
						refreshGrid();
					}
				}
				
			}
		});
	}
	
	/**
	 * Method that create the layout with 2 buttons for the action column for crud grid Warehouse
	 * @param item what we want do the actions
	 * @return Returns an horizontal layout with 2 buttons that will allow you update or remove the Warehouse 
	 */
	private HorizontalLayout createGridButton(Warehouse item) {
	    @SuppressWarnings("unchecked")
	    HorizontalLayout layout =  new HorizontalLayout();
	    layout.setWidthFull();
	    
	    // Create remove button with cick event acction
	    Button buttonRemove = new Button("Remove", clickEvent -> {
	        try {
	        	// Remove the Warehouse from the DB
	        	warehouseService.delete(item);
	        	
	        	// Refresh the grid list
	        	refreshGrid();
	        	
	        }catch(Exception e) {
	        	
	        }
	    });
	    
	    // Create update button with cick event acction
	    Button buttonUpdate = new Button("Update", clickEvent -> {
	    	// Open the Dialog with the Warehouse info form
	    	openWarehouseForm(item);
	    });
	    
	    // Set Style to the buttons
	    buttonRemove.getElement().getStyle().set("color", "red");
	    buttonUpdate.getElement().getStyle().set("color", "green");

	    // Add the 2 buttons to the horizontal grid
	    layout.add(buttonUpdate);
	    layout.add(buttonRemove);


	    return layout;
	}
	
}
