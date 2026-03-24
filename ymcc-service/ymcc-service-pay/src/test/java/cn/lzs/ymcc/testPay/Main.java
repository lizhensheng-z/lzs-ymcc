package cn.lzs.ymcc.testPay;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.factory.Factory.Payment;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;

public class Main {
    public static void main(String[] args) throws Exception {
        // 1. 设置参数（全局只需设置一次）
        Factory.setOptions(getOptions());
        try {
            // 2. 发起API调用（以创建当面付收款二维码为例）

            AlipayTradePagePayResponse response = Payment.Page()
                    .pay("测试支付样例",
                            "20250920144435",
                            "1.00",
                            "http://www.baidu.com");
            // 3. 处理响应或异常
            if (ResponseChecker.success(response)) {
                System.out.println("调用成功");
            } else {
                System.err.println("调用失败，原因：" + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com";
        config.signType = "RSA2";
        config.appId = "9021000154653941";
        // 为避免私钥随源码泄露，推荐从文件中读取私钥字符串而不是写入源码中
        config.merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQOpDDemsgXUtb2u6rmFig5EVGwW+XQxcxLWnDKDfbN/vqaT4ctL5AQZ/gsXUHk/FazjAkonaxFylGWB2KmIEDM4VUC95UnJgC0Wilxu36aNg98vaJFmD2VrO8a7RIPIRLznc6FchxE+thzZGbXfqX2lQ6/TQIAr8JRZkdE8oaOTbKDik1KxRGG4cHo2MsU1xg8y/Z6LDXbz3sGksXTr76rdVmGyokWeCH6nAdOaDj1SsrwOzjum4deD4LdWANV46GDy6knwqobalrQTspxE+ZfaLjngJ4cHnU16SRG/oy3ML8Lg+/U+KVluWBD3LC1aEtvfSiJ9ThjGJIcjmmAFs3AgMBAAECggEBAIZIoOP/pKIbesBpFHJbg5k5L27zRsyUwJEe8Sus1GScuUXE+vmLJt94ujxfvNBzY/Hhq11QjiXPpLRD8i1KVEQAwhBAhJ5OthFZm49NqeDNoRf8kKGOXnS8wNebh90By0MxVjyUb20o2KzU0s7PLzQXKPlLKhiTRf6B+E7kT/UdC+r77JUBZOsK+FftTiMD396cmoFYR7CpZvbrpuKLGfi+NivEHY2dlNaLguurXrfdHzpECJEO3Tzwekkh+9plQPCUuxWmQi3VSpccT7z1L6divqvspnZJLvys4gUaF4piMKAIwhm+pCXUmAK3X5huK+deHLkUM6+LVWyZLd4PFUECgYEA1riitsIHM7zIDq2I/haiiksBu122z1VoAdldNsKqWB6ENsWjPT6DVyX4EcCBN9QMbdV4sKckoLqFWc5aE90QRw++R36Guw2i4YgCBsFfDpLVcjzKXoqIYyMiqpxiqBochu4bX6/prq3bOcE/SBbxq0fjhjQGa+VN7rtkm8ZY7TECgYEAq/SwkLZLC+QGRHVd6Ici82vFu7/0/d+zT03O8/dCWeeuvzNeL0vxZOxe/e2hkY0hCqMfENNbP7bZ7FzfqtIAU/LZBchalWIwWggN5VWasYm/VQPrQDt5Y8Hkxt0JJk+FF0BSGQMnDOB4x13o8aONAw6Mo2oGAIKuA3tFK9xClOcCgYBHxyj9lxb5QJ5P6crlxYNIX5KQo988p/xAwfzbFXAETQ89kAvlqUQWmea3aHbJxuTwwm+5/eXMpQ122/hYs4mBd83P8Ifxd7l/Uwv6XiH8qTEZIyEzQXQeT9yC1AxWzYPDx0z3S8L+Vbf3MpaI7sKS01FHx5Rt9RV8PMCBImkPwQKBgGnwPjdpl6KjwRi8FjcDyoydgRzCjnU6NIZPMYksdDxfeFFjnEsXRoke9wcKN9gsUobzKFh9XmJKjMgKD+J1rq4Jl5rwbJ5L9+3/uE/qBXwLmU0PNQhFm+vRsNizpMdthllkzy+Jua09PivVt4XndSDE/fBPiZMW745Fi8AR2xNRAoGAV1Md7kNjP/udb+RT0tU8a0P56Urha8dpZ0Q6NQ/exC3sTHlPqFmLpcEa+tMTFUCkf4nketeZbbaqmfZMhBtgZmhcNj6sfmyP48AQJY5HWJVOe9yhfkeY4dlT9xtzUj8zx4D4eHOBuhEkmIOrFHkceMrccqS0ArXlPboY2XlwCBc=";
        //注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，优先从文件系统中加载，加载失败后会继续尝试从CLASS_PATH中加载

        //注：如果采用非证书模式，则无需赋值上面的三个证书路径，改为赋值如下的支付宝公钥字符串即可
        config.alipayPublicKey = "";
        //可设置异步通知接收服务地址（可选）
        config.notifyUrl = "http://www.baidu.com";
        //可设置AES密钥，调用AES加解密相关接口时需要（可选）
//        config.encryptKey = "<-- 请填写您的AES密钥，例如：aa4BtZ4tspm2wnXLb1ThQA== -->";
        return config;
    }
}