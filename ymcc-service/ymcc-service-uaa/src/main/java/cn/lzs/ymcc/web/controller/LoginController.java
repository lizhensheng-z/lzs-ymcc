package cn.lzs.ymcc.web.controller;

import cn.lzs.ymcc.DTO.LoginDTO;
import cn.lzs.ymcc.DTO.RefreshTokenDTO;
import cn.lzs.ymcc.VO.AccessTokenVo;
import cn.lzs.ymcc.service.ILoginService;
import cn.lzs.ymcc.domain.Login;
import cn.lzs.ymcc.query.LoginQuery;
import cn.lzs.ymcc.result.JSONResult;
import cn.lzs.ymcc.result.PageList;
import com.baomidou.mybatisplus.plugins.Page;
import com.sun.imageio.plugins.common.I18N;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    public ILoginService loginService;

    /**
     * 登录成功
     * @return
     */
    @RequestMapping("/success")
    @ResponseBody
    public String loginSuccess(){
        return "登录成功";
    }

    /**
     * 登录接口
     * @param loginDTO
     * @return
     */
    @RequestMapping(value = "/common", method = RequestMethod.POST)
    public JSONResult loginCommon(@RequestBody @Valid LoginDTO loginDTO){
        //登录接口，调用Service层发起登录请求，在后台拼接字符串
        AccessTokenVo accessTokenVo = loginService.loginCommon(loginDTO);

        return JSONResult.success(accessTokenVo);
       
    }

    @PostMapping("/refresh")
    public JSONResult refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO){
        AccessTokenVo accessTokenVo = loginService.refresh(refreshTokenDTO);
        return JSONResult.success(accessTokenVo);
    }


    /**
     * 保存和修改公用的
     */
    @RequestMapping(value="/registerByPhone",method= RequestMethod.POST)
    public JSONResult registerByPhone(@RequestBody Login login){
            loginService.insert(login);
            //获取到插入后数据的主键
            System.out.println("注册登录用户，生成的ID: " + login.getId());
        if (login.getId() == null) {
            return JSONResult.error("用户注册失败：无法获取用户ID");
        }
        return JSONResult.success(login.getId());
    }
    /**
    * 保存和修改公用的
    */
    @RequestMapping(value="/save",method= RequestMethod.POST)
    public JSONResult saveOrUpdate(@RequestBody Login login){
        if(login.getId()!=null){
            loginService.updateById(login);
        }else{
            loginService.insert(login);
        }
        return JSONResult.success();
    }

    /**
    * 删除对象
    */
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    public JSONResult delete(@PathVariable("id") Long id){
        loginService.deleteById(id);
        return JSONResult.success();
    }

    /**
   * 获取对象
   */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public JSONResult get(@PathVariable("id")Long id){
        return JSONResult.success(loginService.selectById(id));
    }


    /**
    * 查询所有对象
    */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public JSONResult list(){
        return JSONResult.success(loginService.selectList(null));
    }


    /**
    * 带条件分页查询数据
    */
    @RequestMapping(value = "/pagelist",method = RequestMethod.POST)
    public JSONResult page(@RequestBody LoginQuery query){
        Page<Login> page = new Page<Login>(query.getPage(),query.getRows());
        page = loginService.selectPage(page);
        return JSONResult.success(new PageList<Login>(page.getTotal(),page.getRecords()));
    }
}
