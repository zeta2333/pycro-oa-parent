package pycro.usts.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pycro.usts.auth.mapper.SysMenuMapper;
import pycro.usts.auth.service.SysMenuService;
import pycro.usts.auth.service.SysRoleMenuService;
import pycro.usts.auth.utils.MenuHelper;
import pycro.usts.common.config.exception.PycroException;
import pycro.usts.model.system.SysMenu;
import pycro.usts.model.system.SysRoleMenu;
import pycro.usts.vo.system.AssignMenuVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-16
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    // 菜单列表
    @Override
    public List<SysMenu> findNodes() {
        // 1 查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);
        // 2 构建树形结构
        /*
         * {
         *   ...
         *   children: {
         *          ...
         *      }
         * }
         */
        List<SysMenu> resultList = MenuHelper.buildTree(sysMenuList);
        return resultList;
    }

    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有下一层菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        // 查找条件：父菜单为当前菜单id的子菜单
        wrapper.eq(SysMenu::getParentId, id);
        // 查找子菜单的数量
        Integer count = baseMapper.selectCount(wrapper);
        // 含有子菜单则抛出异常
        if (count > 0) {
            throw new PycroException(201, "当前菜单包含子菜单，无法删除");
        }
        // 不含子菜单则删除
        baseMapper.deleteById(id);
    }

    // 查询所有菜单和角色分配的菜单
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 1查询所有菜单-添加条件status=1
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapper);
        // 2根据角色id roleId查询角色菜单关系表里面角色id对应所有的菜单id
        LambdaQueryWrapper<SysRoleMenu> wrapperRoleMenu = new LambdaQueryWrapper<>();
        wrapperRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperRoleMenu);
        // 3根据获取菜单id，获取对应菜单对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        // 3.1拿着菜单id 和所有菜单集合里面id进行比较，如果相同封装，将sysMenu的isSelect变为true
        // for (SysMenu sysMenu : allSysMenuList) {
        //     if (menuIdList.contains(sysMenu.getId())) {
        //         sysMenu.setSelect(true);
        //     }
        // }
        allSysMenuList.forEach(item -> {
            if (menuIdList.contains(item.getId())) {
                item.setSelect(true);
            }
        });
        // 4返回规定树形显示格式的菜单列表
        return MenuHelper.buildTree(allSysMenuList);
    }

    // 角色分配菜单
    @Override
    public void doAssign(AssignMenuVo assignMenuVo) {
        // 1根据角色id 删除菜单角色表 分配数据
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        Long curRoleId = assignMenuVo.getRoleId();
        wrapper.eq(SysRoleMenu::getRoleId, curRoleId);
        sysRoleMenuService.remove(wrapper);

        // 2从参数里面获取角色新分配菜单id列表，
        // 进行遍历，把每个id数据添加菜单角色表
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            if (StringUtils.isEmpty(menuId)) continue;

            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(curRoleId);
            sysRoleMenuService.save(sysRoleMenu);
        }
    }
}
