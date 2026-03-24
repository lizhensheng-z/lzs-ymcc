package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李振生
 * 生成token
 */
@RestController("/common")
public class TokenController {
    @Autowired
    private ITokenService tokenService;

    @GetMapping("/createToken/{courseId}")
    public JSONResult createToken(@PathVariable("courseId") Long courseId){
        String token  = tokenService.createToken(courseId);
        return JSONResult.success(token);
    }
}
