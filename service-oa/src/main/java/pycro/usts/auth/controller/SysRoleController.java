package pycro.usts.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import pycro.usts.auth.service.SysRoleService;
import pycro.usts.common.result.Result;
import pycro.usts.model.system.SysRole;
import pycro.usts.vo.system.AssignRoleVo;
import pycro.usts.vo.system.SysRoleQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 10:14 AM
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    // 注入service
    @Autowired
    private SysRoleService sysRoleService;

    // 1 查询所有角色 和 当前用户所属角色

    @ApiOperation("根据用户id获取角色")
    @GetMapping("/toAssign/{userId}")
    public Result<?> toAssign(@PathVariable("userId") Long userId) {
        Map<String, Object> map = sysRoleService.findRoleDataByUserId(userId);
        return Result.ok(map);
    }

    // 2 为用户分配角色
    @ApiOperation("为用户分配角色")
    @PostMapping("/doAssign")
    public Result<?> doAssign(@RequestBody AssignRoleVo assignRoleVo) {
        sysRoleService.doAssign(assignRoleVo);
        return Result.ok();
    }

    // 获取所有记录
    /* @GetMapping("/findAll")
    public List<SysRole> findAll() {
        List<SysRole> list = sysRoleService.list();
        return list;
    } */
    // 统一返回结果
    @ApiOperation("查询所有角色")
    @GetMapping("/findAll")
    public Result<?> findAll() {
        // try {
        //     int i = 1 / 0;
        // } catch (Exception e) {
        //     throw new PycroException(22222, "customized exception");
        // }
        List<SysRole> list = sysRoleService.list();
        return Result.ok(list);
    }

    /**
     * @param page           当前页
     * @param limit          每页显示记录数
     * @param sysRoleQueryVo 条件对象
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("条件分页查询")
    @GetMapping("/{current}/{size}")
    public Result<?> findPage(
            @PathVariable("current") Long page,
            @PathVariable("size") Long limit,
            SysRoleQueryVo sysRoleQueryVo
    ) {
        // 1 创建page对象，传递分页相关参数
        Page<SysRole> pageParam = new Page<>(page, limit);
        // 2 封装条件，判断条件是否为空，不为空则进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {// 条件不为空则封装
            // 模糊查询,正则匹配roleName
            wrapper.like(SysRole::getRoleName, roleName);
        }
        // 3 调用方法实现
        IPage<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    /**
     * 添加角色
     *
     * @param sysRole
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("/save")
    public Result<?> save(@RequestBody SysRole sysRole) {
        boolean isSuccess = sysRoleService.save(sysRole);
        if (isSuccess) {
            return Result.ok().message("添加成功");
        } else {
            return Result.fail();
        }
    }

    /**
     * 修改角色-根据id查询角色
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id查询")
    @GetMapping("/get/{id}")
    public Result<?> getById(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.ok(sysRole);
    }

    /**
     * 修改角色-最终修改
     *
     * @param sysRole
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result<?> update(@RequestBody SysRole sysRole) {
        boolean isSuccess = sysRoleService.updateById(sysRole);
        if (isSuccess) {
            return Result.ok().message("修改成功");
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据id删除
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除")
    @DeleteMapping("/remove/{id}")
    public Result<?> removeById(@PathVariable Long id) {
        boolean isSuccess = sysRoleService.removeById(id);
        if (isSuccess) {
            return Result.ok().message("删除成功");
        } else {
            return Result.fail();
        }
    }

    /**
     * 根据id列表批量删除
     *
     * @param idList
     * @return
     */
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("批量删除")
    @DeleteMapping("/batchRemove")
    public Result<?> batchRemove(@RequestBody List<Long> idList) {
        boolean isSuccess = sysRoleService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok().message("批量删除成功");
        } else {
            return Result.fail();
        }
    }

}
