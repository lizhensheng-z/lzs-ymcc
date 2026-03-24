package cn.lzs.ymcc.web.controller;



import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.util.AliOssUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/oss")
public class OssController {
    //注入依赖：AliOSSUtiles工具类
    @Autowired
    private AliOssUtils aliOssUtils;

    @PostMapping("/uploadFile")
    public JSONResult uplaodFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传过来的参数：{}", file.getOriginalFilename());

        //调用阿里云OSS工具类【AliOSSUtiles】进行文件上传
        String url = aliOssUtils.upload(file);
        log.info("文件上传完成，url是：{}", url);

        return JSONResult.success(url);
    }
}


