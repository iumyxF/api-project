package com.example.api.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * {@code @author:} iumyxF
 **/
@RestController
@RequestMapping("/image")
public class ImageUpload {

    @Value("${upload.filePath}")
    private String basePath;

    @PostMapping("/upload")
    public BaseResponse<String> upload(MultipartFile file) {
        //获取与原来文件名
        String filename = file.getOriginalFilename();
        //截取文件结尾格式
        String tail = null;
        if (filename != null) {
            tail = filename.substring(filename.lastIndexOf("."));
        }
        //使用uuid组装新文件名
        String random = UUID.randomUUID().toString();
        //将文件转存在设置的地址
        String newName = random + tail;
        //创建一个目录对象
        File dir = new File(basePath);
        //如果文件夹不存在则创建文件夹
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            file.transferTo(new File(basePath + newName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtils.success(newName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流读取对象
            FileInputStream inputStream = new FileInputStream(basePath + name);
            //输出流写对象
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            //输入流读取，输出流写入
            int len = 0;
            byte[] bytes = new byte[1024];
            //这里读取使用的while不是if
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes);
                outputStream.flush();
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
