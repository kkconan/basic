package com.money.game.basic.component.sms;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ContentSms extends Sms {
	public String content;
}
