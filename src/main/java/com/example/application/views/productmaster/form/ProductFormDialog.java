package com.example.application.views.productmaster.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.example.application.backend.model.Product;
import com.example.application.backend.model.Product.Family;
import com.example.application.backend.model.Warehouse;
import com.example.application.backend.service.WarehouseService;
import com.example.application.views.component.ErrorNotification;
import com.example.application.views.enums.FORM_ACTION;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;

public class ProductFormDialog extends Dialog{
	
	@Autowired
	private WarehouseService warehouseService;
	
	public FORM_ACTION action;
	
	private Product producto = new Product();
	
	private FormLayout productData = new FormLayout();
	
	Binder<Product> binder = new BeanValidationBinder<Product>(Product.class);

	
	
    private TextField productName;
    private TextField productDescription;
    private ComboBox<Family> productFamily;
    private ComboBox<Warehouse> productWarehouse;
    private NumberField productPrice;
    private Checkbox productActive;
    
    
	
	public ProductFormDialog(Product product,WarehouseService warehouseService) {
		// Initialize the Dialog and set Properties
		super();
		action = FORM_ACTION.CANCEL;
		setCloseOnEsc(true);
		setCloseOnOutsideClick(false);

		// Initialized the service
		this.warehouseService = warehouseService;
		
		// Create the form
		createProductEditor();
		
		// Initialize the binder
		createBinder();
		
		// Check if the product in the constructor is null ( create new ) or no (update)
		if(product!=null) {
			
			this.producto = product;
			
			// Fill the field with the product properties
			binder.readBean(producto);
		}
		
		// Create the Confirm buttons layout
    	HorizontalLayout confirmLayout = confirmButtons();
    	
    	// Add the form layout and the button layout to the Dialog
		 add(productData,confirmLayout);
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

    			// fill the product with the new data
				if(binder.writeBeanIfValid(producto)) {
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
	 * Method that create the Product form
	 */
	private void createProductEditor() {
		
		/* Create the Name field */
        productName = new TextField("Product Name");
        productName.setRequired(true);
        productName.setRequiredIndicatorVisible(true);
        productName.setId("name");
        setColspan(productName, 2);
        binder.forField(productName).asRequired("Name is required")
        	.bind(Product::getName,Product::setName);
        
		/* Create the description field */       
        productDescription = new TextField("Product Description");
        productDescription.setId("description");
        setColspan(productDescription, 4);
        binder.forField(productDescription)
        	.bind(Product::getDescription,Product::setDescription);
        
		/* Create the familly field */
        productFamily = new ComboBox<Family>();
        productFamily.setId("familly");
        productFamily.setLabel("Familly");
        productFamily.setItems(Family.ELECTRONICS,Family.FASHION,Family.PERISHABLE);
        productFamily.setValue(Family.ELECTRONICS);
        productFamily.setRequiredIndicatorVisible(true);
        setColspan(productFamily, 1);
        binder.forField(productFamily).asRequired("Family is required")
        	.bind(Product::getFamily,Product::setFamily);

		/* Create the warehouse field */
        //Get all the warehouse
        List<Warehouse> listaWarehouse = warehouseService.findAll();
        // Initialize the combobox and set some properties
        productWarehouse = new ComboBox<Warehouse>();
        productWarehouse.setId("warehouse");
        productWarehouse.setLabel("Warehouse");
        productWarehouse.setItemLabelGenerator(Warehouse::getName);
        // Set the itemList
        productWarehouse.setItems(listaWarehouse);
        //Initialize the value getting the first item
        productWarehouse.setValue(listaWarehouse.get(0));
        productWarehouse.setRequiredIndicatorVisible(true);
        setColspan(productWarehouse, 1);
        binder.forField(productWarehouse).asRequired("Warehoyse is required")
        	.bind(Product::getWarehouse,Product::setWarehouse);
        
        /* Create the Price field */
        productPrice = new NumberField("Product Price");
        productPrice.setId("price");
        productPrice.setPrefixComponent(new Icon(VaadinIcon.EURO));
        productPrice.setRequiredIndicatorVisible(true);
        binder.forField(productPrice).asRequired("price is required")
        	.bind(Product::getPrice,Product::setPrice);
        setColspan(productPrice, 1);

        /* Create the active field */
        productActive = new Checkbox("Product Active");
        productActive.setId("active");
        productActive.setLabel("Active");
        productActive.setValue(true);
        setColspan(productPrice, 1);
        binder.forField(productActive)
        	.bind(Product::isActive,Product::setActive);

        // Create the form layout and setting the fields
        productData = new FormLayout(productName, productDescription, productFamily,
        		productWarehouse,productPrice, productActive);
        productData.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 4));

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
//	  binder.bind(productName, Product::getName, Product::setName);
//	  binder.bind(productDescription, Product::getDescription, Product::setDescription);
//	  binder.bind(productFamily, Product::getFamily, Product::setFamily);
//	  binder.bind(productPrice, Product::getPrice, Product::setPrice);
//	  binder.bind(productActive, Product::isActive, Product::setActive);
  binder.bindInstanceFields(this);
	}

    
	public FORM_ACTION getAction() {
		return action;
	}

	public void setAction(FORM_ACTION action) {
		this.action = action;
	}

	public Product getProducto() {
		return producto;
	}

	public void setProducto(Product producto) {
		this.producto = producto;
	}
	
	
}
