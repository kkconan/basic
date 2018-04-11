package com.money.game.basic.component.ext.web;

import com.money.game.basic.component.exception.GHException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
	public class BaseResp {

	protected int errorCode;
	protected String errorMessage;

	public void error(Throwable error) {
		if (error instanceof GHException) {
			GHException e = (GHException) error;
			this.errorCode = e.getCode() == 0 ? -1 : e.getCode();
			this.errorMessage = e.getMessage();
			log.warn("error={}",error.getMessage(),error);
		} else if (error instanceof BindException) {
			BindException be = (BindException) error;
			String msg = be.getBindingResult().getAllErrors().get(0).getDefaultMessage();
			this.errorCode = -1;
			this.errorMessage = msg;
			log.warn("error={}",error.getMessage(),error);
		} else {
			this.errorCode = -1;
			this.errorMessage = error.getMessage();
			log.error("error={}",error.getMessage(),error);
		}
	}

}
