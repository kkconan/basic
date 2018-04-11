package com.money.game.basic.component.oss;


import com.money.game.basic.component.ext.web.BaseResp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileD extends BaseResp {
	/**
	 * 文件名称
	 */
	private String realname;
	
	/**
	 * 文件后缀/扩展名
	 */
	private String fileExte;
//	
//	/**
//	 * 对象key
//	 */
//	private String objKey;
	
	/**
	 * request url(对公是直接能访问的url,对私取值来源于key)
	 */
	private String url;
	
	


}
