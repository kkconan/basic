package com.money.game.basic.component.repayment.rate;

import java.math.BigDecimal;

public abstract interface YieldRate {

	public abstract BigDecimal dailyRate();

	public abstract BigDecimal monthRate();

}
