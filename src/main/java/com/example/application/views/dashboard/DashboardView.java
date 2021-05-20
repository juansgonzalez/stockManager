package com.example.application.views.dashboard;

import com.vaadin.addon.leaflet4vaadin.LeafletMap;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.DefaultMapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.map.options.MapOptions;
import com.vaadin.addon.leaflet4vaadin.layer.ui.marker.Marker;
import com.vaadin.addon.leaflet4vaadin.types.Icon;
import com.vaadin.addon.leaflet4vaadin.types.LatLng;
import com.vaadin.addon.leaflet4vaadin.types.LatLngBounds;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.application.backend.model.Warehouse;
import com.example.application.backend.service.WarehouseService;
import com.example.application.views.main.MainView;
import com.example.application.views.productmaster.ProductMasterView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;

@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
public class DashboardView extends VerticalLayout {
	
	@Autowired
	private WarehouseService warehouseService;

    public DashboardView(WarehouseService warehouseService) {
    	setSizeFull();
    	setPadding(false);
    	List<Warehouse> listaWarehouse = warehouseService.findAll();
    	
    	this.warehouseService = warehouseService;
    	
    	MapOptions options = new DefaultMapOptions();
    	options.setZoom(7);
    	LeafletMap leafletMap = new LeafletMap(options);
    	leafletMap.setBaseUrl("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png");
    	List<LatLng> listaLats = new ArrayList<>();
    	for (Warehouse warehouse : listaWarehouse) {
    		Marker marker = new Marker();
    		marker.setIcon(Icon.DEFAULT_ICON);
    		LatLng lat = new LatLng(warehouse.getLatitude(), warehouse.getLongitude());
    		listaLats.add(lat);
    		marker.setLatLng(lat);
    		marker.setDraggable(true);
    		marker.setDraggable(false);
    		marker.bindTooltip(warehouse.getName());
    		marker.onClick((e) -> {
    			UI.getCurrent().getSession().setAttribute("id", warehouse.getId().toString());
    			UI.getCurrent().navigate("products");
    			
    		});
    		marker.addTo(leafletMap);			
		}
    	
    	leafletMap.fitBounds(new LatLngBounds(listaLats));
    	add(leafletMap);
	}

}
