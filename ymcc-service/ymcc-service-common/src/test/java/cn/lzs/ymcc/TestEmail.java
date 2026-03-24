package cn.lzs.ymcc;

import cn.lzs.ymcc.web.service.impl.MailService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author 李振生
 */
@SpringBootTest
@Slf4j
@EnableAsync
public class TestEmail {
    public static void main(String[] args) {
        System.out.println("hello");
    }
   @Autowired
    private MailService mailService;
   @Test
    public void testSendEmail(){

       mailService.sendSimpleMailAsync("3836281973@qq.com","测试邮件","测试邮件内容1");
       log.info("邮件发送成功：{}","3836281973@qq.com");
   }
}
