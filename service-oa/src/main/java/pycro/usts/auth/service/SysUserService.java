package pycro.usts.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.system.SysUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-15
 */
public interface SysUserService extends IService<SysUser> {

    // 更新状态
    void updateStatus(Long id, Integer status);

    // 根据用户名查询用户
    SysUser getUserByUsername(String username);
}
