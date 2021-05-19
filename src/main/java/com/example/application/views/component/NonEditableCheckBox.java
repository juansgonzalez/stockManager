package com.example.application.views.component;

import com.vaadin.flow.component.checkbox.Checkbox;

public class NonEditableCheckBox extends Checkbox{
	
	public NonEditableCheckBox(boolean state) {
		super(state);
		
		addValueChangeListener(new ValueChangeListener<ValueChangeEvent<Boolean>>() {

			@Override
			public void valueChanged(ValueChangeEvent<Boolean> event) {
				setValue(state);
				
			}
		}); 

	}

}
