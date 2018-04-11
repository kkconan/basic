package com.money.game.basic.component.ext.web;

import java.sql.Timestamp;

import com.money.game.basic.component.ext.web.parameter.CustomTimestampEditor;
import com.money.game.basic.component.ext.web.parameter.CustomerSqlDateEditor;
import com.money.game.basic.component.ext.web.parameter.CustomerTimeEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;

import com.money.game.basic.component.ext.web.parameter.CustomerJavaDateEditor;

@ControllerAdvice
public class GlobalBindingInitializer {

	@InitBinder
	public void registerCustomEditors(WebDataBinder binder, WebRequest request) {

		binder.registerCustomEditor(Timestamp.class, CustomTimestampEditor.getInstance());
		binder.registerCustomEditor(java.util.Date.class, CustomerJavaDateEditor.getInstance());
		binder.registerCustomEditor(java.sql.Date.class, CustomerSqlDateEditor.getInstance());
		binder.registerCustomEditor(java.sql.Time.class, CustomerTimeEditor.getInstance());
	}

}
