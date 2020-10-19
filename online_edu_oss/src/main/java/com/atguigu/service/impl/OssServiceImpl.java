package com.atguigu.service.impl;

import com.atguigu.oss.OssTemplate;
import com.atguigu.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {
    @Autowired
    private OssTemplate ossTemplate;

    @Override
    public String uploadFile(MultipartFile file) throws Exception {
        //得到上传文件的名称 2.jpg
        String originalFilename = file.getOriginalFilename();
        //8c61696c03ac-4765b40bf46529cf0513
        String suffix = originalFilename.substring(originalFilename.indexOf("."), originalFilename.length());
        String name = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName=name+suffix;
        //得到文件的流
        InputStream inputStream = file.getInputStream();
        String retUrl = ossTemplate.uploadFile(inputStream, fileName);
        return retUrl;
    }

    @Override
    public boolean deleteFile(String fileName) {
        ossTemplate.deleteSingleFile(fileName);
        return true;
    }


}
