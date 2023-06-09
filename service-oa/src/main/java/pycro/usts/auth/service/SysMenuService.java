package pycro.usts.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pycro.usts.model.system.SysMenu;
import pycro.usts.vo.system.AssignMenuVo;
import pycro.usts.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-16
 */
public interface SysMenuService extends IService<SysMenu> {

    // 菜单列表
    List<SysMenu> findNodes();

    // 递归删除
    void removeMenuById(Long id);

    // 查询所有菜单和角色分配的菜单
    List<SysMenu> findMenuByRoleId(Long roleId);

    // 角色分配菜单
    void doAssign(AssignMenuVo assignMenuVo);

    // 根据用户id获取用户可以操作菜单列表
    List<RouterVo> findMenuListByUserId(Long userId);

    // 根据用户id获取用户可以操作按钮列表
    List<String> findPermsByUserId(Long userId);
}
