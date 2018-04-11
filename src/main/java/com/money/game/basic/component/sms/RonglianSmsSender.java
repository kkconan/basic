package com.money.game.basic.component.sms;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.money.game.basic.common.StringUtil;
import com.money.game.basic.component.ext.web.BaseResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
/**
 * 容联短信通道
 * @author jeffrey
 *
 */
@Slf4j
@Service("ronglianSms")
public class RonglianSmsSender implements SmsSender<TemplateSms> {

	private static CCPRestSDK restAPI;
	
	// 容联
	@Value("${sms.ronglian.serverIP:#{null}}")
	private String serverIP;
	@Value("${sms.ronglian.serverPort:#{null}}")
	private String serverPort;
	@Value("${sms.ronglian.accountSid:#{null}}")
	private String accountSid;
	@Value("${sms.ronglian.accountToken:#{null}}")
	private String accountToken;
	@Value("${sms.ronglian.appId:#{null}}")
	private String appId;
	
//	@PostConstruct
	public void initRonglianSMS() {
		if (!StringUtil.isEmpty(this.serverIP) &&
				!StringUtil.isEmpty(this.serverPort) &&
				!StringUtil.isEmpty(this.accountSid) &&
				!StringUtil.isEmpty(this.accountToken) && 
				!StringUtil.isEmpty(this.appId)) {
			
			restAPI = new CCPRestSDK();
			restAPI.init(this.serverIP, this.serverPort);// 生产环境配置成app.cloopen.com，端口都是8883. 
			restAPI.setAccount(this.accountSid, this.accountToken);// 初始化主帐号名称和主帐号令牌
			restAPI.setAppId(this.appId);// 初始化应用ID
						
		} else {
			log.error("容联短信通道初始化数据失败！");
		}
	}
	
	@Override
	public BaseResp send(TemplateSms t) {
		BaseResp resp = new BaseResp();
		HashMap<String, Object> result = null;
		if (t.getPhone() == null || t.getTempId() == null) {
			resp.setErrorCode(-1);
			resp.setErrorMessage("无效的短信内容！");
			return resp;
		}
		result = restAPI.sendTemplateSMS(t.getPhone(), t.getTempId(), t.getValues());
		if (!"000000".equals(result.get("statusCode"))) {
			//异常返回输出错误码和错误信息
			log.error("用户：{}，短信发送失败，错误原因：{}", t.getPhone(), result.get("statusMsg") + "(" + result.get("statusCode") + ")");
			resp.setErrorCode(-1);
			resp.setErrorMessage(result.get("statusMsg") + "(" + result.get("statusCode") + ")");
			
		}
		log.debug("send sms " + t.getTempId() + " to " + t.getPhone());
		
		return resp;
	}

}
