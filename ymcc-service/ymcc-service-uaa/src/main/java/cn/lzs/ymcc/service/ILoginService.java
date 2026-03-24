package cn.lzs.ymcc.service;

import cn.lzs.ymcc.DTO.LoginDTO;
import cn.lzs.ymcc.DTO.RefreshTokenDTO;
import cn.lzs.ymcc.VO.AccessTokenVo;
import cn.lzs.ymcc.domain.Login;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 登录表 服务类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
public interface ILoginService extends IService<Login> {
    /**
     * 登录接口
     * @param loginDTO
     * @return
     */
    AccessTokenVo loginCommon(LoginDTO loginDTO);

    AccessTokenVo refresh(RefreshTokenDTO refreshTokenDTO);
}
