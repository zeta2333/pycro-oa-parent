package pycro.usts.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pycro.usts.auth.mapper.SysRoleMapper;
import pycro.usts.auth.service.SysRoleService;
import pycro.usts.auth.service.SysUserRoleService;
import pycro.usts.model.system.SysRole;
import pycro.usts.model.system.SysUserRole;
import pycro.usts.vo.system.AssignRoleVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 10:00 AM
 */
@Service
public class SysRoleServiceImpl
        extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    @Autowired
    SysUserRoleService sysUserRoleService;

    // 1 查询所有角色 和 当前用户所属角色
    @Override
    public Map<String, Object> findRoleDataByUserId(Long userId) {
        // 1 查询所有角色，返回List集合，返回
        List<SysRole> allRoleList = baseMapper.selectList(null);
        // 2 根据userid查询角色用户关系表，查询userid对应所有角色id
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> existUserRoleList = sysUserRoleService.list(wrapper);
        List<Long> existRoleIdList = existUserRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        // 3 根据查询所有角色id，找到对应角色信息
        // 根据角色id到所有的角色的List集合进行比较
        // List<SysRole> assignRoleList = new ArrayList<>();
        // for (SysRole sysRole : allRoleList) {
        //     // 比较
        //     if (existRoleIdList.contains(sysRole.getId())){
        //         assignRoleList.add(sysRole);
        //     }
        // }
        List<SysRole> assignRoleList = allRoleList.stream()
                .filter(sysRole -> existRoleIdList.contains(sysRole.getId()))
                .collect(Collectors.toList());
        // 4 把得到两部分数据封装map集合，返回
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assignRoleList", assignRoleList);
        roleMap.put("allRoleList", allRoleList);
        return roleMap;
    }

    // 2 为用户分配角色
    @Override
    public void doAssign(AssignRoleVo assignRoleVo) {
        // 把用户之前分配角色数据删除，用户角色关系表里面，根据userid删除
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId, assignRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);
        // 重新进行分配
        List<Long> roleIdList = assignRoleVo.getRoleIdList();
        for (Long roleId : roleIdList) {
            if (StringUtils.isEmpty(roleId)) continue;
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            sysUserRoleService.save(sysUserRole);
        }

    }
}
