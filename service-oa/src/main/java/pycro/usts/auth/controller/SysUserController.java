package pycro.usts.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.common.result.Result;
import pycro.usts.common.utils.MD5;
import pycro.usts.model.system.SysUser;
import pycro.usts.vo.system.SysUserQueryVo;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Pycro
 * @since 2023-05-15
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {
    @Autowired
    private SysUserService service;

    @ApiOperation(value = "更新状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        service.updateStatus(id, status);
        return Result.ok();
    }

    @ApiOperation("条件分页查询")
    @GetMapping("/{current}/{size}")
    public Result<?> findPage(
            @PathVariable("current") Long page,
            @PathVariable("size") Long limit,
            SysUserQueryVo sysUserQueryVo
    ) {
        // 创建page对象
        Page<SysUser> pageParam = new Page<>(page, limit);
        // 封装条件，不为空则封装
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 获取条件值
        String keyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        // 判断条件值是否为空，封装条件
        if (!StringUtils.isEmpty(keyword)) {
            wrapper.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getPhone, keyword)
                    .or().like(SysUser::getName, keyword);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge(SysUser::getCreateTime, createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le(SysUser::getCreateTime, createTimeEnd);
        }
        // 调用方法实现
        IPage<SysUser> pageModel = service.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("/get/{id}")
    public Result<?> get(@PathVariable Long id) {
        SysUser user = service.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("/save")
    public Result<?> save(@RequestBody SysUser user) {
        // 对明文密码进行加密
        user.setPassword(MD5.encrypt(user.getPassword()));
        service.save(user);
        return Result.ok().message("添加成功");
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("/update")
    public Result<?> updateById(@RequestBody SysUser user) {
        service.updateById(user);
        return Result.ok().message("修改成功");
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/remove/{id}")
    public Result<?> remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok().message("删除成功");
    }
}

