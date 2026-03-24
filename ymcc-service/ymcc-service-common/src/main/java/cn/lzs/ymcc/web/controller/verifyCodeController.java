package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.IMailService;
import cn.lzs.ymcc.service.VerifyCodeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 李振生
   value = "验证码相关接口")
 */

@Api(tags = "验证码相关接口")
@RestController
@RequestMapping("/verifyCode")
public class verifyCodeController {
    @Resource
    private VerifyCodeService verifyCodeService;
    @Resource
    private IMailService mailService;
    @ApiOperation("发送验证码")
    @GetMapping("/sendSmsCode/{phone}")
    public JSONResult sendSmsCode(@PathVariable("phone") String phone){
        verifyCodeService.generateCode(phone);
        return JSONResult.success();
    }
    @GetMapping("/sendEmailCode/{email}")
    public JSONResult sendEmailCode(@PathVariable("email") String email){

        mailService.sendSimpleMail(email,"验证码", "验证码为：");
        return JSONResult.success();
    }
}
