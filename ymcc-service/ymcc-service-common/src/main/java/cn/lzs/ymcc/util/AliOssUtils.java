package cn.lzs.ymcc.util;


import cn.lzs.ymcc.properties.AliOssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 * 因为不属于controller层，也不属于service层，所以用component注解来存放到IOC容器里
 */
@Component
public class AliOssUtils {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    @Autowired
    public AliOssUtils(AliOssProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.bucketName = properties.getBucketName();
    }

    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile file) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 获取当前日期，格式化为 "yyyy/MM/dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();

        // 避免文件覆盖：生成唯一的文件名
        String fileName = currentDate + "/" + UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        // 文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        // 关闭 ossClient
        ossClient.shutdown();

        return url;  // 返回上传到OSS的路径
    }

}