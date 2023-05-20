package pycro.usts.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pycro.usts.auth.mapper.SysMenuMapper;
import pycro.usts.auth.service.SysMenuService;
import pycro.usts.auth.service.SysRoleMenuService;
import pycro.usts.auth.utils.MenuHelper;
import pycro.usts.common.config.exception.PycroException;
import pycro.usts.model.system.SysMenu;
import pycro.usts.model.system.SysRoleMenu;
import pycro.usts.vo.system.AssignMenuVo;
import pycro.usts.vo.system.MetaVo;
import pycro.usts.vo.system.RouterVo;

import java.util.LinkedList;
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

    // 根据用户id获取用户可以操作菜单列表
    @Override
    public List<RouterVo> findMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList;
        // 1判断当前用户是否是管理员userId=1是管理员
        // 1.1如果是管理员，查询所有菜单列表
        if (userId == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 1.2如果不是管理员，根据userId查询可以操作菜单列表
            // 多表关联查询:用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findListByUserId(userId);
        }

        // 2把查询出来数据列表构建成框架要求的路由数据结构
        // 使用菜单操作工具类构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);
        // 构建成框架要求的路由结构
        List<RouterVo> routerList = buildRouter(sysMenuTreeList);
        return routerList;
    }

    // 构建成框架要求的路由结构
    public List<RouterVo> buildRouter(List<SysMenu> menus) {
        // 创建最终返回的路由
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(menu));
            router.setComponent(menu.getComponent());
            router.setMeta(new MetaVo(menu.getName(), menu.getIcon()));
            List<SysMenu> children = menu.getChildren();
            // 如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if (menu.getType() == 1) {
                List<SysMenu> hiddenMenuList = children
                        .stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    router.setAlwaysShow(true);
                    // 递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    // 根据用户id获取用户可以操作按钮列表
    @Override
    public List<String> findPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList;
        // 1判断当前用户是否是管理员userId=1是管理员
        // 1.1如果是管理员，查询所有菜单列表
        if (userId == 1) {
            // 查询所有菜单列表
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 1.2如果不是管理员，根据userId查询可以操作菜单列表
            // 多表关联查询:用户角色关系表、角色菜单关系表、菜单表
            sysMenuList = baseMapper.findListByUserId(userId);
        }
        // 3从查询出来的数据里面，获取可以操作按钮值的list集合，返回
        List<String> permList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
        return permList;
    }
}
