package com.example.application.views.component;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;

public class ErrorNotification extends Notification{


	public ErrorNotification (String messagge) {
		super();
		Span error = new Span(messagge);
		error.getStyle().set("color", "red");
		this.add(error);
		this.setDuration(3000);
	}


}
