package com.zwl.controller;

import com.zwl.model.baseresult.Result;
import com.zwl.model.baseresult.ResultCodeEnum;
import com.zwl.util.AliOSSUtil;
import com.zwl.util.FileCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/image")
public class ImageController {
    /**
     * 本地存放目录
     */
//    private static String uoloadPath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "upload/";
    private static String uploadPath = "F:\\sass\\image\\";

    @PostMapping("/upload")
    public Result imageUpload(@RequestParam("file") MultipartFile file) {
        Result result = new Result();
        if (file.isEmpty()) {
            result.setCode(ResultCodeEnum.FAIL);
            return result;
        }
        try {
            // 上传文件信息
            log.info("OriginalFilename：" + file.getOriginalFilename());
            log.info("ContentType：" + file.getContentType());
            log.info("Name：" + file.getName());
            log.info("Size：" + file.getSize());
            //TODO:文件大小、名称、类型检查的业务处理
            if(file.getSize()>1024*1024){
                result.setCode(ResultCodeEnum.FAIL);
                result.setMessage("上传图片大小不能超过1M！");
                return result;
            }
           if(!FileCheckUtil.isImageType(file.getContentType())){
               result.setCode(ResultCodeEnum.FAIL);
               result.setMessage("请上传正确的图片类型！");
               return result;
           }

            // 检查上传目录
            File targetFile = new File(uploadPath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }

            // 实例化输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uploadPath + file.getOriginalFilename()));
            out.write(file.getBytes());
            out.flush();
            out.close();

            // 上传到OSS
            String url = AliOSSUtil.uploadLocalFile(new File(uploadPath + file.getOriginalFilename()), "upload/image/");
            if (url == null) {
                //TODO:上传失败的业务处理
                result.setCode(ResultCodeEnum.FAIL);
                return result;
            }
            log.info("上传完毕,访问地址:" + url);
            HashMap<String,Object> reslutMapData=new HashMap<>();
            reslutMapData.put("imageUrl", url);
            result.setData(reslutMapData);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传失败", e);
            return result;
        }
    }

}
