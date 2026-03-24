package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.DTO.LoginDTO;
import cn.lzs.ymcc.DTO.RefreshTokenDTO;
import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.VO.AccessTokenVo;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.mapper.LoginMapper;
import cn.lzs.ymcc.service.ILoginService;
import cn.lzs.ymcc.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * <p>
 * 登录表 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
@Slf4j
public class LoginServiceImpl extends ServiceImpl<LoginMapper, Login> implements ILoginService {


    private final LoginMapper loginMapper;
    @Value("${ymcc.authUrl}")
    private String authUrl;
    @Value("${ymcc.refreshUrl}")
    private String refreshUrl;

    public LoginServiceImpl(LoginMapper loginMapper) {
        this.loginMapper = loginMapper;
    }

    @Override
    public AccessTokenVo loginCommon(LoginDTO loginDTO) {
        //参数校验

        //查询用户信息
        Login login = selectOne(new EntityWrapper<Login>().eq("username", loginDTO.getUsername()));
        if(login == null){
            throw new GlobalBusinessException(SystemConstants.USER_IS_NOT_EXIST);
        }
        if(loginDTO.getType()!=login.getType()){
            throw new GlobalBusinessException(SystemConstants.USER_TYPE_IS_NOT_MATCH);
        }
        //拼接URL发起登录请求
        String url = String.format(authUrl,login.getClientId(),
                login.getClientSecret(),
                loginDTO.getUsername(),
                loginDTO.getPassword());
        //获取token
        String tokenJSON = HttpUtil.sendPost(url, null);
        //jsonString转为accessToken
        log.info("获取到Token:{}", tokenJSON);
        AccessTokenVo accessTokenVo = JSON.parseObject(tokenJSON, AccessTokenVo.class);
        //TODO 封装token过期时间，为当前时间+expiresTime
//        accessTokenVo.setExpiresTime(System.currentTimeMillis()+accessTokenVo.getExpiresTime()*1000);
        return accessTokenVo;
    }
    @Override
    public AccessTokenVo refresh(RefreshTokenDTO refreshTokenDTO) {
        //参数校验,判断该用户名是否存在
        Login login = selectOne(new EntityWrapper<Login>().eq("username", refreshTokenDTO.getUsername()));
        if(login == null){
            throw new GlobalBusinessException(SystemConstants.USER_IS_NOT_EXIST);
        }
        //拼接URL发起刷新token请求

        String url = String.format(refreshUrl,refreshTokenDTO.getRefreshToken(),
                login.getClientId(),
                login.getClientSecret());
        String refreshTokenJSON = HttpUtil.sendPost(url, null);
        //jsonString转为accessToken
        log.info("获取到Token:{}", refreshTokenJSON);
        AccessTokenVo accessTokenVo = JSON.parseObject(refreshTokenJSON, AccessTokenVo.class);
        //封装token过期时间，为当前时间+expiresTime然后返回
        accessTokenVo.setExpiresTime(System.currentTimeMillis()+accessTokenVo.getExpiresTime()*1000);

        return accessTokenVo;
    }
}
