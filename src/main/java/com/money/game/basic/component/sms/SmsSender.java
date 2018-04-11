package com.money.game.basic.component.sms;

import com.money.game.basic.component.ext.web.BaseResp;

public interface SmsSender<T extends Sms> {
	public BaseResp send(T t);
}
