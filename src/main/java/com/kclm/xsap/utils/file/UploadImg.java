package com.kclm.xsap.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author fangkai
 * @description
 * @create 2021-12-28 17:25
 */
@Slf4j
public class UploadImg {


    /**
     * 文件上传的方法
     * @param file 传入的文件流
     * @param fileUploadRelativeAddress 保存文件的相对位置
     * @return uuid修改的文件名
     */
    public static String uploadImg(MultipartFile file, String fileUploadRelativeAddress) {
        String projectDir = System.getProperty("user.dir");
        log.debug("\n==>文件上传中打印工程目录==>{}", projectDir);

        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        log.debug("\n==>上传图片的原始名字==>{}", originalFilename);
        //生成一个uuid作为文件的新名字
        String fileName = UUID.randomUUID().toString();

        //获取原始文件的扩展名
        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        log.debug("\n==>原始文件的缀名==>{}", suffix);
        //拼接文件新名字
        fileName += suffix;
        log.debug("\n==>上传图片的新名字==>{}", fileName);

        //io
        try {
            File realPath = new File(projectDir, fileUploadRelativeAddress);
            log.debug("\n==>文件的存放地址==>{}", realPath);
            if (!realPath.exists()) {
                log.debug("创建文件目录结构");
                realPath.mkdirs();
            }

            File fileFullPath = new File(realPath, fileName);
            file.transferTo(fileFullPath);
            log.debug("\n==>文件上传成功! ==>文件名：{};文件地址：==>{}", fileName, fileFullPath);
        } catch (IOException e) {
            log.error("头像上传失败！");
            throw new RuntimeException(e);
        }
        return fileName;
    }
}
