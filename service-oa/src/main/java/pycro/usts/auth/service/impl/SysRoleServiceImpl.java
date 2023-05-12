package pycro.usts.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pycro.usts.auth.mapper.SysRoleMapper;
import pycro.usts.auth.service.SysRoleService;
import pycro.usts.model.system.SysRole;

/**
 * @author Pycro
 * @version 1.0
 * 2023-05-12 10:00 AM
 */
@Service
public class SysRoleServiceImpl
        extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

}
