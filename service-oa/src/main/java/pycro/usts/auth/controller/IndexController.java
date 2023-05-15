package pycro.usts.auth.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pycro.usts.common.result.Result;

import java.util.Collections;
import java.util.HashMap;
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
    /**
     * login
     * @return
     */
    @PostMapping("/login")
    public Result<?> login() {
        // {"code":200,"data":{"token":"admin-token"}}
        Map<String, Object> map = new HashMap<>();
        map.put("token", "admin-token");
        return Result.ok(map);
    }

    /**
     * get user info
     * @return
     */
    @GetMapping("/info")
    public Result<?> info(){
        // {"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}
        Map<String, Object> map = new HashMap<>();
        map.put("roles", Collections.singletonList("admin"));
        map.put("introduction", "I am a super administrator");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", "Pycro");
        return Result.ok(map);
    }

    /**
     * logout
     * @return
     */
    @PostMapping("/logout")
    public Result<?> logout(){
        return Result.ok();
    }
}
