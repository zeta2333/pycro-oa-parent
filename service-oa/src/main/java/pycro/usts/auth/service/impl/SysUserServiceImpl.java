package pycro.usts.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pycro.usts.auth.mapper.SysUserMapper;
import pycro.usts.auth.service.SysUserService;
import pycro.usts.model.system.SysUser;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-15
 */
@Service
public class SysUserServiceImpl
        extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    // 更新状态
    @Override
    public void updateStatus(Long id, Integer status) {
        // 根据userid查询用户对象
        SysUser sysUser = baseMapper.selectById(id);
        // 设置修改状态值
        sysUser.setStatus(status);
        // 调用方法进行修改
        baseMapper.updateById(sysUser);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        return sysUser;
    }
}
