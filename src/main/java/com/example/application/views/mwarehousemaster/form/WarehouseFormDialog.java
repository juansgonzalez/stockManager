package com.example.application.views.mwarehousemaster.form;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.backend.model.Warehouse;
import com.example.application.backend.service.WarehouseService;
import com.example.application.views.enums.FORM_ACTION;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

public class WarehouseFormDialog extends Dialog{
	
	@Autowired
	private WarehouseService warehouseService;
	
	public FORM_ACTION action;
	
	private Warehouse warehouse = new Warehouse();
	
	private FormLayout warehouseData = new FormLayout();
	
	Binder<Warehouse> binder = new BeanValidationBinder<Warehouse>(Warehouse.class);

    private TextField warehouseName;
    private TextField warehouseAddress;
    private NumberField warehouseLatitude;
    private NumberField warehouseLongitude;
    
    public WarehouseFormDialog(Warehouse warehouse,WarehouseService warehouseService) {
		// Initialize the Dialog and set Properties
		super();
		action = FORM_ACTION.CANCEL;
		setCloseOnEsc(true);
		setCloseOnOutsideClick(false);

		// Initialized the service
		this.warehouseService = warehouseService;
		
		// Create the form
		createWarehouseEditor();
		
		// Initialize the binder
		createBinder();
		
		// Check if the warehouse in the constructor is null ( create new ) or no (update)
		if(warehouse!=null) {
			
			this.warehouse = warehouse;
			
			// Fill the field with the warehouse properties
			binder.readBean(warehouse);
		}
		
		// Create the Confirm buttons layout
    	HorizontalLayout confirmLayout = confirmButtons();
    	
    	// Add the form layout and the button layout to the Dialog
		 add(warehouseData,confirmLayout);
	}
    
    /**
	 * Method that create the layout of the action buttons save and cancel
	 * @return
	 */
	private HorizontalLayout confirmButtons() {
		// Initialize the layout and setting some properties
		HorizontalLayout botones = new HorizontalLayout();
    	botones.setWidthFull();
    	
    	// Create the cancel button with the click event
    	Button botonCancelar = new Button("Cancel", ClickEvent ->{
			// set the action of the form to save
    		setAction(FORM_ACTION.CANCEL);
			close();
    	});
    	
    	// Set some style to the cancel button
    	botonCancelar.getElement().getStyle().set("margin-right", "auto");
    	botonCancelar.getElement().getStyle().set("cursor", "pointer");
    	botonCancelar.getElement().getStyle().set("color", "red");
    	

    	// Create the Save button with the click event
    	Button botonAceptar = new Button("Save", ClickEvent ->{

    			// fill the Warehouse with the new data
				if(binder.writeBeanIfValid(warehouse)) {
					// set the action of the form to save
					setAction(FORM_ACTION.SAVE);
					
					// close the Dialog
					close();					
				}
				

    	});
    	
    	// Set some style to the cancel button
    	botonAceptar.getElement().getStyle().set("margin-left", "auto");
    	botonAceptar.getElement().getStyle().set("cursor", "pointer");
    	botonAceptar.getElement().getStyle().set("color", "green");

    	// Add the buttons to the layout
    	botones.add(botonCancelar);
    	botones.add(botonAceptar);
    	
		return botones;
	}
    
	/**
	 * Method that create the Warehouse form
	 */
	private void createWarehouseEditor() {
		
		/* Create the Name field */
        warehouseName = new TextField("Warehouse Name");
        warehouseName.setRequired(true);
        warehouseName.setRequiredIndicatorVisible(true);
        warehouseName.setId("name");
        setColspan(warehouseName, 1);
        binder.forField(warehouseName).asRequired("Name is required")
        	.bind(Warehouse::getName,Warehouse::setName);
        
		/* Create the description field */       
        warehouseAddress = new TextField("Warehouse Address");
        warehouseAddress.setId("address");
        setColspan(warehouseAddress, 4);
        binder.forField(warehouseAddress)
        	.bind(Warehouse::getAddress,Warehouse::setAddress);
        
        /* Create the latitude field */
        warehouseLatitude = new NumberField("Warehouse Latitude");
        warehouseLatitude.setId("latitude");
        warehouseLatitude.setRequiredIndicatorVisible(true);
        binder.forField(warehouseLatitude).asRequired("Latitude is required")
        	.bind(Warehouse::getLatitude,Warehouse::setLatitude);
        setColspan(warehouseLatitude, 1);
        
        /* Create the Price field */
        warehouseLongitude = new NumberField("Warehouse Longitude");
        warehouseLongitude.setId("longitude");
        warehouseLongitude.setRequiredIndicatorVisible(true);
        binder.forField(warehouseLongitude).asRequired("Longitude is required")
        	.bind(Warehouse::getLongitude,Warehouse::setLongitude);
        setColspan(warehouseLongitude, 1);

        // Create the form layout and setting the fields
        warehouseData = new FormLayout(warehouseName, warehouseLatitude, warehouseLongitude,warehouseAddress);
        warehouseData.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 3));

	}
	
	/**
	 * Set the col span from the fields into the form
	 * @param component component to set the col span
	 * @param colspan size of the col span
	 */
	private void setColspan(Component component, int colspan) {
		component.getElement().setAttribute("colspan", Integer.toString(colspan));
	}
	
	/**
	 * Method that initialize the binder
	 */
	private void createBinder() {
//	  binder.bind(warehouseName, Warehouse::getName, Warehouse::setName);
//	  binder.bind(warehouseDescription, Warehouse::getDescription, Warehouse::setDescription);
//	  binder.bind(warehouseFamily, Warehouse::getFamily, Warehouse::setFamily);
//	  binder.bind(warehousePrice, Warehouse::getPrice, Warehouse::setPrice);
//	  binder.bind(warehouseActive, Warehouse::isActive, Warehouse::setActive);
		binder.bindInstanceFields(this);
	}

    
	public FORM_ACTION getAction() {
		return action;
	}

	public void setAction(FORM_ACTION action) {
		this.action = action;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
}
