package com.money.game.basic.component.collectinfo;

import com.money.game.basic.component.ext.web.BaseResp;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;

import feign.Feign;

@Configuration
public class CollectInfoSdk {
	private static String host="http://demo.guohuaitech.com/";
	private static CollectInfoApi collectInfoApi=Feign.builder().target(CollectInfoApi.class, host);
	
	public static BaseResp collectInfo(String info){
		BaseResp rep=new BaseResp();
		try {
			rep = JSONObject.parseObject(collectInfoApi.collectInfo(info),BaseResp.class);
		} catch (Exception e) {
			rep.setErrorCode(-1);
		}
		return rep;
	}
}
