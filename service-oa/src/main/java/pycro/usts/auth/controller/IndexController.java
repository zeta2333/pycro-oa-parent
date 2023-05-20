package pycro.usts.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pycro.usts.auth.service.SysMenuService;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.common.config.exception.PycroException;
import pycro.usts.common.jwt.JwtHelper;
import pycro.usts.common.result.Result;
import pycro.usts.common.utils.MD5;
import pycro.usts.model.system.SysUser;
import pycro.usts.vo.system.LoginVo;
import pycro.usts.vo.system.RouterVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-14 9:04 AM
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * login
     *
     * @return
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginVo loginVo) {
        // {"code":200,"data":{"token":"admin-token"}}
        /* Map<String, Object> map = new HashMap<>();
        map.put("token", "admin-token");
        return Result.ok(map); */
        // 1 获取输入的用户名和密码
        String username = loginVo.getUsername();
        String password = loginVo.getPassword();
        // 2 查询数据库
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        // 3 用户信息是否存在
        if (sysUser == null) {
            throw new PycroException(201, "用户不存在");
        }
        // 4 判断密码是否正确
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(password);
        if (!password_db.equals(password_input)) {
            throw new PycroException(201, "密码错误");
        }
        // 5 判断用户是否被禁用
        if (sysUser.getStatus() == 0) {
            throw new PycroException(201, "用户被禁用");
        }
        // 6 使用jwt根据用户id和用户名生成token字符串
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        // 7 返回
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.ok(map);
    }

    /**
     * get user info
     *
     * @return
     */
    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        // 1从请求头获取用户信息（获取请求头token字符串)
        String token = request.getHeader("token");
        // 2从token字符串获取用户id 或者用户名称
        Long userId = JwtHelper.getUserId(token);
        // 3根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = sysUserService.getById(userId);
        // 4根据用户id获取用户可以操作菜单列表
        // 查询数据库动态构建路由结构，进行显示
        List<RouterVo> routerList = sysMenuService.findMenuListByUserId(userId);
        // 5根据用户id获取用户可以操作按钮列表
        List<String> permList = sysMenuService.findPermsByUserId(userId);
        // 6返回相应的数据
        // {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}
        Map<String, Object> map = new HashMap<>();
        map.put("roles", Collections.singletonList("admin"));
        map.put("introduction", "I am a super administrator");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", sysUser.getName());
        // 路由权限列表
        map.put("routers", routerList);
        // 按钮权限列表
        map.put("buttons", permList);
        return Result.ok(map);
    }

    /**
     * logout
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        return Result.ok();
    }
}
