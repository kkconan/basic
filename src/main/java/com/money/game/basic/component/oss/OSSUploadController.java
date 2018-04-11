package com.money.game.basic.component.oss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.money.game.basic.component.exception.GHException;
import com.money.game.basic.component.ext.web.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping(value = "/common/file", produces = "application/json")
public class OSSUploadController extends BaseController {
	
	// 对公上传
	@RequestMapping(value = "yup", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<FileD> yup(HttpServletRequest request, 
            HttpServletResponse response, @RequestParam("file") MultipartFile file) {
			
		super.getLoginUser();
		
		if (file.isEmpty()) {
			throw GHException.getException("文件为空，上传失败！");
		}
		
		FileD fileD = OSSUploadUtil.uploadFile(file, false);

		return new ResponseEntity<FileD>(fileD, HttpStatus.OK);
	}
	
	// 对私上传
	@RequestMapping(value = "companyyup", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<FileD> companyYup(HttpServletRequest request, 
            HttpServletResponse response, @RequestParam("file") MultipartFile file) {
			
		super.getLoginUser();
		
		if (file.isEmpty()) {
			throw GHException.getException("文件为空，上传失败！");
		}
		
		FileD fileD = OSSUploadUtil.uploadFile(file, true);

		return new ResponseEntity<FileD>(fileD, HttpStatus.OK);
	}
	
	// 对私下载链接
	@RequestMapping(value = "companyyupload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<FileRep> companyYupload(@RequestParam(required = true) String url) {
		FileRep rep = new FileRep();	
		rep.setUrl(OSSUploadUtil.getIpaURl(url));

		return new ResponseEntity<FileRep>(rep, HttpStatus.OK);
	}
	
}
