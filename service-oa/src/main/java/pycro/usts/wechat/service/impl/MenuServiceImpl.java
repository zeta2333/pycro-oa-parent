package pycro.usts.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pycro.usts.model.wechat.Menu;
import pycro.usts.vo.wechat.MenuVo;
import pycro.usts.wechat.mapper.MenuMapper;
import pycro.usts.wechat.service.MenuService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author Pycro
 * @since 2023-05-29
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private WxMpService wxMpService;

    // 获取全部菜单
    @Override
    public List<MenuVo> findMenuInfo() {
        // 查询所有菜单
        List<Menu> menuList = baseMapper.selectList(null);
        // vo列表,用于返回
        List<MenuVo> menuVoList = new ArrayList<>();
        // 查询所有的一级菜单
        List<Menu> primaryMenuList = menuList.stream().filter(menu -> menu.getParentId() == 0).collect(Collectors.toList());
        // 遍历所有的一级菜单
        for (Menu primaryMenu : primaryMenuList) {
            // menu ---> menuVo
            MenuVo primaryMenuVo = new MenuVo();
            BeanUtils.copyProperties(primaryMenu, primaryMenuVo);
            // 获取一级菜单下的所有二级菜单
            List<Menu> secondaryMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == primaryMenu.getId())
                    .sorted(Comparator.comparing(Menu::getSort))
                    .collect(Collectors.toList());
            // List<Menu> ---> List<MenuVo>
            List<MenuVo> children = new ArrayList<>();
            for (Menu secondaryMenu : secondaryMenuList) {
                MenuVo secondaryMenuVo = new MenuVo();
                BeanUtils.copyProperties(secondaryMenu, secondaryMenuVo);
                children.add(secondaryMenuVo);
            }
            primaryMenuVo.setChildren(children);
            menuVoList.add(primaryMenuVo);
        }
        return menuVoList;
    }

    // 同步菜单
    @Override
    public void syncMenu() {
        List<MenuVo> menuVoList = this.findMenuInfo();
        // 菜单
        JSONArray buttonList = new JSONArray();
        for (MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if (CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://ggkt1.vipgz1.91tunnel.com/#" + oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for (MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if (twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        // H5页面地址
                        view.put("url", "http://ggkt1.vipgz1.91tunnel.com/#" + twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        // 菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            // 调用方法实现创建菜单
            wxMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    // 删除菜单
    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
