package com.kclm.xsap.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ImageUploadUtils {
    private ImageUploadUtils() {
    }
    public static String uploadMemberImg(MultipartFile avatarFile, String savePath){
        if(avatarFile!=null){
            String filename = avatarFile.getOriginalFilename();//获取文件名以及后缀名\
            if(filename==null) return null;
            String projectPath = System.getProperty("user.dir");
            String dirPath=projectPath+savePath;
            File filePath=new File(dirPath);
            if(!filePath.exists()) filePath.mkdirs();
            UUID uuid=UUID.randomUUID();//生成文件名
            String newFileName= uuid+filename.substring(filename.lastIndexOf("."));//生成文件名+后缀
            try {
                avatarFile.transferTo(new File(dirPath+newFileName));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return newFileName;
        }else {
            return null;
        }

    }

}
