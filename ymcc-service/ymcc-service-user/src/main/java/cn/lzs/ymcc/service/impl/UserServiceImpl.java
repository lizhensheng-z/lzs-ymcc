package cn.lzs.ymcc.service.impl;

import cn.lzs.ymcc.Exception.GlobalBusinessException;
import cn.lzs.ymcc.api.LoginApi;
import cn.lzs.ymcc.constant.RedisConstants;
import cn.lzs.ymcc.constant.SystemConstants;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.domain.User;
import cn.lzs.ymcc.domain.UserAccount;
import cn.lzs.ymcc.domain.UserBaseInfo;
import cn.lzs.ymcc.dto.PhoneRegisterDTO;
import cn.lzs.ymcc.mapper.UserMapper;
import cn.lzs.ymcc.pojo.VerifyCode;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.service.IUserAccountService;
import cn.lzs.ymcc.service.IUserBaseInfoService;
import cn.lzs.ymcc.service.IUserService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 * 会员登录账号 服务实现类
 * </p>
 *
 * @author lzs
 * @since 2025-06-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Resource
    private RedisTemplate<Object,Object> redisTemplate;
    @Resource
    private LoginApi loginApi;
    @Resource
    private IUserAccountService userAccountService;
    @Resource
    private IUserBaseInfoService userBaseInfoService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserMapper userMapper;

    /**
     * 需要使用分布式事务，跨服务调用
     *  @GlobalTransactional(rollbackFor = Exception.class)
     * @param phoneRegisterDTO
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void registerByPhone(PhoneRegisterDTO phoneRegisterDTO) {
        //1参数校验 DTO层已做
        String code = phoneRegisterDTO.getSmsCode();
        String phone = phoneRegisterDTO.getMobile();
        //1.1校验手机号是否被注册
        EntityWrapper<User> userEntityWrapper = new EntityWrapper<>();
        userEntityWrapper.eq("phone",phone);
        User user = selectOne(userEntityWrapper);
        if(user != null){
            throw new GlobalBusinessException(SystemConstants.PHONE_IS_REGISTERED);
        }
        //1.2校验验证码是否过期
        VerifyCode redisCode = (VerifyCode) redisTemplate.opsForValue().get(RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX+phone);
        assert redisCode != null;
        if(redisCode.getCode() == null){
            throw new GlobalBusinessException(SystemConstants.VERIFY_CODE_IS_EXPIRED);
        }
        //1.3校验验证码是否正确
        if(!code.equals(redisCode.getCode())){
            throw new GlobalBusinessException(SystemConstants.VERIFY_CODE_IS_NOT_VALID);
        }
        //2注册用户 TODO 操作四张表
        //2.1首先调用Uaa服务完成t_login表的注册返回Login表的id
        Login login = new Login();
        login.setUsername(phone);//手机号充当默认用户名
        login.setPassword(passwordEncoder.encode(phoneRegisterDTO.getPassword()));
        login.setType(SystemConstants.REGISTER_TYPE_USER);
        // 设置OAuth2客户端信息
        login.setClientId(SystemConstants.DEFAULT_CLIENT_ID);
        login.setClientSecret(SystemConstants.DEFAULT_CLIENT_SECRET);
        // 设置账户状态
        login.setEnabled(true);
        login.setAccountNonExpired(true);
        login.setCredentialsNonExpired(true);
        login.setAccountNonLocked(true);
        JSONResult jsonResult = loginApi.registerByPhone(login);
        // 检查返回结果
        if (jsonResult == null || !jsonResult.isSuccess() || jsonResult.getData() == null) {
            throw new GlobalBusinessException("用户注册失败: " + (jsonResult != null ? jsonResult.getMessage() : "服务调用失败"));
        }
        Long login_id = Long.valueOf(jsonResult.getData().toString());

        //2.2将Login表的id和phone存入t_user表，注册t_user
        user = new User();//一定要new user 因为前面查的是null根本就没有
        user.setPhone(phoneRegisterDTO.getMobile());
        user.setCreateTime(new Date().getTime());
        user.setLoginId(login_id);
        user.setNickName(phone);
        user.setBitState(SystemConstants.USER_STATUS_Enable);
        insert(user);

        //2.3注册t_user_account，t_user_base_info
        Long userId = user.getId();
        UserAccount userAccount = new UserAccount();
        userAccount.setId(userId);
        userAccount.setCreateTime(new Date().getTime());
        userAccount.setUsableAmount(SystemConstants.USER_ACCOUNT_INIT_BALANCE);
        userAccount.setFrozenAmount(SystemConstants.USER_ACCOUNT_INIT_FREEZE_BALANCE);
        userAccountService.insert(userAccount);

        UserBaseInfo userBaseInfo = new UserBaseInfo();
        userBaseInfo.setId(userId);
        userBaseInfo.setCreateTime(new Date().getTime());
        userBaseInfo.setRegChannel(phoneRegisterDTO.getRegChannel());
        userBaseInfoService.insert(userBaseInfo);
        //删除redis存的验证码
        redisTemplate.delete(new StringBuffer(RedisConstants.REDIS_VERIFY_CODE_KEY_PREFIX).append(phone).toString());

    }

    /**
     * 根据loginId获取用户信息
     * @param loginId
     * @return
     */
    @Override
    public User getUserByLoginId(Long loginId) {
        EntityWrapper<User> userEntityWrapper = new EntityWrapper<>();
        userEntityWrapper.eq("login_id",loginId);
        return selectOne(userEntityWrapper);
    }
}
