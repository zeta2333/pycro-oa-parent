package pycro.usts.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.system.SysRole;
import pycro.usts.vo.system.AssignRoleVo;

import java.util.Map;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 10:00 AM
 */
public interface SysRoleService extends IService<SysRole> {
    // 1 查询所有角色 和 当前用户所属角色
    Map<String, Object> findRoleDataByUserId(Long userId);

    // 2 为用户分配角色
    void doAssign(AssignRoleVo assignRoleVo);
}
