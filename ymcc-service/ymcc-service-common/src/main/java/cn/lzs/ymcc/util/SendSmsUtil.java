package cn.lzs.ymcc.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 短信发送工具类（模拟发送，不真实调用阿里云短信服务）
 */
public class SendSmsUtil {

    /**
     * 模拟发送短信验证码（不真实发送，只打印日志）
     * @param phone 手机号
     * @param code 验证码
     */
    public static void sendMessageCode(String phone, String code) {
        // 模拟发送成功，打印验证码
        System.out.println("【模拟短信】验证码已发送，手机号: " + phone + ", 验证码: " + code);
    }


    /**
     * 工具类方法不要抛异常
     * @param phone
     * @param code
     * @throws Exception
     */
//    public static void sendMessageCode(String phone,String code){
//        //TODO 只能发送给自己的手机号做测试，没有企业资质，不能个人认证
//        phone = "18186114194";
//
//        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
//                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
//
//
//                .build());
//        // Configure the Client
//        AsyncClient client = AsyncClient.builder()
//                .region("cn-qingdao") // Region ID
//                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
//                .credentialsProvider(provider)
//                //.serviceConfiguration(Configuration.create()) // Service-level configuration
//                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
//                .overrideConfiguration(
//                        ClientOverrideConfiguration.create()
//                        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
////                                .setEndpointOverride("dysmsapi.aliyuncs.com")
//                        //.setConnectTimeout(Duration.ofSeconds(30))
//                )
//                .build();
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("code",code);
//        String jsonString  = jsonObject.toJSONString();
//        // Parameter settings for API request
//        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
//                .signName("阿里云短信测试-IT云课堂项目")
//                .templateCode("SMS_321086730")
//                .phoneNumbers(phone)
//                .templateParam(jsonString)
//                // Request-level configuration rewrite, can set Http request parameters, etc.
//                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
//                .build();
//
//        // Asynchronously get the return value of the API request
//        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
//        // Synchronously get the return value of the API request
//        SendSmsResponse resp = null;
//        try {
//            resp = response.get();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(new Gson().toJson(resp));
//
//        client.close();
//    }

}