package com.money.game.basic.component.oss;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.money.game.basic.common.Clock;
import com.money.game.basic.component.exception.GHException;
import com.money.game.basic.common.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云OSS文件仓库工具类
 * 
 * @author sunnyyao
 * 
 */
@Component
@Slf4j
public class OSSUploadUtil {

	// 对公
	private static String END_POINT;// 阿里云API的内或外网域名
	private static String ACCESS_KEYID;// 连接keyId
	private static String ACCESS_KEY_SECRET;// 秘钥
	private static String BUCKET_NAME;// 需要存储的bucketName
	private static String PIC_LOCATION;// 文件保存路径
	
	// 对私
	private static String PRI_END_POINT;// 阿里云API的内或外网域名
	private static String PRI_ACCESS_KEYID;// 连接keyId
	private static String PRI_ACCESS_KEY_SECRET;// 秘钥
	private static String PRI_BUCKET_NAME;// 需要存储的bucketName
	private static String PRI_PIC_LOCATION;// 文件保存路径
	
	private static Integer HOLD_DAYS;// 持有天数
	
	
	// 持有时间
	@Value("${oss.hold.days:3650}")
	public void setHOLD_DAYS(Integer v) {
		HOLD_DAYS = v;
	}

	// 对私，例如身份证、企业认证等
	@Value("${oss.end.point.private:oss-cn-shanghai.aliyuncs.com}")
	public void setPRI_END_POINT(String v) {
		PRI_END_POINT = v;
	}

	@Value("${oss.access.keyid.private:LTAIsJSQNbfvdLf1}")
	public  void setPRI_ACCESS_KEYID(String v) {
		PRI_ACCESS_KEYID = v;
	}

	@Value("${oss.access.proKey.secret.private:TXnGhl4RSM8I4fNq2RXwALH3ZouDMr}")
	public  void setPRI_ACCESS_KEY_SECRET(String v) {
		PRI_ACCESS_KEY_SECRET = v;
	}

	@Value("${oss.bucket.name.private:91ebkdev-private}")
	public  void setPRI_BUCKET_NAME(String v) {
		PRI_BUCKET_NAME = v;
	}

	@Value("${oss.filebox.name.private:yupload/}")
	public  void setPRI_PIC_LOCATION(String v) {
		PRI_PIC_LOCATION = v;
	}

	// 对公，web 图片,协议等
	@Value("${oss.end.point:oss-cn-shanghai.aliyuncs.com}")
	public void setEND_POINT(String v) {
		END_POINT = v;
	}
	
	@Value("${oss.access.keyid:LTAIsJSQNbfvdLf1}")
	public void setACCESS_KEYID(String v) {
		ACCESS_KEYID = v;
	}
	
	@Value("${oss.access.proKey.secret:TXnGhl4RSM8I4fNq2RXwALH3ZouDMr}")
	public void setACCESS_KEY_SECRET(String v) {
		ACCESS_KEY_SECRET = v;
	}
	
	@Value("${oss.bucket.name:91ebkdev-public}")
	public void setBUCKET_NAME(String v) {
		BUCKET_NAME = v;
	}
	
	@Value("${oss.filebox.name:yupload/}")
	public void setPIC_LOCATION(String v) {
		PIC_LOCATION = v;
	}
	
	/**
	 * 
	 * @MethodName: uploadFile
	 * @Description: OSS单文件上传
	 * @param file File
	 * @param fileNameD 存储在阿里云上的文件名称,如0001.png
	 * @return fileD FileD ,如果file.url==null，则上传失败
	 */
	public static FileD uploadFile(File file,String fileNameD) {
		String fileExte = file.getName().substring(file.getName().lastIndexOf(".")+1); // 文件扩展名
		String fileName = fileNameD==null?(UUID.randomUUID().toString().toUpperCase().replace("-", "") + "." + fileExte):fileNameD; // 文件名，根据UUID来
		String objKey = PIC_LOCATION + UUID.randomUUID().toString().toUpperCase().replace("-", "");
		String url = putObject(file, fileExte, objKey);
		FileD fileD = new FileD();
		fileD.setFileExte(fileExte);
		fileD.setRealname(fileName);
		fileD.setUrl(objKey);
		return fileD;
	}
	
	/**
	 * 
	 * @MethodName: replaceFile
	 * @Description: 替换文件:删除原文件并上传新文件，文件名和地址同时替换 解决原数据缓存问题，只要更新了地址，就能重新加载数据)
	 * @param file File
	 * @param fileNameD String 存储在阿里云上的文件名称,如0001.png
	 * @return fileD FileD
	 * @return objKey String PIC_LOCATION+fileNameD 例如：yupload/0001.png
	 */
	public static FileD uploadAndReplaceFile(File file, String fileNameD, String objKey) {
		boolean flag = deleteFile(objKey); // 先删除原文件
		if (!flag) {
			// 更改文件的过期时间，让他到期自动删除。
		}
		return uploadFile(file, fileNameD);
	}

	/**
	 * 
	 * @MethodName: deleteFile
	 * @Description: 单文件删除
	 * @param ojbKey 对象key
	 * @return boolean 是否删除成功
	 */
	public static boolean deleteFile(String ojbKey) {
		OSSClient ossClient = null;
		boolean flag = true;
		try {
			ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
			GenericRequest request = new DeleteObjectsRequest(BUCKET_NAME).withKey(ojbKey);
			ossClient.deleteObject(request);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		return flag;
	}

	/**
	 * 批量删除文件
	 * @Description: 批量文件删除(较快)：适用于相同endPoint和BucketName
	 * @param objKeys 需要删除的文件key集合
	 * @return int 成功删除的个数
	 */
	public static int deleteFiles(List<String> objKeys) {
		int deleteCount = 0; // 成功删除的个数
		OSSClient ossClient = null;
		try {
			ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
			DeleteObjectsRequest request = new DeleteObjectsRequest(BUCKET_NAME).withKeys(objKeys);
			DeleteObjectsResult result = ossClient.deleteObjects(request);
			deleteCount = result.getDeletedObjects().size();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		return deleteCount;

	}


	/**
	 * 
	 * @MethodName: putObject
	 * @Description: 上传文件
	 * @param file File
	 * @param fileExte 文件扩展名 
	 * @param objKey 文件KEY 
	 * @return String url
	 */
	private static String putObject(File file, String fileExte, String objKey) {
		String url = null; 
		OSSClient ossClient = null;
		try {
			ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
			InputStream input = new FileInputStream(file);
			ObjectMetadata meta = new ObjectMetadata(); // 创建上传Object的Metadata
			meta.setContentType(OSSUploadUtil.contentType(fileExte)); // 设置上传内容类型
			meta.setCacheControl("no-cache"); // 被下载时网页的缓存行为
			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, input, meta); // 创建上传请求
			ossClient.putObject(request);
			url = END_POINT.replaceFirst("http://", "http://" + BUCKET_NAME + ".") + "/" + objKey; // 上传成功再返回的文件路径
		} catch (Exception oe) {
			oe.printStackTrace();
		} finally {
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		return url;
	}
	
	
	/**
	 * 
	 * @MethodName: uploadFile
	 * @Description: OSS单文件上传
	 * @param inputStream InputStream
	 * @param fileName 存储在阿里云上的文件名称,如0001.png
	 * @param fileExte 文件扩展名 如：png、bmp。。。
	 * @return fileD FileD ,如果file.url==null，则上传失败
	 */
	public static FileD uploadFile(InputStream inputStream,String fileName,String fileExte) {
		OSSClient ossClient = null;
		FileD file = new FileD();
		try {
			ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
			ObjectMetadata meta = new ObjectMetadata(); // 创建上传Object的Metadata
			meta.setContentType(OSSUploadUtil.contentType(fileExte)); // 设置上传内容类型
			meta.setCacheControl("no-cache"); // 被下载时网页的缓存行为
			String objKey = PIC_LOCATION + StringUtil.uuid().toUpperCase() + "." + fileExte;
			file.setFileExte(fileExte);
			file.setRealname(fileName);
			file.setUrl(objKey);
			PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, objKey, inputStream, meta); // 创建上传请求
			ossClient.putObject(request);
		} catch (Throwable e) {
			log.error("文件上传失败：{}", e);
			throw GHException.getException("文件上传失败！");
		} finally {
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		return file;
	}
	
	/**
	 * 
	 * @MethodName: uploadFile
	 * @Description: OSS单文件上传：区分对公，还是对私
	 * @param inputStream InputStream
	 * @param fileName 存储在阿里云上的文件名称,如0001.png
	 * @param fileExte 文件扩展名 如：png、bmp。。。
	 * @param isPrivate 如果true，则是对私，否则对公
	 * @return fileD FileD ,如果file.url==null，则上传失败
	 */
	public static FileD uploadFile(InputStream inputStream,String fileName,String fileExte,Boolean isPrivate) {
		OSSClient ossClient = null;
		FileD file = new FileD();
		String objKey = null;
		PutObjectRequest request = null;
		try {
			file.setFileExte(fileExte);
			file.setRealname(fileName);
			ObjectMetadata meta = new ObjectMetadata(); // 创建上传Object的Metadata
			meta.setContentType(OSSUploadUtil.contentType(fileExte)); // 设置上传内容类型
			meta.setCacheControl("no-cache"); // 被下载时网页的缓存行为
			if(isPrivate){
				ossClient = new OSSClient(PRI_END_POINT, PRI_ACCESS_KEYID, PRI_ACCESS_KEY_SECRET);
				objKey = PRI_PIC_LOCATION + StringUtil.uuid().toUpperCase() + "." + fileExte;
				request = new PutObjectRequest(PRI_BUCKET_NAME, objKey, inputStream, meta); // 创建上传请求
			}else{
				ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
				objKey = PIC_LOCATION + StringUtil.uuid().toUpperCase() + "." + fileExte;
				request = new PutObjectRequest(BUCKET_NAME, objKey, inputStream, meta); // 创建上传请求
				objKey = "https://" + BUCKET_NAME + "." + END_POINT + "/" + objKey; // 上传成功再返回的文件路径
			}
			file.setUrl(objKey);
			ossClient.putObject(request);
		} catch (Throwable e) {
			log.error("文件上传失败：{}", e);
			throw GHException.getException("文件上传失败！");
		} finally {
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		return file;
	}
	
	// 文件上传
	public static FileD uploadFile(MultipartFile file, boolean isPrivate) {
		FileD fileD = new FileD();
		
		String filename = file.getOriginalFilename();
		
		String fileExte = filename.substring(filename.lastIndexOf(".") + 1 ); // 文件扩展名
       
		try {
			fileD = OSSUploadUtil.uploadFile(file.getInputStream(), filename, fileExte, isPrivate);
		} catch (Throwable e) {
			log.error("文件上传失败！", e);
			throw GHException.getException("文件上传失败！");
		}
		
		return fileD;
	}
	
	/**
	 * 
	 * @MethodName: contentType
	 * @Description: 获取文件类型
	 * @param fileExte String 文件扩展名，例如bmp、gif。。。
	 * @return String
	 */
	private static String contentType(String fileExte) {
		fileExte = fileExte.toLowerCase();
		String contentType = "";
		switch (fileExte) {
		case "bmp":
			contentType = "image/bmp";
			break;
		case "gif":
			contentType = "image/gif";
			break;
		case "png":
		case "jpeg":
		case "jpg":
			contentType = "image/jpeg";
			break;
		case "html":
			contentType = "text/html";
			break;
		case "txt":
			contentType = "text/plain";
			break;
		case "vsd":
			contentType = "application/vnd.visio";
			break;
		case "ppt":
		case "pptx":
			contentType = "application/vnd.ms-powerpoint";
			break;
		case "doc":
		case "docx":
			contentType = "application/msword";
			break;
		case "xml":
			contentType = "text/xml";
			break;
		case "mp4":
			contentType = "video/mp4";
			break;
		default:
			contentType = "application/octet-stream";
			break;
		}
		return contentType;
	}

		
	/**
	 * 
	 * @param objKey 对象或者文件的key
	 * @param localFilePath 本地文件路径
	 * @Description 下载到本地文件
	 */
	public static void downLoad(String objKey,String localFilePath){
		OSSClient ossClient = null;
		try{
			ossClient = new OSSClient(END_POINT, ACCESS_KEYID, ACCESS_KEY_SECRET);
			ossClient.getObject(new GetObjectRequest(BUCKET_NAME, objKey), new File(localFilePath));
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
		
	}
    
	/**
     * @Description:根据key获取oss服务器上的ipa文件地址（对私）
     * @param key
     * @return 
     * @ReturnType:String
     */
    public static String getIpaURl(String objKey){
    	java.net.URL url =null;
    	OSSClient ossClient =null;
    	try{
    		ossClient  = new OSSClient(PRI_END_POINT, PRI_ACCESS_KEYID, PRI_ACCESS_KEY_SECRET);
	        Date expires = new Date(new Date().getTime()+ HOLD_DAYS*24*3600*1000);
	        GeneratePresignedUrlRequest generatePresignedUrlRequest =new GeneratePresignedUrlRequest(PRI_BUCKET_NAME, objKey);
	        generatePresignedUrlRequest.setExpiration(expires);// 设置生成的URL的超时时间
	        log.info("开始获取文件下载地址：{}", new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
	        url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
	        log.info("结束获取文件下载地址：{}", new Timestamp(Clock.DEFAULT.getCurrentTimeInMillis()));
    	}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(ossClient!=null){
				ossClient.shutdown();
			}
		}
    	return url==null?null:url.toString().replace("http", "https");
    }
	
    
    
   
	/**
	 * 测试上传文件到阿里云
	 * @param args
	 */
/*	public static void main(String[] args) {
			
			FileD fileD = OSSUploadUtil.uploadFile(new File("D:\\test\\"+"互金管理系统业务整理V1.0.docx"),null);
			
			String url = OSSUploadUtil.getIpaURl(fileD.getObjKey());
			System.out.println("========================图片链接地址："+url);
			
			OSSUploadUtil.deleteFile(fileD.getObjKey());
		}*/
}