package com.kclm.xsap.utils;

import java.io.File;
import java.util.UUID;

import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	/* ============一些全局设置=========== */
	private static final String RESOURCE_PATH = "static/img";
	
	
	/* ====================以下是公共方法区==================== */
	
	//==图像上传
	public static String uploadFiles(MultipartFile uploadFile) throws Exception{
		//定义文件名
		String fileName = ""; 
		//1.获取原始文件名 
		String uploadFileName = uploadFile.getOriginalFilename(); 
		//3.把文件加上随机数，防止文件重复 
		String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase(); 
		//4.判断是否输入了文件名 
		fileName = uuid + "_"+ uploadFileName;
		System.out.println(fileName);
		//2.获取文件路径
		String basePath = ResourceUtils.getURL("classpath:").getPath();
		System.out.println("basePath: "+basePath);
		//4.判断路径是否存在
		File realPath = new File(basePath,RESOURCE_PATH);
		System.out.println("filePath: "+realPath);
		if(!realPath.exists()) {
			System.out.println("创建目录结构。。。");
			realPath.mkdirs(); 
		} 
		//5.使用MulitpartFile接口中方法，把上传的文件写到指定位置 
		uploadFile.transferTo(new File(realPath,fileName)); 
		System.out.println("图片名："+fileName);
		return fileName;
	}
	
}
