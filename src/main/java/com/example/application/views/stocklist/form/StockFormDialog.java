package com.example.application.views.stocklist.form;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.example.application.backend.model.Product;
import com.example.application.backend.model.Product.Family;
import com.example.application.backend.model.Stock;
import com.example.application.backend.model.Stock.Status;
import com.example.application.backend.model.Warehouse;
import com.example.application.backend.service.ProductService;
import com.example.application.backend.service.StockService;
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
import com.vaadin.flow.component.datepicker.DatePicker;
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

public class StockFormDialog extends Dialog{
	
	@Autowired
	private WarehouseService warehouseService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private StockService stockService;
	
	public FORM_ACTION action;
	
	private Stock stock = new Stock();
	
	private FormLayout productData = new FormLayout();
	
	Binder<Stock> binder = new BeanValidationBinder<Stock>(Stock.class);

	
	private ComboBox<Product> stockProduct;
	private DatePicker stockExpirationDate;
    private TextField stockLot;
    private TextField stockSerialNumber;
    private ComboBox<Warehouse> stockWarehouse;
    private NumberField stockQuantity;
    private ComboBox<Status> stockStatus;
    
    
	
	public StockFormDialog(Stock stock,WarehouseService warehouseService, ProductService productService, StockService stockService) {
		// Initialize the Dialog and set Properties
		super();
		action = FORM_ACTION.CANCEL;
		setCloseOnEsc(true);
		setCloseOnOutsideClick(false);

		// Initialize the services
		this.productService = productService;
		this.warehouseService = warehouseService;
		this.stockService = stockService;
		
		// Create the form
		createProductEditor();
		
		// Initialize the binder
		createBinder();
		
		// Check if the product in the constructor is null ( create new ) or no (update)
		if(stock!=null) {
			
			this.stock = stock;
			
			// Fill the field with the product properties
			binder.readBean(stock);
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
				if(binder.writeBeanIfValid(stock)) {
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
		
		/* Create the Product field */
        stockProduct = new ComboBox<Product>();
        stockProduct.setId("product");
        stockProduct.setLabel("Product");
        stockProduct.setItemLabelGenerator(Product::getName);
        stockProduct.setItems(productService.findAll());
        stockProduct.setRequiredIndicatorVisible(true);
        setColspan(stockProduct, 1);
        binder.forField(stockProduct).asRequired("Product is required")
        	.bind(Stock::getProduct,Stock::setProduct);
        
        /* Create the ExpirationDate field */
        stockExpirationDate = new DatePicker();
        stockExpirationDate.setLabel("Expirtion Date");
        setColspan(stockExpirationDate, 1);
        binder.forField(stockExpirationDate)
        	.bind(Stock::getExpirationDate,Stock::setExpirationDate);
        
		/* Create the SerialNumber field */       
        stockSerialNumber = new TextField("Serial Number");
        stockSerialNumber.setId("serialNumber");
        setColspan(stockSerialNumber, 1);
        binder.forField(stockSerialNumber).withNullRepresentation("")
        	.bind(Stock::getSerialNumber,Stock::setSerialNumber);
        
        /* Create the lot field */       
        stockLot = new TextField("Lot");
        stockLot.setId("lot");
        setColspan(stockLot, 1);
        binder.forField(stockLot)
        	.bind(Stock::getLot,Stock::setLot);
        

		/* Create the warehouse field */
        //Get all the warehouse
        List<Warehouse> listaWarehouse = warehouseService.findAll();
        // Initialize the combobox and set some properties
        stockWarehouse = new ComboBox<Warehouse>();
        stockWarehouse.setId("warehouse");
        stockWarehouse.setLabel("Warehouse");
        stockWarehouse.setItemLabelGenerator(Warehouse::getName);
        // Set the itemList
        stockWarehouse.setItems(listaWarehouse);
        //Initialize the value getting the first item
        stockWarehouse.setValue(listaWarehouse.get(0));
        stockWarehouse.setRequiredIndicatorVisible(true);
        setColspan(stockWarehouse, 1);
        binder.forField(stockWarehouse).asRequired("Warehoyse is required")
        	.bind(Stock::getWarehouse,Stock::setWarehouse);
        
     
        /* Create the Quantity field */
        stockQuantity = new NumberField("Quantity");
        stockQuantity.setId("quantity");
        stockQuantity.setRequiredIndicatorVisible(true);
        binder.forField(stockQuantity).asRequired("Quantity is required")
        	.bind(Stock::getQuantity,Stock::setQuantity);
        setColspan(stockQuantity, 1);

        
        /* Create the Status field */
        // Initialize the combobox and set some properties
        stockStatus = new ComboBox<Status>();
        stockStatus.setId("status");
        stockStatus.setLabel("status");
        // Set the itemList
        stockStatus.setItems(Status.RESERVED,Status.STORED);
        //Initialize the value getting the first item
        stockStatus.setValue(Status.STORED);
        stockStatus.setRequiredIndicatorVisible(true);
        setColspan(stockStatus, 1);
        binder.forField(stockStatus).asRequired("Status is required")
        	.bind(Stock::getStatus,Stock::setStatus);

        // Create the form layout and setting the fields
        productData = new FormLayout(stockProduct, stockLot, stockSerialNumber,
        		stockQuantity,stockStatus, stockExpirationDate);
        productData.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));

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
//	  binder.bind(stockWarehouse, Stock::getWarehouse, Stock::setWarehouse);
//	  binder.bind(stockProduct, Stock::getProduct, Stock::setProduct);
//	  binder.bind(stockExpirationDate, Stock::getExpirationDate, Stock::setExpirationDate);
//	  binder.bind(stockLot, Stock::getLot, Stock::setLot);
//	  binder.bind(stockSerialNumber, Stock::getSerialNumber, Stock::setSerialNumber);
//	  binder.bind(stockQuantity, Stock::getQuantity, Stock::setQuantity);
//	  binder.bind(stockStatus, Stock::getStatus, Stock::setStatus);
		binder.bindInstanceFields(this);
	}

    
	public FORM_ACTION getAction() {
		return action;
	}

	public void setAction(FORM_ACTION action) {
		this.action = action;
	}

	public Stock getProducto() {
		return stock;
	}

	public void setProducto(Stock stock) {
		this.stock = stock;
	}
	
	
}
